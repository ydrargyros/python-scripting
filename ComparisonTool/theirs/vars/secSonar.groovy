// Calling sample
// secSonar(config)

def call(Map config) {
    sonarConfig = config.security.secSonar
    branch = config.build.branch
    productPrefix = config.product.productName
    projectKey = "${productPrefix}-${config.build.maven.groupId}:${config.build.maven.artifactId}"
    projectName = "${productPrefix}-${config.build.maven.artifactId}"

    sonarProperties = "-Dsonar.projectName='${projectName}' -Dsonar.projectKey='${projectKey}' -Dsonar.branch.name='${branch}'"

    println "productPrefix is ${productPrefix}"
    println "projectKey is ${projectKey}"
    println "projectName is ${projectName}"
//    println "default sonar properties are ${sonarProperties}"

    if(config.build.repoType == 'NPM') {
        config.security.secSonar.npm_properties.each { propertyKey, propertyValue ->
            property = "-Dsonar.${propertyKey}"
//            println "property is ${property}"
            propertyKeyValue = "${property}='${propertyValue}'"
//            println "propertyKeyValue is ${propertyKeyValue}"
            sonarProperties += " ${propertyKeyValue}"
//            println " new sonarBaseCommand is ${sonarCommand}"
        }
    } else if (config.build.repoType == 'MAVEN') {
        config.security.secSonar.maven_properties.each { propertyKey, propertyValue ->
            property = "-Dsonar.${propertyKey}"
//            println "property is ${property}"
            propertyKeyValue = "${property}='${propertyValue}'"
//            println "propertyKeyValue is ${propertyKeyValue}"
            sonarProperties += " ${propertyKeyValue}"
//            println " new sonarBaseCommand is ${sonarProperties}"
        }
    }
//    println "updated sonarProperties is ${sonarProperties}"

    //sh "ls -Ral"
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        withSonarQubeEnv(config.security.secSonar.global_properties.sonarQube_server) {
            container('maven') {
                sh "mvn sonar:sonar -s ${config.build.maven.settingsPath} ${sonarProperties}"
//                sh "echo ${sonarProperties}"
            }

        }
    }
}
