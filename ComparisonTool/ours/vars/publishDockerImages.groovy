// Calling sample
// publishDockerImages(config) 

def call(Map config) {
    container('docker') {
        withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
            passwordString = "${password}"
            sh "docker login ${config.build.dockerRegistry.harborUrl} -u ${username} -p ${passwordString}"
            try {
                // iterate through services map, get getDockerImage
                services = config.services
                services.any { imageName, dockerfileLocation ->
                    if (fileExists("${dockerfileLocation}/Dockerfile")) {
                        tag = getTagLatest(config)
                        dockerImage = dockerize.getDockerImage(config, imageName)
                        dockerImageName = dockerize.getDockerImageName(config, imageName)
                        sh "docker push ${dockerImage}"
                        sh "docker push ${dockerImageName}:${tag}"
                    }
                }
            } catch (Exception e) {
                echo 'Exception occurred: ' + e.toString()
            }
        }
    } //container
}
