// Calling sample
// bldNpm(config.buildPlan.npm)

def call(Map build, String goalPhase) {
    if (goalPhase == 'install') {
        npmrcFileTmp = "${build.npm.npmrcPublicId}"
    } else if (goalPhase == 'deploy' && build.branch == 'master' ) {
        npmrcFileTmp = "${build.npm.npmrcReleaseId}"
    } else if (goalPhase == 'deploy' && build.branch == 'develop' ) {
        npmrcFileTmp = "${build.npm.npmrcSnapshotId}"
    } else if (goalPhase == 'deploy' && build.branch == 'release' ) {
        npmrcFileTmp = "${build.npm.npmrcSnapshotId}"
    } else {
        println " NO npmrc file available"
    }
    configFileProvider([configFile(fileId: "${npmrcFileTmp}", variable: 'npmrcFile')]) {
        sh "cp ${npmrcFile} ${WORKSPACE}/.npmrc && chmod 644 .npmrc && chown 1000:1000 .npmrc"
    }
}