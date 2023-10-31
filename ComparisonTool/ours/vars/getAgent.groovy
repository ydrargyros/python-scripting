// Calling sample
//getAgent(config)
import groovy.transform.Field

@Field
Map defaults = [
  agent:[
          jnlp :[
                  "image" : "jenkins/inbound-agent:4.11-1-jdk11",
                  resources:[
                          //"cpuRequests":"100m",
                          //"memoryRequests":"256Mi",
                          //"cpuLimits":"2000m",
                          //"memoryLimits":"2024Mi",
                          "ephemeralRequests":"3000Mi"
                          //"ephemeralLimits":"3500Mi"
                  ]
          ],
          curl :[
          "image" : "alpine/curl:3.14",
//          resources:[
                  //"cpuRequests":"100m",
                  //"memoryRequests":"256Mi",
                  //"cpuLimits":"2000m",
                  //"memoryLimits":"2024Mi",
//                  "ephemeralRequests":"3000Mi"
                  //"ephemeralLimits":"3500Mi"
//          ]
  ],
      maven395ibmsemeru17focal:[
        //"image":"maven:3.8.4-ibm-semeru-17-focal",
        "image":"maven:3.9.5-ibm-semeru-17-focal",
        resources:[
            "cpuRequests":"7500m",
            "memoryRequests":"11500Mi",
            "cpuLimits":"8000m",
            "memoryLimits":"12000Mi",
            //"cpuLimits":"12000m",
            //"memoryLimits":"18000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven395ibmsemeru17focalPRF:[
        "image":"maven:3.9.5-ibm-semeru-17-focal",
        resources:[
            "cpuRequests":"11500m",
            "memoryRequests":"17500Mi",
            //"cpuLimits":"8000m",
            //"memoryLimits":"12000Mi",
            "cpuLimits":"12000m",
            "memoryLimits":"18000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven383openjdk11slim:[
        "image":"maven:3.8.3-openjdk-11-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"8000m",
            "memoryLimits":"12000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven363jdk11slim:[
        "image":"maven:3.6.3-jdk-11-slim",
        resources:[
            "cpuRequests":"7500m",
            "memoryRequests":"13500Mi",
            "cpuLimits":"8000m",
            "memoryLimits":"14000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven383jdk8slim:[
        "image":"maven:3.8.3-jdk-8-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"2000m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven381openjdk11slim:[
        "image":"maven:3.8.1-openjdk-11-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"2000m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven363openjdk8slim:[
        "image":"maven:3.6.3-openjdk-8-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"2000m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      docker:[
        "image":"docker:24.0.5",
//        "image":"docker:19.03.12",
        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      helm:[
        "image":"lachlanevenson/k8s-helm:v3.3.1",
//        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"500Mi"
//        ]
      ],
      anchore:[
        "image":"anchore/engine-cli:v0.9.1",
        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      gitleaks:[
        "image":"zricethezav/gitleaks:v8.8.6",
        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      npm:[
        "image":"node:14.13.0-alpine",
//        resources:[
//            "cpuRequests":"200m",
//           "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
//            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
//        ]
      ],
      npm16:[
        "image":"node:16.15.0",
//        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
//            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
//        ]
      ],
      npm18:[
        "image":"node:18.10-alpine",
//        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
//            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
//        ]
      ],
      ubuntu:[
        "image":"ubuntu:20.04",
//        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
//            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
//        ]
      ]
  ]
]

def call(List images, Map pipelineConfig) {
    config = getConfig.merge(defaults, pipelineConfig)
    podYaml = getYamlHead()
    images.each { image ->
      // e.g. npm18, npm16 images -> npm container, same specs
      def matcher = image =~ /\d/
      def index = matcher ? matcher.start() : image.length()
      def containerType = image.substring(0, index)
      // remove the above lines if you want different specs for each image version
      func = "$containerType" + "_AgentSpec" // eg mavenAgentSpec
      imagesSubmap = config.agent.get(image)
      podYaml += "$func"(imagesSubmap) // call function with corresponding submap eg maven386jdk11_AgentSpec(config.agent.maven)
      if (imagesSubmap.containsKey("resources")) {
        podYaml += getResources(imagesSubmap)
      }
    }
    podYaml += getYamlTail(images)
    return podYaml
}

def getYamlHead() {
    """apiVersion: v1
kind: Pod
metadata:
spec:
  hostAliases:
  - ip: 10.240.36.105
    hostnames:
    - nexus.swad-alm.intrasoft-intl.com
  - ip: 10.240.36.172
    hostnames:
    - irme2e01
  containers:
"""
}

def getYamlTail(images) {
  tail = """  imagePullSecrets:
    - name: dockerhub-creds
  volumes:"""

  def isMaven = images.any { it.contains("maven") }
  if (isMaven) {
    tail += """  
    - name: m2-repo
      persistentVolumeClaim:
        claimName: maven-repo"""
  }


  def isHelm = images.any { it.contains("helm") }
  if (isHelm) {
    tail += """    
    - name: helmplugins
      hostPath:
        path: /jenkinsfs/devops/helm/plugins"""
  }

  def isDocker = images.any { it.contains("docker") }
  if (isDocker) {
    tail += """    
    - name: dockersock
      hostPath:
        path: /var/run/docker.sock
    - name: registry-pvc
      persistentVolumeClaim:
        claimName: registry-pvc"""
  }

  def isNpm = images.any { it.contains("npm") }
  if (isNpm) {
    tail += """    
    - name: nodejs
      hostPath:
        path: /jenkinsfs/devops/nodejs"""
  }

  def isCurl = images.any { it.contains("curl") }
  if (isCurl) {
      tail += """    
    - name: curl"""
    }

  tail += """    
    - name: workspace-volume
      emptyDir:
        medium: ''"""

  return tail
}

def getResources(Map containerConfig) {
    return     """    resources:
      requests:
        memory: ${containerConfig.resources.memoryRequests}
        cpu: ${containerConfig.resources.cpuRequests}
        ephemeral-storage: ${containerConfig.resources.ephemeralRequests}
      limits:
        memory: ${containerConfig.resources.memoryLimits}
        cpu: ${containerConfig.resources.cpuLimits}
        #ephemeral-storage: ${containerConfig.resources.ephemeralLimits}
"""
}

def jnlp_AgentSpec(Map containerConfig) {
    """  - name: jnlp
    image: ${containerConfig.image}
    args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
"""
}

def maven_AgentSpec(Map containerConfig) {
    """  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command:
      - cat
    tty: true
    securityContext:
      privileged: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2/repository
      - name: dockersock
        mountPath: /var/run/docker.sock
      - name: registry-pvc
        mountPath: /var/lib/registry
  
"""
}

def ubuntu_AgentSpec(Map containerConfig) {
    """  - name: ubuntu
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command: ["/bin/sleep", "3650d"]
"""
}

def npm_AgentSpec(Map containerConfig) {
    """  - name: npm
    image: ${containerConfig.image}
    command:
      - cat
    tty: true
    volumeMounts:
      - name: nodejs
        mountPath: /root/.npm
"""
}

def curl_AgentSpec(Map containerConfig) {
    """  - name: curl
    image: ${containerConfig.image}
    command:
      - cat
    tty: true
"""
}

def docker_AgentSpec(Map containerConfig) {
    """  - name: docker
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command:
      - cat
    tty: true
    volumeMounts:
      - name: dockersock
        mountPath: /var/run/docker.sock
      - name: registry-pvc
        mountPath: /var/lib/registry
"""
}

def anchore_AgentSpec(Map containerConfig) {
  """  - name: anchore-cli
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command:
      - cat
    tty: true
    securityContext:
      allowPrivilegeEscalation: false
      runAsUser: 0
"""
}

def helm_AgentSpec(Map containerConfig) {
  """  - name: helm
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"  
    command:
      - cat
    tty: true
    volumeMounts:
      - name: helmplugins
        mountPath: /root/helmplugins
"""
}

def gitleaks_AgentSpec(Map containerConfig) {
  """  - name: gitleaks
    image: ${containerConfig.image}
    command:
      - cat
    tty: true
"""
}
