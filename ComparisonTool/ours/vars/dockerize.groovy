// Calling sample
// dockerize(config) 

def call(Map config, String mode = "") {
    if (mode == "jib") {
        withJib(config)
    } else {
        container('docker') {
            withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerhubCredsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                passwordString = "${password}"
                sh "docker login -u ${username} -p ${passwordString}"
            }
            tag = getTagLatest(config)
            services = config.services
            services.any { imageName, dockerfileLocation ->
                if (fileExists("${dockerfileLocation}/Dockerfile")) {
                    dockerImage = getDockerImage(config, imageName)
                    dockerImageName = getDockerImageName(config, imageName)
                    //sh "docker pull ${dockerImageName}:${tag}"
                    try {
                        sh "docker pull ${dockerImageName}:${tag}"
                    } catch (Exception e) {
                        echo "Ignoring Docker image pull error: ${e.message}"
                    }
                    
                    sh "DOCKER_BUILDKIT=0 docker build -t ${dockerImage} -t ${dockerImageName}:${tag} ${dockerfileLocation}  --cache-from ${dockerImageName}:${tag} --network host"
//                    sh "docker build -t ${dockerImage} -t ${dockerImageName}:${tag} ${dockerfileLocation} --cache-from ${dockerImageName}:${tag} --network host"
                }
            }
        }// container
    }
}

def getDockerImage(Map config, def imageName) {
    imageTag = getDockerImageTag(config)
    dockerImageName = getDockerImageName(config, imageName)
    dockerImage = "${dockerImageName}:${imageTag}"
    return dockerImage
}

def getDockerImageName(Map config, def imageName) {
    dockerConfig = config.build.dockerRegistry
    return "${dockerConfig.harborUrl}/${imageName.toLowerCase()}"
}

// create dockerImageTag
def getDockerImageTag(Map config) {
    image_tag = ''
    branchName = getBranchName(config)
    if (branchName == 'master') {
        upstream = currentBuild.rawBuild.getCause(hudson.model.Cause$UpstreamCause)
        cause = upstream?.shortDescription
        if (cause != null && cause.contains("scheduled-sec")) {
            // scheduled-sec tag
            image_tag = "${config.build.version}-sec-b${env.BUILD_NUMBER}"
        } else {
            image_tag = "${config.build.version}"
        }
    } else {
        image_tag = "${config.build.version}-${branchName}-b${env.BUILD_NUMBER}"
    }
    return image_tag
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
                    -Dimage=${config.build.dockerRegistry.harborUrl}/${projectName}:${dockerImageTag} \
                    -Djib.to.auth.username=${env.username} \
                    -Djib.to.auth.password=${env.password} \
                    -Djib.to.tags=${dockerImageTag},${branchName},${commitId} \
                    -s ${config.build.maven.settingsPath}""")
        }
    }
}

