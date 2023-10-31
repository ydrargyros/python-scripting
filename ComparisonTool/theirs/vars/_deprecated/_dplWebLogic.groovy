// Calling sample
// dplWebLogic(config) 

import java.text.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

def call(def config) {
    failedList = []
    war = warList()
    weblogicConfig = config.deploy.WebLogic
    war.each { war ->
        echo "War being deployed is ${war}"
        dir(weblogicConfig.warPath) {
            script {
                try {
                    if (params.containsKey("DEPLOY_ALL") && params.DEPLOY_ALL == true) {
                        // get war value
                        sh """curl -s "${weblogicConfig.repoSnapshots}com/intrasoft/${war.key}/"| jq -r '.children[].uri'|tail -2| head -1 > commandResult"""
                        warValue = readFile('commandResult').trim().replace("/", ""); // remove backslash
                    }
                    else {
                        warValue = war.value
                    }
                    sh """curl -s "${weblogicConfig.repoSnapshotsApi}com/intrasoft/${war.key}/${warValue}/" | jq -r '.children[].uri'|grep war|tail -1 > commandResult"""
                    result = readFile('commandResult').trim();
                    // sh """wget ${weblogicConfig.repoSnapshots}com/intrasoft/${war.key}/${warValue}${result} -O ${weblogicConfig.warPath}${result}"""
                    sh """wget ${weblogicConfig.repoSnapshots}com/intrasoft/${war.key}/${warValue}${result} -O ${weblogicConfig.warPath}/${war.key}.war"""
                    
                    
                    withCredentials([usernamePassword(credentialsId: weblogicConfig.weblogicAdminUserID, usernameVariable: 'username', passwordVariable: 'password')]) {
                        sh "echo 'deploying to WebLogic'"
                        // sh """ docker exec -i wls-local bash -c "ls -lahtr /u01/wars/" """;
                        //Undeploy
                        echo 'Undeploy'
                        sh """ docker exec -i wls-local bash -c "java -cp /u01/oracle/wlserver/server/lib/weblogic.jar \
                                                             weblogic.Deployer -debug -remote -verbose  -name ${war.key} \
                                                             -targets AdminServer -adminurl t3://${weblogicConfig.targetServer} \
                                                             -user ${env.username} -password ${env.password} -undeploy -noexit;" """
                        //Deploy
                        try {
                            echo 'Deploy'
                            sh """ docker exec -i wls-local bash -c "java -cp /u01/oracle/wlserver/server/lib/weblogic.jar \
                                                             weblogic.Deployer -debug -stage -remote -verbose -upload -name ${war.key} \
                                                             -source /u01/wars/${war.key}.war -targets AdminServer -adminurl t3://${weblogicConfig.targetServer} \
                                                             -user ${env.username} -password ${env.password} -deploy;" """
                        } catch (Exception e){
                            echo 'Failed deployment of ${war.key}'
                            failedList.add(result)
                        }
                    }
                } catch (Exception e) {
                    echo 'Wrong artifact version'
                    failedList.add(war.key)
                    currentBuild.result = 'UNSTABLE'
                    return failedList
                }
            }
        }
    }
}


def warList() {
    def enabledWars = [:]
    if (params.DEPLOY_ALL != null && params.DEPLOY_ALL == false) {
        //if param.value is true add it to one array and if it is empty abort the job
        params.each { param ->
        // get all params except deploy_all
        if (param.key != 'DEPLOY_ALL')
        {
            if (!param.value.empty) {
                println "${param.key} -> ${param.value}"
                enabledWars << param
                sh "echo 'This is NOT empty! Adding to the list...'"
                }
            }
        }
        
    }
    else {
        params.each { param ->
            // get all war names
            if (param.key != 'DEPLOY_ALL') {
                println "${param.key}"
                enabledWars << param
                sh "echo 'This is NOT empty! Adding to the list...'"
            }
        }
    }
    if (enabledWars.isEmpty()) {
        echo "Aborting Job.......No .war file was chosen for deployment"
        currentBuild.result = 'ABORTED'
    } else {
        return enabledWars
    }
}
