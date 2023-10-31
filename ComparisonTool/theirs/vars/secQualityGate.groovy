// Calling sample
// secQualityGate()

def call() {
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        timeout(time: 40, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}