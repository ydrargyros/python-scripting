// Calling sample
// getEngagement(config, "secAnchore") 
import groovy.json.JsonSlurper;

//def call(Map config, String secMethod) {
//    ids = readJSON file : "${env.WORKSPACE}/cdp/${config.cdp.settings}/engagements.json"
//    println "[DEBUG] | ids from getengament is ${ids}"
//    jobName = "${env.JOB_NAME}".split('/')[1]
//    branch = getBranchName(config)
//    return ids[jobName][branch][secMethod]
//}


def call(Map config, String secMethod) {
    try {
        ids = readJSON file: "${env.WORKSPACE}/cdp/${config.cdp.settings}/engagements.json"
//        println "[DEBUG] | ids from getengament is ${ids}"
        jobName = "${env.JOB_NAME}".split('/')[-2]
//        jobName1 = "${env.JOB_NAME}".split('/')[1]
//        jobName2 = "${env.JOB_NAME}"
        branch = getBranchName(config)
//        println "[DEBUG] | branch is ${branch}"
//        println "[DEBUG] | jobname is ${jobName}"
//        println "[DEBUG] | jobname1 is ${jobName1}"
//        println "[DEBUG] | jobname2 is ${jobName2}"
//        println "[DEBUG] | secMethod is ${secMethod}"
        return ids[jobName][branch][secMethod]
    } catch (Exception e) {
        println("[ERROR] | An error occurred while executing the function: ${e.message}")
        return null
    }
}
