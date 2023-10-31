// Calling sample
// sendEmails(config, 2) 

def call(Map config, Integer numOfAuthors = 2) {
    script {
        def defaultMailRecipients = '$DEFAULT_RECIPIENTS' as java.lang.Object
        def latestAuthors = " "
        for (int i = 0; i < numOfAuthors; i++) {
            latestAuthors += sh (
                script: "git log -n 1 --skip ${i} --format='%ae' | head -1",
                returnStdout: true
            ).trim()
            latestAuthors += " "
        }
        RELEASE_NOTES = sh(script: """git log --format="medium" -1 ${GIT_COMMIT}""", returnStdout: true)
        def mailRecipients = "${config.product.customMailRecipients} ${defaultMailRecipients} ${latestAuthors}" as java.lang.Object
        def emailSubject = "Project # ${currentBuild.fullProjectName} - Job # ${currentBuild.projectName} - Build # [${env.BUILD_NUMBER}] - ${currentBuild.currentResult} " as java.lang.Object
        emailext(
                subject: emailSubject,
                body: "Follow the below link ${BUILD_URL} to view the job results - Commit Details: # [${RELEASE_NOTES}]",
                to: mailRecipients
        )
    }
}
