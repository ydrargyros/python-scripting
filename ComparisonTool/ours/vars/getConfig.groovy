// Calling sample
// getConfig(pipelineConfig)

import groovy.json.JsonSlurperClassic

def call(Map pipelineConfig) {
    defaultConfig = getDefaultConfig()
    effectiveConfig = merge(defaultConfig, pipelineConfig)
    //println "Default config: " + defaultConfig + " \n\n" //Sanity Check: Show Default configuration (parsed json) NEEDED
    //println "Given config: " + pipelineConfig + " \n\n" //Sanity Check: Show Given configuration (pipelineConfig) NEEDED
    //println "Effective config: " + effectiveConfig + " \n\n" //Sanity Check: Show Effective configuration (merged configs) NEEDED
    return effectiveConfig
}

def merge(Map lhs, Map rhs) {
    return rhs.inject(lhs.clone()) { map, entry ->
        if (map[entry.key] instanceof Map && entry.value instanceof Map) {
            map[entry.key] = merge(map[entry.key], entry.value)
        } else if (map[entry.key] instanceof Collection && entry.value instanceof Collection) {
            map[entry.key] += entry.value
        } else {
            map[entry.key] = entry.value
        }
        return map
    }
}

def getDefaultConfig() { // read json
    def json = libraryResource 'defaultConfig.json'
    def jsonSlurper = new JsonSlurperClassic()
    def map = jsonSlurper.parseText(json)
    return map
}
