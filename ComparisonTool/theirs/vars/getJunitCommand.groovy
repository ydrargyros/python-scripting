def call(Map mavenConfig) {
//    map is config.build
    try {
        repoType = mavenConfig.repoType.toLowerCase()

        junitCommand = "junit skipPublishingChecks: true, allowEmptyResults: true, testResults: mavenConfig.${repoType}.testResults"
        println "junitCommand is ${junitCommand}"
        junit skipPublishingChecks: true, allowEmptyResults: true, testResults: mavenConfig."${repoType}".testResults
//        return junitCommand

    } catch (Exception e) {
        error("Failed to generate JUnit command: ${e.message}")
    }
}
