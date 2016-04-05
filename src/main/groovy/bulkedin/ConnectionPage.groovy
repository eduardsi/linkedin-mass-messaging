package bulkedin

import geb.Module
import geb.Page

class ConnectionPage extends Page {

    def scroll = new PageScroll(page: this)

//    static at = { title == "Contacts | LinkedIn" }
    static at = { title == "Connections | LinkedIn" }

    static content = {
        sorter { $("#contact-list-organizer-menu .sort .arrow-down") }
        sortNewFirstLink { $("li[data-li-item-id='new']") }

        filter { $("#contact-list-organizer-menu .filter .arrow-down") }
        filterConnectionsOnly { $("li[data-li-item-id='connections']") }

        footer { $("footer") }

        connections { $(".contact-item-view") }

        loadPages { int numberOfPages, Closure onPageLoad ->
            1.upto(numberOfPages + 1) {
                browser.js.exec('window.scrollBy(0, 2000)')
                waitFor { ((it + 1) * 10) == connections.size() }


                def from = (it - 1) * 10
                def to = from + 9
                onPageLoad(from, to)
            }
        }

        waitForNewMessagePopup { int index, popupShown ->
            waitFor { $(".contacts-dialog").size() == popupShown }
            waitFor { $(".contacts-dialog", index).displayed }
        }


        waitForNewMessagePopupCloses { int index ->
            waitFor { !$(".contacts-dialog h3", index).displayed }
        }

        newMessagePopup { int index ->
            $(".contacts-dialog", index).module(NewMessageModule)
        }
    }

    ConnectionRow connection(int index) {
        def row = connections[index]
        scroll.scrollDownAndWaitUntil {
            row.displayed
        }
        row.module(ConnectionRow)
    }

    def waitForLoading() {
        waitFor { $(".engagement-card").size() == 9 }
        waitFor { $(".engagement-card")*.displayed }
        waitFor { $(".contact-item-view").size() == 10 }
        waitFor { $(".contact-item-view")*.displayed }
    }

    def sortNewFirst() {
        sorter.click()
        sortNewFirstLink.click()
        waitForLoading()
    }

    def showConnectionsOnly() {
        filter.click()
        filterConnectionsOnly.click()
        waitForLoading()
    }

}

class ConnectionRow extends Module {
    static content = {
        cell { $("td", it) }
        fullName { $("h3 a").text() }
        firstName { fullName.split().first() }
        lastName { fullName.split().last() }
        newMessageLink(required: false) { $("li[data-li-item-id='message'] a") }
    }

    boolean isMessageSendingPossible() {
        def ignores = [new AlreadyContacted(), new ExplicitlyIgnored()] as IgnoreSpecification[]
        def shouldIgnore = ignores.any { it.satisfies(fullName) }

        interact {
            moveToElement this
        }
        !shouldIgnore && newMessageLink.displayed
    }

    def newMessage() {
        interact {
            moveToElement this
        }
        newMessageLink.click()

    }
}

class NewMessageModule extends Module {

    static content = {
        send { $("#send_message_linkedin_message_button") }
        subject { $("#send_message_linkedin_message_subject") }
        message { $("#send_message_linkedin_message_message") }
        close { $(".dialog-close") }
    }


}

