def call(config) {
    tagLatest = ''
    branchName = getBranchName(config)
    if (branchName == 'main') {
        tagLatest = "latest-snapshot"
    } else if (branchName == 'master') {
        tagLatest = "latest-master"
    } else if (branchName == 'develop') {
        tagLatest = "latest-snapshot"
    } else if (branchName == 'release') {
        tagLatest = "latest-release"
    }
    return tagLatest
}
