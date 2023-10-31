// Calling sample
// log.stageBanner "${STAGE_NAME}"

// Definition of available colouring
def loadColors() {
    STAGEBANNER= '\033[1;38;5;231;48;5;45m'
    GREY = '\033[1;47m' //DEBUG
    YELLOW_BG = '\033[43m' //WARN
    RED = '\033[0;37;1;101m' //ERROR
    NC = '\033[0m' //Disable colour
}

//Display Stage Banner
def stageBanner(stageName) {
    loadColors()
    printf "\n\n${STAGEBANNER}##################################################################"
    printf "${STAGEBANNER}##${NC}\t\t [STAGE] - $stageName "
    printf "${STAGEBANNER}##################################################################${NC} \n\n"
}

// Message types
def info(message) {
    loadColors()
    printf "\n\n${YELLOW_BG}##################################################################"
    printf "${YELLOW_BG}##${NC}\t\t [STAGE] - $message "
    printf "${YELLOW_BG}##################################################################${NC} \n\n"
}

def debug(message) {
    loadColors()
    printf "${GREY}[DEBUG] - $message ${NC}\n"
//    debug = evaluate("debug")
//    script{echo "Debug mode is ${debug}"}
}

def warn(message) {
    loadColors()
    printf "${YELLOW_BG}[WARN] - $message ${NC}\n"
}

def error(message) {
    loadColors()
    printf "${RED}[ERROR] - $message ${NC}\n"
}