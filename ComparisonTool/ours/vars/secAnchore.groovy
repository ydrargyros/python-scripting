// Calling sample
// secAnchore(config, services) 

def call(Map config, def services) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        engagement  = getEngagement(config, "secAnchore") 
        container('anchore-cli') {
            dockerImageTag = dockerize.getDockerImageTag(config)
            path = sh returnStdout: true, script: "pwd"
            anchoreFile = path.trim() + "/anchore_images"

            // only one service with the job name
            if (services == null) {     
                services = [env.JOB_NAME.tokenize('/')[1]]
            } 

            for (service in services) {
                config.security.secAnchore.service = "${service}.json"
                anchoreFileLine = config.build.dockerRegistry.harborUrl + "/"  + service + ":" + dockerImageTag
                withCredentials([usernamePassword(credentialsId: config.security.secAnchore.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image add ${anchoreFileLine}"
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image wait ${anchoreFileLine}"
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} --json image vuln ${anchoreFileLine} os > ${service}.json"
                    sh "echo ${anchoreFileLine} >> anchore_images"
                }
                reportDojo(config, "secAnchore", engagement) 
            }
            anchore name: 'anchore_images', bailOnFail: false, engineRetries: '1000'
        }
    }
}