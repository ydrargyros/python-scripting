// Calling sample
// secAnchore(config, services) 

//def call(Map config, def services) {
//    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
//        engagement  = getEngagement(config, "secAnchore")
//        container('anchore-cli') {
//            dockerImageTag = dockerize.getDockerImageTag(config)
//            path = sh returnStdout: true, script: "pwd"
//            anchoreFile = path.trim() + "/anchore_images"
//
//            // only one service with the job name
//            if (services == null) {
//                services = [env.JOB_NAME.tokenize('/')[1]]
//            }
//
//            for (service in services) {
//                config.security.secAnchore.service = "${service}.json"
//                anchoreFileLine = config.build.dockerRegistry.harborUrl + "/" + config.build.dockerRegistry.harborProject + "/" + service + ":" + dockerImageTag
//                withCredentials([usernamePassword(credentialsId: config.security.secAnchore.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
//                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image add ${anchoreFileLine}"
//                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image wait ${anchoreFileLine}"
//                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} --json image vuln ${anchoreFileLine} os > ${service}.json"
//                    sh "echo ${anchoreFileLine} >> anchore_images"
//                }
//                reportDojo(config, "secAnchore", engagement)
//            }
//            anchore name: 'anchore_images', bailOnFail: false, engineRetries: '1000'
//        }
//    }
//}

def call(Map config) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        engagement  = getEngagement(config, "secAnchore")
        container('anchore') {
            dockerImageTag = dockerize.getDockerImageTag(config)
            println "dockerImageTag is ${dockerImageTag}"
            //dockerNameList = sh(script: "cat mvn_build.log | grep 'Created docker-build.tar' | cut -d '[' -f3 | cut -d ':' -f1", returnStdout: true)
            //dockerNameList = sh(script: "cat mvn_build.log | grep 'Created docker-build.tar' | grep -o '\\[[^]]*\\]' | sed 's/[][]//g' | grep -E '[^:]+:[^:]+-[^:]+' | uniq", returnStdout: true)
            dockerNameList = sh(script: "grep -o 'Successfully tagged .*' mvn_build.log | cut -d ' ' -f 3- | sort | uniq", returnStdout: true)
            dockerNameList.split('\n').each { dockerName ->
               //println "dockerName is ${dockerName}"
                //dockerRegNameTagList = "${config.build.dockerRegistry.dockerRegistryUrl}" + '/' + "${dockerName}" + ':' + "${dockerImageTag}"
                dockerRegNameTagList = "${config.build.dockerRegistry.dockerRegistryUrl}" + '/' + "${dockerName}"
                //println "dockerRegNameTagList is ${dockerRegNameTagList}"
                dockerNameTag = "${dockerName}" + ':' + "${dockerImageTag}".trim()
                //println "dockerNameTag is ${dockerNameTag}"
                config.security.secAnchore.service = "${dockerName}.json"
                //println "secAnchore service is ${config.security.secAnchore.service}"
                withCredentials([usernamePassword(credentialsId: config.security.secAnchore.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image add ${dockerRegNameTagList}"
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} image wait ${dockerRegNameTagList}"
                    sh "anchore-cli --u ${env.username} --p ${env.password} --url ${config.security.secAnchore.anchoreUrl} --json image vuln ${dockerRegNameTagList} os > ${dockerName}.json"
                    sh "echo ${dockerRegNameTagList} >> anchore_images"
                }
                reportDojo(config, "secAnchore", engagement)
            }
//            anchore name: 'anchore_images', bailOnFail: false, engineRetries: '1000'
        }
    }
}
