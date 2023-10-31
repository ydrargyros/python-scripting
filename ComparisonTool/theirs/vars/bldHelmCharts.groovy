// Calling sample
// bldHelmCharts(config) 

def call(config) {
    dir("charts") {
        git branch: "${config.deploy.chartDetails.chartRepoBranch}", credentialsId: "${config.gitlab.creds}", url: "${config.deploy.chartDetails.chartRepo}"
        helmChartVersion = getHelmChartVersion(config)
        dockerImageTag = dockerize.getDockerImageTag(config)
        sh "sed -i \"s@ tag:.*@ tag: ${dockerImageTag}@g\" ${config.deploy.chartDetails.chartRepoPath}/values.yaml"
        sh "sed -i \"s@version:.*@version: ${helmChartVersion}@g\" ${config.deploy.chartDetails.chartRepoPath}/Chart.yaml"
        sh "sed -i \"s@appVersion:.*@appVersion: ${dockerImageTag}@g\" ${config.deploy.chartDetails.chartRepoPath}/Chart.yaml"
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