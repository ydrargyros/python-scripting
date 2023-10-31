// Calling sample
// dplHelmchart("dev", config) 

def call(String targetEnv, Map config) {
    envConfig = config.deploy.environments.get(targetEnv)     // eg config.environments.dev submap

    if (config.deploy.environments.allEnvs.containsKey("chartProject")) {
        chartProject = config.deploy.environments.allEnvs.chartProject     // same chart project name in all envs
    } else {
        chartProject = envConfig.chartProject                              // chart project name of specific environment
    }

    container('helm') {
        withCredentials([file(credentialsId: envConfig.credsId, variable: 'KUBECONFIG')]) {
            sh "helm upgrade ${chartProject} --namespace ${envConfig.namespace} --install charts --atomic -f charts/envs/${targetEnv}/values.yaml --set images.dockerRegistry=${config.build.dockerRegistry.harborUrl} --set images.harborProject=${config.build.dockerRegistry.harborProject} --debug"
        } // withCredentials
    }
}
