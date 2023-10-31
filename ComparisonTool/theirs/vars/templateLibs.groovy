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
            booleanParam(name: 'SKIP_PUBLISH_ARTIFACTS',
                    description: 'Skip pushing libraries to repository',
                    defaultValue: false)
            booleanParam(name: 'SKIP_SONAR_ANALYSIS',
                    description: 'Skip sonar analysis',
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
                    // sh "sleep 600"
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                    }
                }
            }

            stage("Workspace Setup") {
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                        config = getConfig(pipelineConfig) // pipeline configuration
//                    println "config is ${config}"
                        config = getBuildPlan(config) // build info
                        getGitDetails(config) // get repo info
                        println "[DEBUG] | Jenkins home is : ${env.JENKINS_HOME}"
                        println "[DEBUG] | Workspace directory is : ${WORKSPACE}"
                        println "[DEBUG] | branch name is : ${config.build.branch}"
                        println "[DEBUG] | project version is : ${config.build.version}"

                    }
                }

            }
            stage('Scan for Gitleaks') {
                when {
                    expression { getBranchName(config) ==~ /(master|develop|release)/ }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}"
                        secGitLeaks(config)
                    }
                }
            }//gitleaks

            stage("Build Artifact") {
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}" // display stage nam
                        bldRepo(config)
                    }
                }
            }

            stage('Dependency Track') {
                when {
                    expression { "${config.build.branch}" ==~ /(develop|release*|master|hotfix)/ }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}"
                        secDTrack(config)
                    }
                }
            }//dtrack

            stage('SonarQube analysis') {
                when {
                    allOf {
                        expression { "${config.build.branch}" ==~ /(develop|master|release)/ }
                        expression { params.SKIP_SONAR_ANALYSIS == false }
                    }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}"
                        secSonar(config)
                    }
                }
            }//sonar central

            stage("Publish Artifact") {
                when {
                    allOf {
                        expression { "${config.build.branch}" ==~ /(develop|master|release)/ }
                        expression { params.SKIP_PUBLISH_ARTIFACTS == false }
                    }
                }
                steps {
                    script {
                        log.stageBanner "${STAGE_NAME}"
                        publishArtifacts(config)
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