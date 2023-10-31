/* This one calls the following
getConfig
getBuildPlan(future)
getGitDetails
getDebugMode

and all other support methods needed for the pipeline

 */
def call (Map pipelineConfig) {
    config = getConfig(pipelineConfig)
    buildPlan = getBuildPlan(config)
    config = getConfig(buildPlan)
    getGitDetails(config)
    getDebugMode(config)
}