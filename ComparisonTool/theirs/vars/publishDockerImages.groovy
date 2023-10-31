// Calling sample
// publishDockerImages(config) 

def call(Map config) {
    container('docker') {
        // push the docker image
        withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
            passwordString = "${password}"
            sh "docker login ${config.build.dockerRegistry.harborUrl} -u ${username} -p ${passwordString}"
            writeFile(file: 'tmpDocker', text: env.password)
            try {
                // login to dockerRegistry
                sh "cat ${'tmpDocker'}|docker login -u '${env.username}' --password-stdin ${config.build.dockerRegistry.harborUrl}"
                // iterate through services map, get getDockerImage
                services = config.services
                services.any { imageName, dockerfileLocation ->
                    if (fileExists("${dockerfileLocation}/Dockerfile")) {
                        dockerImage = dockerize.getDockerImage(config, imageName)
                        sh "docker push ${dockerImage}"
                    }
                }
            } catch (Exception e) {
                echo 'Exception occurred: ' + e.toString()
            }
        }
    } //container
}
