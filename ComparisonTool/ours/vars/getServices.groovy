// Calling sample
// getServices(config, "-app") 

def call(Map config, String servicePattern = '-app') {
	// if provided in pipelineConfig
	if (config.containsKey("services")) { 
		return config
	}

	// else search for them
	microservices = [:]		
	config.services = getMicroservices(microservices, "")
	return config
}

def getMicroservices(Map microservices, String directory) {
	String old_directory = directory
	contents = findFiles() 
	contents.find{ content -> 
		directory = old_directory
		if (content.name == "Dockerfile") {
			microservices[directory.tokenize("/")[-1]] = directory
			return true
		} else if (content.directory) {
			if (content.name == ".git" || content.name == "cdp" || content.name == "helmcharts" || content.name == "src") {
				return false
			}
			directory += content.name + "/"
			dir(content.name) {
				getMicroservices(microservices, directory)
			}
		}
		return false
	}
	return microservices
}
