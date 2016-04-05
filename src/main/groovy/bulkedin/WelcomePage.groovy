package bulkedin

import geb.Page

class WelcomePage extends Page {

    static at = { title == "World’s Largest Professional Network | LinkedIn" }

    private static content = {
        username { $("input", name: "session_key") }
        password { $("input", name: "session_password") }
        loginButton { $("input", type: "submit", name: "submit") }
    }

    MainPage login() {
        username =  ""
        password = ""
        loginButton.click()
        browser.at MainPage
    }

}