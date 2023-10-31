// Calling sample
// reportJira(config) 

def call(Map config) {
    jiraConfig = config.testing.reportJira
    echo "${jiraConfig}"
    catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
        for (file in findFiles(glob: jiraConfig.path)) {
            if ( file.getLength() < 6 ) {
                echo "[ INFO ] File ${file} is too small, will not be uploaded."
            } else {
                echo "[ INFO ] Uploading file ${file}."
                step([$class    : 'XrayImportBuilder',
                  serverInstance: "${jiraConfig.jiraServerId}",
                  endpointName  : "${jiraConfig.endpointName}",
                  importFilePath: "${file}"])
            }
        }
    }
}
