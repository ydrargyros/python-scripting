// Calling sample
// dplWildFly(config) 

import java.text.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

def call(def config) {
    //sh to proddev 39 the deployment, if at least one war status is false the job will stop
    war = warList()
    // apacheControlStop()
    submap = config.deploy.WildFly
    withCredentials([usernamePassword(credentialsId: submap.jbossAdminUserID, usernameVariable: 'username', passwordVariable: 'password')]) {
        war.each { war ->
            def statusCode = sh (script: """
                "${submap.jbossCliPath}"/jboss-cli.sh -c controller=localhost:"${params.WF_Server}" --user="${env.username}" --password="${env.password}" --command="deploy ${submap.warPath}/${war} ${params.Redeploy}"
                """ , returnStatus:true)
            if (statusCode == false){
                echo 'Aborting Job.......' war 'has failed'
                currentBuild.result = 'ABORTED'
            }
            //println LocalDateTime.now()
            //sleep 30
        }
    }
    // apacheControlStart()
}

def warList() {
    //if param.value is true add it to one array and if it is empty abort the job
    enabledParameter = []
    params.each { param ->
        if(param.value == true) {
            println "${param.key} -> ${param.value}"
            enabledParameter.add(param.key)
        }
    }
    if (enabledParameter.empty) {
        echo "Aborting Job.......War parameter was no chosen"
        currentBuild.result = 'ABORTED'
    } else {
        return enabledParameter
    }
}

def apacheControlStop() {
    //take apache status and if it is active stop's after this sleep for 30 sec
    status = sh (script: """
        /usr/sbin/apachectl status | grep Active | awk \'{print \$2}\'
        """ , returnStdout: true).trim()
    println status
    if (status == 'active'){
        echo 'Stopping Apache...'
        sh 'sudo /usr/sbin/apachectl stop'
        sleep 30
    }

}
def apacheControlStart() {
    //take apache status and if it is inactive or failed start's again
    status = sh (script: """
        /usr/sbin/apachectl status | grep Active | awk \'{print \$2}\'
        """ , returnStdout: true).trim()
    println status
    if (status == 'inactive' || status == 'failed'){
        echo 'Starting Apache...'
        sh 'sudo /usr/sbin/apachectl start'
    }

}
