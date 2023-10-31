// return env value for the GitOps pipeline
//def getBranchtoEnv(Map config) {
//    envName = null
//    config.deploy.branchToEnvMap.any { branch, enValue ->
//        if (env.BRANCH_NAME ==~ /${branch}/) {
//            envName = enValue
//        }else {
//            envName = "dev"
//        }
//    }
//    return envName
//}

//def getBranchtoEnv(Map config) {
//    envName = null
//    def branchToEnvMap = config.deploy.branchToEnvMap
//    jobName = "${env.JOB_NAME}".split('/')[-2]
//
//
//    // Iterate over each branch in branchToEnvMap
//    branchToEnvMap.each { mapBranch, mapValue ->
//        // Check if env.BRANCH_NAME matches the current branch
//        if (env.BRANCH_NAME ==~ /${mapBranch}/) {
//            // Check if jobName matches one of the specified patterns
//            mapValue.each { pattern, value ->
//                if (jobName.startsWith(pattern)) {
//                    envName = value
//                }
//            }
//        }
//    }
//    return envName
//}

def getBranchtoEnv(Map config) {
    envNames = []
    def branchToEnvMap = config.deploy.branchToEnvMap
    jobName = "${env.JOB_NAME}".split('/')[-2]
    branchName = config.build.branch
    // Iterate over each branch in branchToEnvMap
    branchToEnvMap.each { mapBranch, mapValues ->
        println "mapBranch is ${mapBranch}"
        // Check if env.BRANCH_NAME matches the current branch
        if (branchName == "${mapBranch}") {

            if (mapValues instanceof List) {
                envNames.addAll(mapValues)
            } else {
                envNames.add(mapValues)
            }

        }
    }

    println "[DEBUG] | envNames: ${envNames}"  // Print envNames array

    return envNames  // Return envNames array
}

def call(Map config) {
    choiceValues = getBranchtoEnv(config)
    jobName = "${env.JOB_NAME}".split('/')[-2]
    choiceValues.each { choiceValue ->
        try {

                build job: "deploy-${jobName}/master", wait: true, parameters: [
                        string(name: 'COMPONENT_DOCKER_IMAGE_TAG_VERSION', value: "${config.build.docker.tag}"),
                        string(name: 'ENVIRONMENT', value: "${choiceValue}")
                ]

        } catch (Exception e) {
            // do nothing or log the error message
            println "[DEBUG] | GitOps Job is not available or environment is invalid for current component: ${jobName}"
        }
    }
}

//def call(Map config) {
//    choiceValue = getBranchtoEnv(config)
//    jobName = "${env.JOB_NAME}".split('/')[-2]
//
//    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
//        build job: "deploy-${jobName}/master", wait: true, parameters: [
//                string(name: 'COMPONENT_DOCKER_IMAGE_TAG_VERSION', value: "${config.build.docker.tag}"),
//                string(name: 'ENVIRONMENT', value: "${choiceValue}")
//        ]
//    }
//}


//def call(Map config) {
//    choiceValue = getBranchtoEnv(config)
//    jobName = "${env.JOB_NAME}".split('/')[-2]
//
//    try {
//        build job: "deploy-${jobName}/master", wait: true, parameters: [
//                string(name: 'COMPONENT_DOCKER_IMAGE_TAG_VERSION', value: "${config.build.docker.tag}"),
//                string(name: 'ENVIRONMENT', value: "${choiceValue}")
//        ]
//    } catch (Exception e) {
//        // do nothing or log the error message
//        println "[DEBUG] | GitOps Job is not available for current component: ${jobName}"
//        println "[DEBUG] | Exception message: ${e.message}"
//
//    }
//}