def call() {
    def causes = currentBuild.getBuildCauses()
    String causesClass = causes._class[0]
    if (causesClass.contains('BranchIndexingCause')) {
        currentBuild.result = "ABORTED"
        log.warn "Scanned pipeline. Please rerun."
    }
    return causesClass.contains('BranchIndexingCause')        
}