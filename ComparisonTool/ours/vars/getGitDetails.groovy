// Calling sample
// gitDetails(config)

def call(Map config) {
    sh "git rev-parse --short HEAD > COMMIT_ID"
    COMMIT_ID = readFile('COMMIT_ID').trim()
    BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}"
    //if (config.descriptionType == 'qa-testing') {
    //    //PROPERTIES_FILE = "${ENV_PROPERTIES}"
    //    TEST_TAGS1 = "${params.CUCUMBER_TAGS_1}"
    //    TEST_TAGS2 = "${params.CUCUMBER_TAGS_2}"
    //}
    getDescription(config)
}

def getDescription(Map config) {
    dockerImageTag = dockerize.getDockerImageTag(config)
    if (config.descriptionType == 'qa-testing') {
        currentBuild.description = "<b>TARGET ENV:</b> ${params.TARGET_ENV} <br> <b>Test Tags:</b> ${params.CUCUMBER_TAGS_1} , ${params.CUCUMBER_TAGS_2}" +  "<br>" + "<b>Threads:</b> ${params.THREADS}" + "<br>" + "<b>Commit Id:</b> ${COMMIT_ID} , <b>${BUILD_TRIGGER_BY}</b>"
    }
    else if (config.descriptionType == 'backup-restore'){
        currentBuild.description = "<b>TARGET ENV:</b> ${params.TARGET_ENV}" + "<br>" + "<b>OPERATION:</b> ${params.OPERATION}" + "<br>" + "<b>Commit Id:</b> ${COMMIT_ID} , <b>${BUILD_TRIGGER_BY}</b>"
    }else if (config.descriptionType == 'performance-testing') {
        currentBuild.description = "<b>TARGET:</b> ${params.TARGET}" + "<br>" + "<b>TARGET ENV:</b> ${params.TARGET_ENV} , <b>RPS:</b> ${params.RPS} , <b>SCALE:</b> ${params.SCALE} , <b>HOLD:</b> ${params.HOLD} <br>" + "<b>Commit Id:</b> ${COMMIT_ID} , "  + "<b>${BUILD_TRIGGER_BY}</b>"
    }else{
        currentBuild.description ="<b>Commit Id:</b> ${COMMIT_ID}" + "<br>" + "<b>Image Tag:</b> ${dockerImageTag} " + "<br>" + "<b>${BUILD_TRIGGER_BY}</b>"
    }
}
//+ "<b>PropertiesFile:</b> ${PROPERTIES_FILE}" 
