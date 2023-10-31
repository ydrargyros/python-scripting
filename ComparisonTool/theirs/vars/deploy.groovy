// Calling sample
// deploy(config, "dplHelmchart"/"dplHelmfile"/"dplManifest"/"dplWebLogic"/"dplWildFly")

def call(Map config, String deployType) {
    if (params.containsKey("WF_Server")) { // call e.g. dplWebLogic/dplWildFly
        "$deployType"(config)
        return
    }
    if (params.containsKey("SKIP_DEV_DEPLOY") && params.SKIP_DEV_DEPLOY == false) {
        "$deployType"("dev", config)          // call e.g dplHelmchart/dplHelmfile/dplManifest
    }
    if (params.containsKey("SKIP_E2E_DEPLOY") && params.SKIP_E2E_DEPLOY == false) {
        "$deployType"("e2e", config)          
    }
    if (params.containsKey("SKIP_E2E_TESTS") && params.SKIP_E2E_TESTS == false) {
        testE2E.triggerE2EJob(config);
    }
    if (params.containsKey("SKIP_STG_DEPLOY") && params.SKIP_STG_DEPLOY == false) {
        "$deployType"("stg", config)
    }
    if (params.containsKey("SKIP_QA_DEPLOY") && params.SKIP_QA_DEPLOY == false) {
        "$deployType"("qa", config)
    }
    if (params.containsKey("SKIP_PRF_DEPLOY") && params.SKIP_PRF_DEPLOY == false) {
        "$deployType"("prf", config)
    }
    if (params.containsKey("SKIP_HFX_DEPLOY") && params.SKIP_HFX_DEPLOY == false) {
        "$deployType"("hfx", config)
    }
    if (!params.containsKey("SKIP_DEV_DEPLOY")) { // no skip param, then deploy on dev
        "$deployType"("dev", config)
    }
}
