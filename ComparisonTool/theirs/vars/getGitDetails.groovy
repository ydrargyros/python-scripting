// Calling sample
// gitDetails(config)

def call(Map config) {
    sh "git rev-parse --short HEAD > COMMIT_ID"
    COMMIT_ID = readFile('COMMIT_ID').trim()
    BUILD_TRIGGER_BY = "${currentBuild.getBuildCauses()[0].shortDescription}"
    if (config.descriptionType == 'extended') {
        PROPERTIES_FILE = "${ENV_PROPERTIES}"
        TEST_TAGS = "${CUCUMBER_TAGS}"
    }
    getDescription(config)
}

def getDescription(Map config) {
    dockerImageTag = dockerize.getDockerImageTag(config)
    currentBuild.description = "<b>Commit Id:</b> ${COMMIT_ID}" + "<br>" + "<b>Image Tag:</b> ${dockerImageTag} " + "<br>" + "<b>${BUILD_TRIGGER_BY}</b>"
    if (config.descriptionType == 'extended') {
        currentBuild.description += "<br>" + "<b>Test Tags:</b> ${TEST_TAGS}"+ "<br>" + "<b>PropertiesFile:</b> ${PROPERTIES_FILE}"
    }
}
