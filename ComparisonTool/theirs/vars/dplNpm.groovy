def call(Map config) {
    if(config.build.buildType == "NPM") {
        container('npm') {
            sh "npm publish"
        }
    }
    else if(config.build.buildType == "MAVEN") {
        getNpmrc(config.build, 'deploy')
        if(config.build.branch == 'develop' || config.build.branch == 'release'){
            setPackageJsonVersion(config.build, 'updateWithSuffix')
        }
        dplMvn(config)
    }
}