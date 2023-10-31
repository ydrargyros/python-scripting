def call(Map config) {
    checkoutCDP(config) // checkout cdp repo to get scripts engagement file
//    env.WORKSPACE = pwd()
    if (fileExists("package.json")) {
        packageJSON = readJSON file: "package.json"
        packageJSONVersion = packageJSON.version
        config.build.version = packageJSONVersion
        config.build.repoType = "NPM"
        if (fileExists("pom.xml")) {
            config.build.buildType = "MAVEN"
            getVersion(config)
            getPomProperties(config)
        } else {
            config.build.buildType = "NPM"
        }
    } else if (fileExists("pom.xml")) {
        config.build.repoType = "MAVEN"
        config.build.buildType = "MAVEN"
        getVersion(config)
        getPomProperties(config)
    }
    config.build.branch = "${getBranchName(config)}"
    getSettings(config)
    getCommitId(config)
    return config
}

def getVersion(Map config) {
    if (readMavenPom().getProperties().getProperty('revision') != null) {
        config.build.version = readMavenPom().getProperties().getProperty('revision')
    } else {
        config.build.version = readMavenPom().getVersion() // eskort/arpa
    }
}

def getSettings(Map config) {

    config.build.maven.settingsId = "${config.product.productName}-settings.xml"
    configFileProvider([configFile(fileId: "${config.build.maven.settingsId}", variable: 'MAVEN_SETTINGS')]) {
        println "${MAVEN_SETTINGS}"
        sh "cp ${MAVEN_SETTINGS} ${WORKSPACE}/settings.xml && chmod 644 settings.xml && chown 1000:1000 settings.xml"
    }
    config.build.maven.settingsPath = "${WORKSPACE}/settings.xml"
}

def checkoutCDP(Map config) {
    product = config.product.productName
    // cdp repo url
    dir('cdp') {
        git branch: "${config.cdp.branch}",
        credentialsId: "${config.gitlab.creds}",
        url: config.cdp.repo.get(product)
    }
}

def getCommitId(Map config) {
    def gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    def versionNumber;
    if (gitCommit == null) {
        versionNumber = env.BUILD_NUMBER;
    } else {
        versionNumber = gitCommit.take(8);
    }
    print 'build  versions...' + versionNumber
    config.build.commitId = versionNumber
}