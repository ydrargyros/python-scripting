// Calling sample
// reportDojo(config, "secGitLeaks", 1) 

def call(Map config, String calling_func, String engagement) {
    dojoConfig = config.security.reportDojo
    dockerImageTag = dockerize.getDockerImageTag(config)
    // removed -F "scan_date=$currentTime" \
    //currentTime = sh(returnStdout: true, script: 'date +%Y-%m-%d').trim()

    withCredentials([string(credentialsId: "${dojoConfig.credsId}", variable: 'DEFECT_DOJO_TOKEN')]) {
        println "Welcome, URL: ${dojoConfig.dojoUrl}, Token: ${DEFECT_DOJO_TOKEN}, Service: ${config.security.get(calling_func).service}"

        container('maven') {
            cmd = """curl --insecure -X POST "${dojoConfig.dojoUrl}" \
                    -H "Authorization: Token ${DEFECT_DOJO_TOKEN}" \
                    -F "engagement=${engagement}" \
                    -F "version=${dockerImageTag}" \
                    -F "verified=${dojoConfig.verify}" \
                    -F "active=${dojoConfig.active}" \
                    -F "scan_type=${config.security.get(calling_func).scanType}" \
                    -F "minimum_severity=Info" \
                    -F "skip_duplicates=${dojoConfig.skipDupes}" \
                    -F "close_old_findings=${dojoConfig.closeOld}" \
                    -F "file=@${config.security.get(calling_func).service}" """  

            if ("${calling_func}" == "secAnchore")  {
                anchoreConfig = config.security.secAnchore.service
                service = anchoreConfig.tokenize('.')[0]
                cmd += """ -F "service=${service}" """
            }
            sh "${cmd}"
        } 
    }
}
