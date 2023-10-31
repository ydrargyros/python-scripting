def call(Map config) {
    def crons = config.build.crons
    println "crons are : ${crons}"
    if( config.build.triggers == "true" ) {
        crons.each { mapBranch, mapValue ->
            if (env.BRANCH_NAME == "${mapBranch}") {
                cron = mapValue
            }
        }
    }
    else {
        cron = ""
    }
    println "current cron is : ${cron}"
    return cron
}