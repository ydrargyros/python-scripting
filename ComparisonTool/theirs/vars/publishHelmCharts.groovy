// Calling sample
// publishHelmCharts(config) 

def call(Map config) {
    container('helm') {
        chartsConfig = config.deploy.chartDetails
        withCredentials([usernamePassword(credentialsId: chartsConfig.credsId, usernameVariable: 'username', passwordVariable: 'password')]) {
            writeFile(file: 'tmpHelm', text: env.password)
            try {
                // install helm push plugin to helm container, add the required repo from the chartDetails, push the chart to the repo
                sh "export HELM_EXPERIMENTAL_OCI=1 && export HELM_REPO_USERNAME=${env.username} && export HELM_REPO_PASSWORD=${env.password} && helm repo add ${chartsConfig.chartMuseum} ${chartsConfig.chartUrl} --insecure-skip-tls-verify && /root/helmplugins/helmpush charts ${chartsConfig.chartMuseum} --insecure"
            } catch (Exception e) {
                echo 'Exception occurred: ' + e.toString()
            }
        }
    }
}