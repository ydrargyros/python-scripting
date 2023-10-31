// Calling sample
// bldNpm(config.buildPlan.npm) 

def call(Map npmConfig) {
    container('npm') {
        //sh "npm cache clean --force"
        //sh "npm install -g npm@latest"
        sh "npm i"
        if (params.containsKey("SKIP_UT") && params.SKIP_UT == false) {
            sh "npm run test"
        }
        sh "npm run build"
        sh "npm run build-storybook"
    }
}
