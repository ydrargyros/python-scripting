def call(String targetEnv, Map config) {

    envConfig = config.deploy.environments.get(targetEnv)     // eg config.environments.dev submap
    sh "echo $envConfig"

    if (config.deploy.environments.allEnvs.containsKey("chartProject")) {
        chartProject = config.deploy.environments.allEnvs.chartProject     // same chart project name in all envs
    } else {
        chartProject = envConfig.chartProject                              // chart project name of specific environment
    }

    jobName = "${env.JOB_NAME}".split('/')[1]
    sh "echo $jobName"
    if (jobName.contains('be')){
        dir('charts') {
             container('helm') {
                 withCredentials([file(credentialsId: envConfig.credsId, variable: 'KUBECONFIG')]) {
                     sh "helm upgrade ${chartProject}-query --namespace ${envConfig.namespace} --install . --atomic -f ./envs/${targetEnv}/values.yaml -f ./values-query-management-deployment.yaml --set images.dockerRegistry=${config.build.dockerRegistry.harborUrl} --debug"
                } // withCredentials
            }
        }  
    } else if (jobName.contains('etl')){
        dir('etl-chart') {
                container('helm') {
                    git branch: 'master',
                        credentialsId: "jenkins_to_gitlab",
                        url: "https://gitlab.swpd/icarus/revenue-management/helmcharts/charts-revenue-management-etl.git"

                    withCredentials([file(credentialsId: envConfig.credsId, variable: 'KUBECONFIG')]) {
                        sh "helm upgrade ${chartProject}-etl --namespace ${envConfig.namespace} --install . --atomic -f ./envs/${targetEnv}/values.yaml --set images.dockerRegistry=${config.build.dockerRegistry.harborUrl} --debug"
                   } // withCredentials
               }
            }
        }
    
}
