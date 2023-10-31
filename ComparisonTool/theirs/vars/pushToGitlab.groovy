def call() {
    withCredentials([usernamePassword(credentialsId: 'jenkins.to.gitlab', usernameVariable: 'username', passwordVariable: 'password')]) {
        script {
            sh("""
                            git config --local user.name "'${env.username}'"
                            git config --local user.email "'${env.username}'@swpd.netcompany-intrasoft.com"
                            git config --local credential.helper "!f() { echo username=\\${env.username}; echo password=\\${env.password}; }; f"
                            git add -A
                            git diff-index --quiet HEAD || git commit -m "Update Tag value on values.yaml" 
                            git push -f origin HEAD:${env.BRANCH_NAME}
                        """)
        }
    }
}