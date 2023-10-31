// Calling sample
// publishArtifacts(config) 

// TODO -> -Dmaven.repo.local, -P options
def call(Map config) {
    if (config.build.repoType == 'MAVEN') {
        container('maven') {
            cmd = "mvn clean deploy -T${config.build.maven.threads} -DskipTests=true -s ${config.build.maven.settingsPath}"
            if (params.containsKey("M2_REPO") && "${params.M2_REPO}" != "") {
                cmd += " -Dmaven.repo.local=${params.M2_REPO}"
            } 
            sh "${cmd}"
        }
    } else if (config.build.repoType == 'NPM') {
        container('npm') {
            sh "npm publish"
        }
    }
}
