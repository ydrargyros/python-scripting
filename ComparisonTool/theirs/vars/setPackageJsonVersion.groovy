import groovy.json.JsonSlurperClassic

// Calling sample
// setPackageJsonVersion(build, updateOption)
// updateOptions: 1.update, 2.updateWithSuffix

def call(Map build, String updateOption) {
    packageJSON = setJsonToMap('package.json')
    if (updateOption == 'update') {
        newPackageJsonVersion = "${build.version}"
    } else if (updateOption == 'updateWithSuffix') {
        newPackageJsonVersion = "${build.version}-${currentBuild.number}"
    }
//    newPackageJsonVersion = "${build.version}-${currentBuild.number}"
    println "newPackageJsonVersion is ${newPackageJsonVersion}"
    packageJSON.version = "${newPackageJsonVersion}"
    writeJSON file: 'package.json', json: packageJSON, pretty: 1
    sh "cat package.json"
}


def setJsonToMap(String fileName) { // read json
    def json = readFile(file: fileName)
    def jsonSlurper = new JsonSlurperClassic()
    def map = jsonSlurper.parseText(json)
//    println "${map}"
    return map
}