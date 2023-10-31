// Calling sample
// publishHelmCharts(config) 

def call(Map config) {
    container('ubuntu') {
        sh "apt-get update && apt-get install curl -y"
        chartsConfig = config.deploy.chartDetails
        withCredentials([usernamePassword(credentialsId: chartsConfig.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
           //writeFile(file: 'tmpHelm', text: env.password)
           try {
              helmChartVersion = bldHelmCharts.getHelmChartVersion(config)
              //sh "cd ${WORKSPACE}/charts && rm -rf .git envs"
              sh "tar czvf ${chartsConfig.chartProject}-${helmChartVersion}.tgz charts"
              sh "curl -u '${username}':'${password}' '${chartsConfig.chartUrl}/${chartsConfig.chartMuseum}/' --upload-file ${chartsConfig.chartProject}-${helmChartVersion}.tgz"            
            } catch (Exception e) {
                echo 'Exception occurred: ' + e.toString()
           }
       }
    }
}

