// Calling sample
// getEngagement(config, "secAnchore") 
import groovy.json.JsonSlurper;

def call(Map config, String secMethod) {
    ids = readJSON file : "${env.WORKSPACE}/cdp/${config.cdp.settings}/engagements.json"
    jobName = "${env.JOB_NAME}".split('/')[1]
    println "Job Name: " + jobName + " \n\n" //Added for sanity
    branch = getBranchName(config)
    println "Branch Name: " + branch + " \n\n" //Added for sanity

    return ids[jobName][branch][secMethod]
}
