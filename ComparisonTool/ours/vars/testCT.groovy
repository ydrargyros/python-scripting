// Calling sample
// testCT(config) 

def call(config) {
    ctConfig = config.testing.testCT
    container('docker') {
        withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerhubCredsId, usernameVariable: 'username', passwordVariable: 'password')]) {
            passwordString = "${password}"
            sh "docker login -u ${username} -p ${passwordString}"
        }
    }
    container('maven') {
        // cts in different repo
        if ("${ctConfig.CTcommand}" == "clean install") { 
            dir("${ctConfig.CTdir}") {
                git branch: "${env.BRANCH_NAME}", credentialsId: "${config.gitlab.creds}", url: "${ctConfig.CTRepo}"
            }
        }

        try {
            withCredentials([usernamePassword(credentialsId: config.build.dockerRegistry.dockerhubCredsId, usernameVariable: 'username', passwordVariable: 'password')]) {
                sh "cd ${ctConfig.CTdir} && mvn ${ctConfig.CTcommand} -s ${config.build.maven.settingsPath} -T4 -U -Ddocker.username=${username} -Ddocker.password=${password}" 
            }
        } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error "CTs have failed"
        }
     }
    archiveArtifacts artifacts: '**/target/*cucumber*.json', allowEmptyArchive: true, fingerprint: true 
    cucumber fileIncludePattern: "**/target/*cucumber*.json", mergeFeaturesWithRetest: true
}
