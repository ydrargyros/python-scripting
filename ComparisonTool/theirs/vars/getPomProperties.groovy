def call(Map config){
    try{
        config.build.maven.artifactId = readMavenPom().getArtifactId()

        if(readMavenPom().getGroupId() == null){
            println "[DEBUG] | groupId is null"
            config.build.maven.groupId = readMavenPom().getParent().getGroupId()
            println "[DEBUG] | parent.groupId is ${readMavenPom().getParent().getGroupId()}"
        }else{
            config.build.maven.groupId = readMavenPom().getGroupId()
            println "[DEBUG] | groupId is ${readMavenPom().getGroupId()}"
        }

//        if (readMavenPom().getProperties().getProperty('revision') != null) {
//            config.build.version = readMavenPom().getProperties().getProperty('revision')
//        } else {
//            config.build.version = readMavenPom().getVersion()
//        }

    }
    catch (Exception e){
        echo "Caught Exception: ${e}"
        currentBuild.result = 'FAILURE'
        error 'Cannot get pom properties'
    }
}