// Calling sample
// secDTrack(config)

import java.io.File;

def call(Map config) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        projectName = env.JOB_NAME.tokenize('/')[1]
        branch = getBranchName(config)
        config.security.secDTrack.projectName = "${projectName}-${branch}"
        
        if (config.build.repoType == "MAVEN") {
            container('maven') {
                sh "mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom \
                    -DprojectType=library \
                    -DschemaVersion=1.3 \
                    -DincludeBomSerialNumber=true \
                    -DincludeCompileScope=true \
                    -DincludeProvidedScope=true \
                    -DincludeRuntimeScope=true \
                    -DincludeSystemScope=true \
                    -DincludeTestScope=false \
                    -DincludeLicenseText=false \
                    -DoutputFormat=xml \
                    -DoutputName=bom \
                    -s  ${config.build.maven.settingsPath}"
            }
        } else if (config.build.repoType == "NPM") {
            container('npm') {
                sh "npm install -g @cyclonedx/bom@3.10.6"
                sh "mkdir -p target"
                sh "cyclonedx-bom -o ./target/bom.xml"
            }
        }
        withCredentials([string(credentialsId: config.security.secDTrack.credsId, variable: 'DT_API_KEY')]) {
            dependencyTrackPublisher artifact: 'target/bom.xml', projectName: config.security.secDTrack.projectName, projectVersion: 'latest', synchronous: true, dependencyTrackApiKey: DT_API_KEY
        }
        zip zipFile: 'bom.zip', archive: true, dir: "target/"
        echo 'Success from Dependency-Track.'
    }
}
