def call(Map config) {
    container('maven') {
        if(config.build.repoType == 'NPM') {
            config.build.maven.threads = ""
        }
            sh "mvn deploy ${config.build.maven.threads} -DskipTests=True -s ${config.build.maven.settingsPath}"
    }
}