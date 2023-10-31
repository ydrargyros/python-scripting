def call(config){
    dir("charts") {
        sh "git config --local user.email ${config.userEmail}"
        sh "git config --local user.name ${config.userName}"
        withCredentials([usernamePassword(credentialsId: config.gitLabCredentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            sh("""git config --local credential.helper "!f() { echo username=\\$USERNAME; echo password=\\$PASSWORD; }; f" """)
        }
        helmDocs(config)
        repoTag(config)
    }
}

def repoTag(config){
    tagVersion = "${config.build.version}"
    sh "echo $tagVersion"
    sh """
        git commit --allow-empty -m "$tagVersion" 
    """
    sh "git tag $tagVersion"
    sh "git push origin ${config.deploy.chartDetails.chartRepoBranch} --tags "
}

def helmDocs(config){
    sh "git add ."
    sh 'git commit -m "add modified files"'
    sh "git push origin ${config.deploy.chartDetails.chartRepoBranch}"
}

