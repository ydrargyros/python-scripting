// Calling sample
// dockerLimits(config) 

def call(Map config) {
    log.info("Check Docker Limits")
        container("ubuntu") {
            withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerhubCredsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                passwordString = "${password}"
                sh '''#!/bin/bash
                apt-get update > /dev/null && apt-get install jq -y > /dev/null;
                apt-get update > /dev/null && apt-get install curl -y > /dev/null;
                TOKEN=$(curl --user '${username}${passwordString}' "https://auth.docker.io/token?service=registry.docker.io&//scope=repository:ratelimitpreview/test:pull" | jq -r .token);
                curl --head -H "Authorization: Bearer $TOKEN" https://registry-1.docker.io/v2/ratelimitpreview/test/manifests/latest 2>&1 | grep //ratelimit;
            '''
            }  

        }
}
