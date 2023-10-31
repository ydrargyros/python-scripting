def call (Map pipelineConfig) {

    println "this is the start of the pipeline"
    config = getConfig(pipelineConfig)
    config.build.branch = "${getBranchName(config)}"
    println "config is ${config}"

    pipeline {

        agent {
            kubernetes {
                yaml getAgent(["maven38jdk17", "jnlp", "node","docker", "gitleaks","anchore"], config)
            }
        }

        options {
            disableConcurrentBuilds()
            skipDefaultCheckout()
            buildDiscarder(logRotator(numToKeepStr: '10'))
            disableResume()
            timestamps()
            timeout(time: 1, unit: 'HOURS')
            preserveStashes(buildCount: 5)
        }

        environment {
            mvn_http_ssl_insecure_arg = "-Dmaven.wagon.http.ssl.insecure"
            CYPRESS_CACHE_FOLDER = "${WORKSPACE}/.cache"
        }

        parameters {
            booleanParam(name: 'SKIP_DOCKER_PUSH',
                    description: 'Skip pushing docker images',
                    defaultValue: false)
            booleanParam(name: 'SKIP_DEPLOY',
                    description: 'Skip deploying to env',
                    defaultValue: false)
        }

        triggers{ parameterizedCron(getCronParams(config)) }


        stages {

            stage('Checkout clean workspace') {
                when {
                    not {
                        expression {
                            ifBranchIndexing()
                        }
                    }
                }
                steps {
                    cleanWs()
                    checkout scm
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                    }
                }
            }

            stage("Workspace Setup") {
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                        config = getBuildPlan(config) // build info
                        println "[DEBUG] | Jenkins home is : ${env.JENKINS_HOME}"
                        println "[DEBUG] | Workspace directory is : ${WORKSPACE}"
                        agent = getAgent(["jnlp", "gitleaks"], config)
                        println "[DEBUG] | ${agent}"

                    }
                }
            }

            stage("Build and push images") {
                when {
                    //expression { getBranchName(config) ==~ /(master|develop|release|hotfix)/ }
                    expression { getBranchName(config) ==~ /(master|develop|release)/   }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                        dockerize(config)
                    }
                }
            }
            stage('Anchore image Scan') {
                steps {

                    script {
                        log.stageBanner "${STAGE_NAME}"
                        secAnchore(config)
                    }
                }
            }//anchore
            stage('Trigger GITOPS job') {
                when {
                    allOf {
                        expression { getBranchName(config) ==~ /(develop|release)/   }
                        expression { params.SKIP_DOCKER_PUSH == false }
                        expression { params.SKIP_DEPLOY == false }
                    }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}"
                        dplGitOpsJob(config)
                    }
                }
            }



        } // All Stages End


        post {
            always {
                echo '[ INFO ] +++++++++++++++++++++ POST: Post step always'
            }
            failure {
                echo '[ INFO ] +++++++++++++++++++++ POST: Post step failure'
            }
        }

    }  // End of Pipeline
}