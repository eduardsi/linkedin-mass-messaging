package bulkedin

import geb.Module
import geb.Page

class ProfilePage extends Page {

    String fullName

    static at = { fullName -> title == "$fullName | LinkedIn" }

    static content = {
        certifications(required: false) { $("#background-certifications").module(Certifications) }
    }


}


class Certifications extends Module {
    static content = {
        $(".section-item").moduleList(Certificate)
    }

}

class Certificate extends Module {
    static content = {
        title { $("hgroup h4 a") }
    }
}