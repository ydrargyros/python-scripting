// Calling sample
// secGitLeaks()

def call(Map config) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        engagement  = getEngagement(config, "secGitLeaks") 
        container('gitleaks') {
        try {
                sh "gitleaks detect --config=${env.WORKSPACE}/cdp/${config.cdp.settings}/gitleaksUpd2.toml --source=. --verbose --no-git --report-path=gitleaks-reports.json"
            } catch (Exception e) {
                sh 'cat gitleaks-reports.json'
            }
            reportDojo(config, "secGitLeaks", engagement) 
        }
    }
}
