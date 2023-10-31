// Calling sample
// publishArtifacts(config) 

// TODO -> -Dmaven.repo.local, -P options
//def call(Map config) {
//    if (config.build.repoType == 'MAVEN') {
//        container('maven') {
//
//            if (params.containsKey("M2_REPO") && "${params.M2_REPO}" != "") {
//                sh "mvn clean deploy -T${config.build.maven.threads} -DskipTests=true -s ${config.build.maven.settingsPath} -Dmaven.repo.local=${params.M2_REPO}"
//            } else {
//                sh "mvn clean deploy -T${config.build.maven.threads} -DskipTests=true -s ${config.build.maven.settingsPath}"
//            }
//
//        }
//    } else if (config.build.repoType == 'NPM') {
//        container('npm') {
//            sh "npm publish"
//        }
//    }
//}

def call(Map config) {
    if (config.build.repoType == 'MAVEN') {
        dplMvn(config);
    }
    else if (config.build.repoType == 'NPM') {
        dplNpm(config);
    }
}
