package bulkedin

import geb.Module
import geb.Page

class EmailPromptPage extends Page {

    static at = { $("why-email-info") }

}

class InvitePage extends Page {

    static at = { title.endsWith "to Connect | LinkedIn" }

    static content = {
        connectionReason { $("#main-options").module(ConnectionReason) }
        greeting { $("textarea", name: "greeting") }
        inviteButton { $("#createMissing-invite-button") }
        alert { $("#global-alert-queue strong") }
    }

    boolean isLimitReached() {
        def alertText = "LinkedIn automatically limits the total number of invitations a user can createMissing, in order to protect both senders and recipients"
        alert.text().contains(alertText)
    }

    def send() {
        connectionReason.colleague.click()
        connectionReason.colleagueAt("Founder at Latvian Software Craftsmanship Community").click()
        inviteButton.click()
    }

}

class ConnectionReason extends Module {
    static content = {
        colleague { $("input", value: "IC") }
        colleagueAt { text -> $("#colleagues-list option", text: text) }
    }
}


