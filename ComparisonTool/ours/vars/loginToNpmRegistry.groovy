// Calling sample
// loginToNpmRegistry(config) 

def call(Map config) {
    container('npm') {
        registryUrl = config.build.npm.registryUrl
        sh "npm config set registry=${registryUrl}"                
        withCredentials([usernamePassword(credentialsId: "${config.build.npm.credsId}", usernameVariable: 'username', passwordVariable: 'password')]) {
            sh 'npm config set _auth="$(echo -n "$username:$password" | base64)"'
        }
    }
}
