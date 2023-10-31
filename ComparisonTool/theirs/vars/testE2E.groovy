// Calling sample
// testE2E(config) 

def call(Map config, String jsonName) {
    tag = ""
    if (jsonName == "api") {
        tag = "${params.CUCUMBER_TAGS_1}"
    } else {
        tag = "${params.CUCUMBER_TAGS_2}"
    }

    container('maven') {
        sh ("""mvn clean install -Dmaven.wagon.http.ssl.insecure -Dcucumber.filter.tags='${tag}' -Dthreads=${params.THREADS} -Djava.awt.headless=false""")
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            archiveArtifacts artifacts: '**/target/*cucumber*.json', allowEmptyArchive: true, fingerprint: true 
            cucumber fileIncludePattern: "**/target/*.json", mergeFeaturesWithRetest: true, reportTitle: "${jsonName}.json"
        }
    }
}

def triggerE2EJob(Map config) {
    e2eConfig = config.testing.testE2E
    build job: "${e2eConfig.e2eJob}/${env.BRANCH_NAME}", propagate: true, wait: true, parameters: [
        booleanParam(name: 'SKIP_TRACK', value: true),
        booleanParam(name: 'SKIP_SONAR', value: false),
        booleanParam(name: 'SKIP_GATES', value: true),
    ]
}
