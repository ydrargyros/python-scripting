def call(Map config) {
    checkoutCDP(config) // checkout cdp repo to get settings file
    env.WORKSPACE = pwd()
    if (fileExists("package.json")) {
        packageJSON = readJSON file: "package.json"
        packageJSONVersion = packageJSON.version
        config.build.version = packageJSONVersion
        config.build.repoType = "NPM"
        if (fileExists("pom.xml")) {
            getVersion(config)
        }
    } else if (fileExists("pom.xml")) {
        config.build.repoType = "MAVEN"
        getVersion(config)
    }
    getSettings(config)
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
    if (params.containsKey("RELEASE_SETTINGS")) {
        config.build.maven.settingsId = "${params.RELEASE_SETTINGS}"
    } else {
        config.build.maven.settingsId = "${config.product.productName}-settings.xml"
    }
    config.build.maven.settingsPath = "${env.WORKSPACE}/cdp/${config.cdp.settings}/${config.build.maven.settingsId}" 
}

def checkoutCDP(Map config) {
    product = config.product.productName
    // cdp repo url
    dir('cdp') {
        git branch: 'master',
        credentialsId: "${config.gitlab.creds}",
        url: config.cdp.repo.get(product)
    }
}
