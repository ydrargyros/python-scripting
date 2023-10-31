// Calling sample
// dockerize(config) 
////
def call(Map config) {
    try{
        if (config.build.docker.buildMode == "jib") {
            withJib(config)
        } else if (config.build.docker.buildMode == "fabric8"){
            withFabric8(config)
        } else {
            container('docker') {
                withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerhubCredsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                    passwordString = "${password}"
                    sh "docker login -u ${username} -p ${passwordString}"
                }
                services = config.services
                services.any { imageName, dockerfileLocation ->
                    if (fileExists("${dockerfileLocation}/Dockerfile")) {
                        dockerImage = getDockerImage(config, imageName)
                        sh "docker build -t ${dockerImage} ${dockerfileLocation} --network host"
                    }
                }
            }// container
        }
    }
    catch(Exception e) {
        echo "Caught Exception: ${e}"
        throw e
        currentBuild.result = 'FAILURE'
        error 'Step dockerize has failed'
    }

}


def getDockerImage(Map config, def imageName) {
    imageTag = getDockerImageTag(config)
    dockerConfig = config.build.dockerRegistry
    dockerImage = "${dockerConfig.harborUrl}/${dockerConfig.harborProject}/${imageName.toLowerCase()}:${imageTag}"
    return dockerImage
}

// create dockerImageTag
def getDockerImageTag(Map config) {
    commit_id = config.build.commitId
    version = config.build.version
    docker_image_tag=''
    branchName= config.build.branch
    if(branchName == 'master') {
        docker_image_tag = "${version}"
    } else {
        docker_image_tag = "${version}-${commit_id}"
    }
    return docker_image_tag
}

def withJib(Map config) {
    dockerImageTag = getDockerImageTag(config)
    sh "git rev-parse --short HEAD > commitId"
    commitId = readFile('commitId').trim()
    branchName = getBranchName(config)
    projectName = env.JOB_NAME.tokenize('/')[1]

    container('maven') {
        withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
            sh(""" mvn clean deploy jib:build -DskipTests=${params.SKIP_UT} -T8 \
                    -Dimage=${config.build.dockerRegistry.harborUrl}/${config.build.dockerRegistry.harborProject}/${projectName}:${dockerImageTag} \
                    -Djib.to.auth.username=${env.username} \
                    -Djib.to.auth.password=${env.password} \
                    -Djib.to.tags=${dockerImageTag},${branchName},${commitId} \
                    -s ${config.build.maven.settingsPath}""")
        }
    }
}

def getBranchToBuildWithTagLatest(Map config) {
    def buildWithLatestTag = "false" // Default value
        config.build.docker.branchesToBuildWithTagLatest.each { k, v ->
            if (config.build.branch ==~ /${k}/) {
                buildWithLatestTag = v
//                println "buildWithLatestTag is ${k}:${buildWithLatestTag}"
//                println "key is ${k}"
            }
        }
    return buildWithLatestTag
}

def withFabric8(Map config) {
    try {
        docker_push = ""
        dockerImageTag = getDockerImageTag(config)
        dockerPushRegistryUrl = null
        branchName = "${config.build.branch}"
        config.build.docker.tag =  "${dockerImageTag}"
        buildWithTagLatest = getBranchToBuildWithTagLatest(config)
        println "buildWithTagLatest is ${buildWithTagLatest}"
        def tags = []

        if ( buildWithTagLatest == "true") {
            tags << "${branchName}-latest"
            tags << "${dockerImageTag}"
        } else {
            tags << "${dockerImageTag}"
        }
        println "Array Tag is ${tags}"
        if (params.SKIP_DOCKER_PUSH == false) {
            docker_push = "docker:push"
        } else {
            docker_push = ""
        }
        if (branchName == 'master') {
            dockerPushRegistryUrl = "${config.build.dockerRegistry.dockerPushRegistryReleasesUrl}"
        } else {
            dockerPushRegistryUrl = "${config.build.dockerRegistry.dockerPushRegistrySnapshotsUrl}"
        }

        println "[DEBUG] | Docker Push Registry is : ${dockerPushRegistryUrl}"
        println "[DEBUG] | Docker Image Tag is : ${dockerImageTag}"
        settingsPath = "${config.build.maven.settingsPath}"
        println "[DEBUG] | Settings Path is : ${settingsPath} "
//        sh "cat ${settingsPath}"
        container('maven') {
            tags.each { tag ->
                withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerRegistryCredentialsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                    script {
                        sh "#!/bin/bash \n" +
                                "set -eo pipefail; mvn docker:build ${docker_push} -s ${config.build.maven.settingsPath} -e \
                      -DskipTests \
                      -Ddocker.imagePullPolicy='Always' \
                      -Ddocker.pull.registry=${config.build.dockerRegistry.dockerRegistryUrl} \
                      -Ddocker.pull.username=${env.username} \
                      -Ddocker.pull.password='${env.password}' \
                      -Ddocker.push.registry=${dockerPushRegistryUrl} \
                      -Ddocker.push.username=${env.username} \
                      -Ddocker.push.password='${env.password}' \
                      -Ddocker.image.tag=${tag} \
                      -Ddocker.host=unix:///var/run/docker.sock \
                      -Ddocker.skip=false \
                      | tee mvn_build.log "
                    } //-Ddocker.host=unix:///var/run/docker.sock \
                }
            }
        }
    }
    catch(Exception e) {
        echo "Caught Exception: ${e}"
        throw e
        currentBuild.result = 'FAILURE'
        error 'Step dockerization with fabric8 has failed'
    }
}