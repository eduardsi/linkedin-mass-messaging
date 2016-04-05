package bulkedin

class InvitationFile {

    File file

    InvitationFile(String countryCode, String fullName) {
        def dir = "/Users/EDUARDSI/Desktop/projects/linkedin/invitations/$countryCode"
        file = new File(dir, "${fullName}.txt")
        println "Thinking of inviting $fullName from $countryCode"
    }

    def present() {
        file.exists()
    }

    def create() {
        file.createNewFile()
    }

}
