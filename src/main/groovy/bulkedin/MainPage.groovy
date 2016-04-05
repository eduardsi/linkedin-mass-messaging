package bulkedin

import geb.Page

class MainPage extends Page {
    static at = { title == "Welcome! | LinkedIn" }

    private static content = {
        myNetwork { $(".main-nav .nav-item:nth-child(3)") }
        myConnections { $("a", text: "Connections") }
        advancedSearchButton { $("#advanced-search") }
    }

    ConnectionPage connections() {
        interact {
            moveToElement myNetwork
        }
        myConnections.click()

        def page = browser.at ConnectionPage
        page.waitForLoading()
        page
    }

    AdvancedSearchPage advancedSearch() {
        advancedSearchButton.click()
        def page = browser.at AdvancedSearchPage
        page.waitForLoading()
        page.checkLimit()
        page
    }

}