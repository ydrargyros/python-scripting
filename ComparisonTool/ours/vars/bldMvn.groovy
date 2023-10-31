// Calling sample
// bldMvn(config.buildPlan.maven) 

def call(Map mavenConfig) {
    container('maven') {
        cmd = "mvn clean install -DskipTests=${params.SKIP_UT} -s ${mavenConfig.settingsPath} -T8 -Dparallel=all -DperCoreThreadCount=true -DuseUnlimitedThreads=true ${params.DEBUG ? '-X' : ''}" //added -X for debugging
        
        if (params.containsKey("M2_REPO") && "${params.M2_REPO}" != "") {
            cmd += " -Dmaven.repo.local=${params.M2_REPO}"
        } 
        sh "${cmd}"
        if (params.containsKey("SKIP_UT") && params.SKIP_UT == false) {
            junit skipPublishingChecks: true, allowEmptyResults: true, testResults: mavenConfig.testResults
        }
    }
}
