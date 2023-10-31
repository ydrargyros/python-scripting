/*  Calling sample
    getBranchName(config)
    Arguments: path or varName, ManagedFileID
 */
//TODO add configFileProvider class, arguments for both variable and targetLocation options (maybe replaceTokens too)

@java.lang.SuppressWarnings('unused')
def loadEnvironmentVariables(path) {
    def props = readProperties file: path
    keys = props.keySet()
    for (key in keys) {
        value = props["${key}"]
        env."${key}" = "${value}"
    }
}