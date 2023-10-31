// Calling sample
// secDTrack(config)

//import java.nio.file.Paths;
//import java.nio.file.Files;
import java.io.File;

def call(Map config) {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        dtrackConfig = config.security.secDTrack
        branch = config.build.branch
        productPrefix = config.product.productName
        projectKey = "${productPrefix}-${config.build.maven.groupId}:${config.build.maven.artifactId}"
        projectName = "${productPrefix}-${config.build.maven.artifactId}"
        repoType = "${config.build.repoType.toLowerCase()}_properties"
        
//        println "dtrackConfig is ${dtrackConfig}"
//        println "branch is ${branch}"
//        println "projectName is ${projectName}"
//        println "repoType is ${repoType}"
        
        cycloneDXBaseProperties = ""
        
        if (config.build.repoType == "MAVEN") {
            container('maven') {
               
              config.security.secDTrack.maven_properties.each { propertyKey, propertyValue ->
              property = " -D${propertyKey}"
//              println "property is ${property}"
              propertyKeyValue = "${property}=${propertyValue}"
//              println "propertyKeyValue is ${propertyKeyValue}"
              cycloneDXBaseProperties += "${propertyKeyValue}"
//              println "new cycloneDXBaseCommand is ${cycloneDXBaseProperties}"
              }
                cycloneDXBaseProperties += " -s ${config.build.maven.settingsPath}"
//            println "Final cycloneDXBaseCommand to be executed is: ${cycloneDXBaseProperties}"
            sh "mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom ${cycloneDXBaseProperties}"
           }
        } else if (config.build.repoType == "NPM") {
            def con = "${config.build.buildType.toLowerCase()}"
            container("${con}") {
                sh "npm install -g @cyclonedx/cyclonedx-npm --registry=https://registry.npmjs.org/ --short-PURLs"
                sh "mkdir -p target"
//                println "Final cycloneDXBaseCommand to be executed is: cyclonedx-bom -o ./target/bom.xml"
                sh "cyclonedx-npm --output-file ./target/bom.xml"
            }
        }
        withCredentials([string(credentialsId: config.security.secDTrack.global_properties.credsId, variable: 'DT_API_KEY')]) {
            dependencyTrackPublisher artifact: 'target/bom.xml', projectName: "${projectName}-${branch}", projectVersion: 'latest', synchronous: true, dependencyTrackApiKey: DT_API_KEY
        }
        zip zipFile: 'bom.zip', archive: true, dir: "target/"
        echo 'Success from Dependency-Track.'
    }
}
