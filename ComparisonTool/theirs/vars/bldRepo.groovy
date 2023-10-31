// Calling sample
// bldRepo(config) 

//def call(Map config) {
//    if (config.build.repoType == 'MAVEN') {
//        bldMvn(config.build.maven);
//    }
//    else if (config.build.repoType == 'NPM') {
//        bldNpm(config.build.npm);
//    }
//}

def call(Map config) {
    if (config.build.repoType == 'MAVEN') {
        bldMvn(config.build);
    }
    else if (config.build.repoType == 'NPM') {
        bldNpm(config.build);
    }
}