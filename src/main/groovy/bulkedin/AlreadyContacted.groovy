package bulkedin

class AlreadyContacted implements IgnoreSpecification {

    @Override
    boolean satisfies(String fullName) {
        def file = new File("/Users/EDUARDSI/Desktop/projects/linkedin/${fullName}.txt")
        file.exists()
    }

}
