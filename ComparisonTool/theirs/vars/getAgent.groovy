// Calling sample
//getAgent(config)
import groovy.transform.Field

@Field
Map defaults = [
  agent:[
    // GP
      jnlp :[
        "image" : "10.240.36.72/dg17-public-docker/jenkins/inbound-agent:4.11-1-jdk11",
        resources:[
            "cpuRequests":"100m",
            "memoryRequests":"256Mi",
            "cpuLimits":"200m",
            "memoryLimits":"512Mi",
            "ephemeralRequests":"3000Mi"
            //"ephemeralLimits":"3500Mi"
        ]
      ],
      node :[
        "image" : "10.240.36.72/dg17-public-docker/node:18.13-bullseye-slim",
        resources:[
            "cpuRequests":"1024m",
            "memoryRequests":"500Mi",
            "cpuLimits":"4000m",
            "memoryLimits":"9000Mi",
            "ephemeralRequests":"3000Mi"
            //"ephemeralLimits":"3500Mi"
        ]
      ],
      maven38jdk17:[
        "image":"10.240.36.72/dg17-public-docker/maven:3.8-openjdk-17",
        resources:[
            "cpuRequests":"500m",
            "memoryRequests":"1024Mi",
            "cpuLimits":"4000m",
            "memoryLimits":"9000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
    // GP
      maven386jdk11:[
        "image":"10.240.36.72/dg17-public-docker/maven:3.8.6-jdk-11",
        resources:[
            "cpuRequests":"800m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"1000m",
            "memoryLimits":"4000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven363jdk11slim:[
        "image":"10.240.36.72/dg17-public-docker/maven:3.6.3-jdk-11-slim",
        resources:[
            "cpuRequests":"7000m",
            "memoryRequests":"13000Mi",
            "cpuLimits":"8000m",
            "memoryLimits":"14000Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven381openjdk11slim:[
        "image":"10.240.36.72/dg17-public-docker/maven:3.8.1-openjdk-11-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"2000m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
      maven383openjdk11slim:[
        "image":"10.240.36.72/dg17-public-docker/maven:3.8.3-openjdk-11-slim",
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
        "image":"10.240.36.72/dg17-public-docker/maven:3.6.3-openjdk-8-slim",
        resources:[
            "cpuRequests":"1000m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"2000m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
            //"ephemeralLimits":"5500Mi"
        ]
      ],
    // GP
      docker:[
        "image":"10.240.36.72/dg17-public-docker/docker:20.10.14-dind",
        resources:[
            "cpuRequests":"500m",
            "memoryRequests":"300Mi",
            "cpuLimits":"4000m",
            "memoryLimits":"2000Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      anchore:[
        "image":"10.240.36.72/dg17-public-docker/anchore/engine-cli:v0.9.1",
        resources:[
//            "cpuRequests":"200m",
//            "memoryRequests":"1907Mi",
//            "cpuLimits":"500m",
//            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      // GP
      gitleaks:[
        "image":"10.240.36.72/dg17-public-docker/zricethezav/gitleaks:v8.8.6",
        resources:[
            "cpuRequests":"200m",
            "memoryRequests":"1907Mi",
            "cpuLimits":"500m",
            "memoryLimits":"3814Mi",
            "ephemeralRequests":"300Mi"
//            //"ephemeralLimits":"500Mi"
        ]
      ],
      // GP
      ubuntu:[
        "image":"10.240.36.72/dg17-public-docker/ubuntu:20.04",
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
    podYaml = getYamlHead(pipelineConfig)
    images.each { image ->
      if (env.BRANCH_NAME == "develop" && image == "maven363openjdk8slim") {
        image = "maven383openjdk11slim"
      }
      func = "$image" + "_AgentSpec" // eg mavenAgentSpec
      imagesSubmap = config.agent.get(image)
      podYaml += "$func"(imagesSubmap,images) // call function with corresponding submap eg maven386jdk11_AgentSpec(config.agent.maven)
      if (imagesSubmap.containsKey("resources")) {
        podYaml += getResources(imagesSubmap)
      }
    }
    podYaml += getYamlTail(images)
    // echo podYaml
    return podYaml
}

def getYamlHead(Map pipelineConfig) {
    head = """
apiVersion: v1
kind: Pod
metadata:
spec:
"""
    if(pipelineConfig.agent_specs.enabled_host_alias == "true"){
        pipelineConfig.agent_specs.host_alias.each { ip,hostnames ->
            head += """
  hostAliases:
  - ip: "${ip}"
    hostnames:
"""
            hostnames.each { hostname ->
                head += """
    - "${hostname}"
"""
            }
        }
    }
    head += """  containers:
    """
    return head
}

def getYamlTail(images) {
  tail = """
  volumes:
  - name: custom-keystore
    secret:
      secretName: ca-key-pair
      items:
      - key: tls.crt
        path: cacerts 
"""

  if (images.contains("maven38jdk17") || images.contains("maven386jdk11") || images.contains("maven363jdk11slim") || images.contains("maven381openjdk11slim") || images.contains("maven383openjdk11slim") || images.contains("maven363openjdk8slim")){
    tail += """ 
  - name: m2-repo
    persistentVolumeClaim:
      claimName: maven-repo-pvc"""
  }

  if (images.contains("docker")) {
    tail += """    
  - name: varrun
    emptyDir: {}"""
  }

  if (images.contains("node")) {
    tail += """    
  - name: npm
    emptyDir: {}"""
  }

  tail += """    """

  return tail
}

def getBasicAgentSpec() {
    """  - name: busybox
    image: busybox
    command:
    - cat
    tty: true
    imagePullPolicy: Always
"""
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

def jnlp_AgentSpec(Map containerConfig,images) {
    """  - name: jnlp
    image: ${containerConfig.image}
    args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
"""
}

def node_AgentSpec(Map containerConfig,images) {
    """  - name: node
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command: [ '/bin/sh','-c','cp -R /usr/local/lib/node_modules /node ; cp -R /opt/yarn-v1.22.19 /node ; cp /usr/local/bin/node /node ; cat' ]
    tty: true
    volumeMounts:
      - mountPath: "/node"
        name: "npm"
        readOnly: false
"""
}

def maven38jdk17_AgentSpec(Map containerConfig,images) {
    agent_spec="""
  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
      postStart:
        exec:
          command:
            - /bin/sh
            - -c
            - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
"""
    if (images.contains("node")) {
      agent_spec += """  
    command: [ '/bin/sh','-c','ln -s /node/node /usr/local/bin/node ; ln -s /node/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx ; ln -s /node/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm ; ln -s /node/node_modules/corepack/dist/corepack.js /usr/local/bin/corepack ; ln -s /node/node /usr/local/bin/nodejs ; ln -s /node/yarn-v1.22.19/bin/yarnpkg /usr/local/bin/yarnpkg ; ln -s /node/yarn-v1.22.19/bin/yarn /usr/local/bin/yarn ; cat' ]
    env:
      - name: NODE_OPTIONS
        value: "--max-old-space-size=8192"
    """
    }
    else {
      agent_spec +="""
    command:
      - cat
    """
    }
    agent_spec +="""     
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
    agent_spec += """    
      - name: "varrun"
        mountPath: "/var/run"
        readOnly: false
"""
  }
    if (images.contains("node")) {
        agent_spec += """    
      - mountPath: "/node"
        name: "npm"
        readOnly: false
"""
    }
  return agent_spec
}

def maven363jdk11slim_AgentSpec(Map containerConfig,images) {
    agent_spec="""  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
        postStart:
          exec:
            command:
              - /bin/sh
              - -c
              - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
    command:
      - cat
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
    agent_spec += """    
      - name: varrun
        mountPath: "/var/run"
        readOnly: false
"""
  }
  return agent_spec
}

def maven381openjdk11slim_AgentSpec(Map containerConfig,images) {
    agent_spec="""  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
        postStart:
          exec:
            command:
              - /bin/sh
              - -c
              - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
    command:
      - cat
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
    agent_spec += """    
      - name: varrun
        mountPath: "/var/run"
        readOnly: false
"""
  }
  return agent_spec
}

def maven383openjdk11slim_AgentSpec(Map containerConfig,images) {
    agent_spec="""  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
        postStart:
          exec:
            command:
              - /bin/sh
              - -c
              - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
    command:
      - cat
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
    agent_spec += """    
      - name: varrun
        mountPath: "/var/run"
        readOnly: false
"""
  }
  return agent_spec
}

def maven386jdk11_AgentSpec(Map containerConfig,images) {
    agent_spec="""
  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
      postStart:
        exec:
          command:
            - /bin/sh
            - -c
            - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
"""
    if (images.contains("node")) {
        agent_spec += """  
    command: [ '/bin/sh','-c','ln -s /node/node /usr/local/bin/node ; ln -s /node/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx ; ln -s /node/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm ; ln -s /node/node_modules/corepack/dist/corepack.js /usr/local/bin/corepack ; ln -s /node/node /usr/local/bin/nodejs ; ln -s /node/yarn-v1.22.19/bin/yarnpkg /usr/local/bin/yarnpkg ; ln -s /node/yarn-v1.22.19/bin/yarn /usr/local/bin/yarn ; cat' ]
    env:
      - name: NODE_OPTIONS
        value: "--max-old-space-size=8192"
    """
    }
    else {
        agent_spec +="""
    command:
      - cat
    """
    }
    agent_spec +="""     
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
        agent_spec += """    
      - name: "varrun"
        mountPath: "/var/run"
        readOnly: false
"""
    }
    if (images.contains("node")) {
        agent_spec += """    
      - mountPath: "/node"
        name: "npm"
        readOnly: false
"""
    }
    return agent_spec
}


def maven363openjdk8slim_AgentSpec(Map containerConfig,images) {
    agent_spec="""  - name: maven
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    lifecycle:
        postStart:
          exec:
            command:
              - /bin/sh
              - -c
              - keytool -import -alias pdcommonca -keystore \$JAVA_HOME/lib/security/cacerts -file /tmp/myCA/cacerts -noprompt -storepass changeit
    command:
      - cat
    tty: true
    volumeMounts:
      - name: m2-repo
        mountPath: /root/.m2
      - name: custom-keystore
        mountPath: /tmp/myCA
"""
    if (images.contains("docker")) {
    agent_spec += """    
      - name: varrun
        mountPath: "/var/run"
        readOnly: false
"""
  }
  return agent_spec
}

def ubuntu_AgentSpec(Map containerConfig,images) {
    """  - name: ubuntu
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    command: ["/bin/sleep", "3650d"]
"""
}


def docker_AgentSpec(Map containerConfig,images) {
    """ 
  - name: docker
    image: ${containerConfig.image}
    imagePullPolicy: "IfNotPresent"
    tty: true
    securityContext:
      privileged: true
    volumeMounts:
      - name: "varrun"
        mountPath: "/var/run"
        readOnly: false
      - name: custom-keystore
        mountPath: /tmp/myCA
    lifecycle:
      postStart:
        exec:
          command:
            - /bin/sh
            - -c
            - 'mkdir -p /etc/docker/certs.d/edr-nexus-compliance.athens.intrasoft-intl.private ; cp /tmp/myCA/cacerts /etc/docker/certs.d/edr-nexus-compliance.athens.intrasoft-intl.private/ca.crt; mkdir -p /etc/docker/certs.d/eds-nexus-compliance.athens.intrasoft-intl.private ; cp /tmp/myCA/cacerts /etc/docker/certs.d/eds-nexus-compliance.athens.intrasoft-intl.private/ca.crt; mkdir -p /etc/docker/certs.d/dockers-nexus-compliance.athens.intrasoft-intl.private ; cp /tmp/myCA/cacerts /etc/docker/certs.d/dockers-nexus-compliance.athens.intrasoft-intl.private/ca.crt'
"""
}

def anchore_AgentSpec(Map containerConfig,images) {
  """  - name: anchore
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

def gitleaks_AgentSpec(Map containerConfig,images) {
  """  - name: gitleaks
    image: ${containerConfig.image}
    command:
      - cat
    tty: true
"""
}
