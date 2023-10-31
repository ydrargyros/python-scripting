// Calling sample
// bldMvn(config.buildPlan.maven) 

def call(Map build) {
    container('maven') {

//        if(build.repoType == 'NPM') {
//            build.maven.threads = ""
//            sh "npm config set strict-ssl false"
//            sh "yarn config set strict-ssl false -g"
//        }
        println "[DEBUG] | REPO TYPE: ${build.repoType}"
        build.maven.threads = ""
        sh "npm config set strict-ssl false"
        sh "yarn config set strict-ssl false -g"

        // Run Maven command with JUnit command (if any)
//        configFileProvider([configFile(fileId: 'compliance-settings.xml', variable: 'MAVEN_SETTINGS')]) {
//            script {
//                sh "mvn clean install -s ${MAVEN_SETTINGS}"
//            }
//        }
        sh "mvn clean install ${build.maven.threads} -s ${build.maven.settingsPath}"
        if (build.maven.skipTests == "false") {
            getJunitCommand(build)
        }
    }
}
