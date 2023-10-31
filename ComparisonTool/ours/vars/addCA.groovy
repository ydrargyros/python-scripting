// Calling sample
// addCA(config) 

def call(Map config) {
    services = config.services
    services.any { imageName, dockerfileLocation ->
        if (fileExists("${dockerfileLocation}/Dockerfile")) {
            withCredentials([file(credentialsId: 'rootCA', variable: 'CA')]) {
                sh "cp ${CA} ${dockerfileLocation}"
            }
        }
    }
}
