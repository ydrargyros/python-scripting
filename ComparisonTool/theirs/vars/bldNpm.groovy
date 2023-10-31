// Calling sample
// bldNpm(config.buildPlan.npm) 

def call(Map build) {
    if(build.buildType == "NPM") {
        container('npm') {
            if(build.npm.sslVerify == "false") {
                sh "npm config set strict-ssl false"
                sh "yarn config set strict-ssl false -g"
            }
//            sh "npm config set strict-ssl false"
//            sh "yarn config set strict-ssl false -g"
            sh "npm i"
            if (params.containsKey("SKIP_UT") && params.SKIP_UT == false) {
                sh "npm run test"
            }
            sh "npm run build"
            sh "npm run build-storybook"
        }
    }
    else if(build.buildType == "MAVEN") {
        getNpmrc(build, 'install')
        setPackageJsonVersion(build, 'update')
        bldMvn(build)
    }
}