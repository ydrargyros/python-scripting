def call() {
    script {
        def scmTargetDir = "envs"
        env.CURRENT_STAGE_NAME = env.STAGE_NAME
        def env = params.ENVIRONMENT
        dir("${scmTargetDir}/${env}") {

            echo "COMPONENT_DOCKER_IMAGE_TAG_VERSION: " + params.COMPONENT_DOCKER_IMAGE_TAG_VERSION

            def replaceImageTag = "${params.COMPONENT_DOCKER_IMAGE_TAG_VERSION}"
            def helmValuesFile = "./values.yaml"

            sh "sed -i -e 's/\\(\\s*tag:\\s*\\).*\$/\\1${replaceImageTag}/' ${helmValuesFile}"

            sh """ cat '$helmValuesFile' """
            //////////////////////END//////////////////////
        }
    }
}