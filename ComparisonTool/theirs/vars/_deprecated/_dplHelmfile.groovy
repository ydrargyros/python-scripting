// Calling sample
// dplHelmfile("dev"/"stg"/"e2e"/"prf"/"hfx"/"qa", config) 

def call(String targetEnv, Map config) {
    envConfig = config.deploy.environments.get(targetEnv) // get submap of targetEnv
    sh "echo namespace is ${envConfig.namespace} and values.yaml is ${envConfig.valuesFile}"
    if (config.deploy.environments.allEnvs.containsKey("credsId")) {
        credsId = config.deploy.environments.allEnvs.credsId            // credsId of all environments
    } else {
        credsId = envConfig.credsId                              // credsId of specific environment
    }
    withCredentials([file(credentialsId: credsId, variable: 'KUBECONFIG')]) {
        sh "helmfile --namespace ${envConfig.namespace} --environment ${targetEnv} -f charts/${envConfig.valuesFile} apply"
    }//withCredentials
            
}