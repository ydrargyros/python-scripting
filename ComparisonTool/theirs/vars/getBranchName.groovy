// Calling sample
// getBranchName(config) 

//  return branch name template trim numbers or strings
def call(Map config) {
    branchName = null
    config.branches.any { branchPattern, branch ->
        if (env.BRANCH_NAME ==~ /${branchPattern}/) {
            branchName = branch
        }
    }
    return branchName
}