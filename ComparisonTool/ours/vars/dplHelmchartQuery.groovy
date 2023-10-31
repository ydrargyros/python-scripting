// Calling sample
// dplHelmchart("dev", config) 

def call(String targetEnv, Map config) {

    envConfig = config.deploy.environments.get(targetEnv)     // eg config.environments.dev submap
    sh "echo $envConfig"

    if (config.deploy.environments.allEnvs.containsKey("chartProject")) {
        chartProject = config.deploy.environments.allEnvs.chartProject     // same chart project name in all envs
    } else {
        chartProject = envConfig.chartProject                              // chart project name of specific environment
    }

    dir('query-chart') {
        git branch: 'master',
            credentialsId: "jenkins_to_gitlab",
            url: "https://gitlab.swpd/icarus/revenue-management/helmcharts/charts-revenue-management-query.git"
        
        helmChartVersion = getHelmChartVersion(config)
        dockerImageTag = dockerize.getDockerImageTag(config)
        sh "sed -i \"s@ tag:.*@ tag: ${dockerImageTag}@g\" ${config.deploy.chartDetails.chartRepoPath}/values.yaml"
        sh "sed -i \"s@version:.*@version: ${helmChartVersion}@g\" ${config.deploy.chartDetails.chartRepoPath}/Chart.yaml"
        sh "sed -i \"s@appVersion:.*@appVersion: ${dockerImageTag}@g\" ${config.deploy.chartDetails.chartRepoPath}/Chart.yaml"    

        sh "cat Chart.yaml"

        container('helm') {
            withCredentials([file(credentialsId: envConfig.credsId, variable: 'KUBECONFIG')]) {
                sh "helm upgrade ${chartProject}-query --namespace ${envConfig.namespace} --install . --atomic -f ./envs/${targetEnv}/values.yaml --set images.dockerRegistry=${config.build.dockerRegistry.harborUrl} --debug"
           } // withCredentials
       }
   }
}

def getHelmChartVersion(config) {
    helmChartVersion = ''
    branchName = getBranchName(config)
    if (branchName == 'master') {
        helmChartVersion = "${config.build.version}"
    } else {
        helmChartVersion = "${config.build.version}-${branchName}"
    }
    return helmChartVersion
}
