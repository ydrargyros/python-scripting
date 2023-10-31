// Calling sample
// secSonar(config)

def call(Map config) {
    branch = getBranchName(config)
    if (config.product.productName == "icarus") {
        getExclusions(config)
    }
    sonarConfig = config.security.secSonar
    // projectName
    if (config.deploy.chartDetails.containsKey("chartProject")) {
        projectName = "${config.build.dockerRegistry.harborProject}-${config.deploy.chartDetails.chartProject}"
    } else {
        productPrefix = config.product.productName
        if (config.product.productName == "eskort") {
            productPrefix = "compliance"
        }
        jobName = env.JOB_NAME.tokenize('/')[1]
        projectName = "${productPrefix}-${jobName}"
    } 
    // run command
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        withSonarQubeEnv('PdSonarQubeCommon') {
            cmd = "mvn sonar:sonar \
                    -Dsonar.host.url='${sonarConfig.sonarUrl}' \
                    -Dsonar.projectName='${projectName}' \
                    -Dsonar.branch.name='${branch}' \
                    -Dsonar.exclusions='${sonarConfig.exclusions}' \
                    -Dsonar.cpd.exclusions='${sonarConfig.cpdExclusions}' \
                    -Dsonar.coverage.exclusions='${sonarConfig.coverageExclusions}' \
                    "
            if (config.build.repoType == "MAVEN") {
                cmd += "-Dsonar.sourceEncoding='UTF-8' \
                    -Dsonar.core.codeCoveragePlugin='${sonarConfig.codeCoveragePlugin}' \
                    -Dsonar.coverage.jacoco.xmlReportPaths='${sonarConfig.xmlReportPath}' \
                    -Dsonar.junit.reportPaths='${sonarConfig.junitReportPaths}' \
                    -s ${config.build.maven.settingsPath}"
            } else if (config.build.repoType == "NPM") {
                cmd += "-Dsonar.projectKey=${sonarConfig.projectKey} \
                    -Dsonar.sources=. \
                    -Dsonar.javascript.lcov.reportPaths=${sonarConfig.jsReportPaths}"
            }
            container('maven') {
                sh "${cmd}"   
            }
        }
    }
}

def getExclusions(config) {
    dir('cdp-config') {
        git branch: 'main',
        credentialsId: "${config.gitlab.creds}",
        url: config.cdpConfig.repo
    }
    exclude = readJSON file : "${env.WORKSPACE}/cdp-config/secSonar.json"
    jobName = "${env.JOB_NAME}".split('/')[1]
    config.security.secSonar.exclusions = exclude[jobName]["exclusions"]
    config.security.secSonar.cpdExclusions = exclude[jobName]["cpdExclusions"]
    config.security.secSonar.coverageExclusions = exclude[jobName]["coverageExclusions"]
}

//  sh "${scannerHome}/bin/sonar-scanner \
//  -Dsonar.host.url=${sonarConfig.sonarUrl} \
//  -Dsonar.projectName='${projectName}' \
//  -Dsonar.projectKey=${sonarConfig.projectKey} \
//  -Dsonar.sources=. \
//  -Dsonar.branch.name='${branch}' \
//  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info \
//  -Dsonar.exclusions=dependency-check-report*"
