package bulkedin

import bulkedin.domain.bot.BotJob
import bulkedin.domain.bot.FailedOnEmailPrompt
import bulkedin.domain.bot.InvitationLimitReached
import bulkedin.domain.bot.NavigableLinkedin
import geb.Browser

class SearchSpecification {
    String jobTitle = "Project Manager"
    String countryCode = "lt"
//    Collection<String> industry = ["Information Technology and Services", "Computer Software", "Telecommunications"]
    Collection<String> industry = ["Information Technology and Services", "Computer Software", "Banking"]
//    Collection<String> industry = []
}

// Architect // Manager / / Analyst / PMO / Head of / Senior / Lead / QA / DevOps / CTO / CIO

// all industries: IT Manager, Product Manager, Project Manager (lt please!), Software Architect, Solution Architect, Solutions Architect, IT Architect, Enterprise Architect,
// Head Management, Head Technical, Head Technology, Head Testing, Head QA, Head Application, Head Security, Head Infrastructure, Head DWH, Head Software Development, Head IT, Head Project, Head Product,Head Portfolio, PMO, System Analyst, Systems Analyst, IT Analyst, Business Analyst,

Browser.drive {
    def linkedin = new NavigableLinkedin(browser)
    linkedin.authenticate()
    linkedin.navigate()
}

class Messenger implements BotJob {

    @Delegate
    Browser browser

    @Override
    void perform() {
        def main = to MainPage
        def connections = main.connections()
        def popupShown = 1
//    def popupShown = 0
        (0..800).each { rowIndex ->
            def connection = connections.connection(rowIndex)
            if (connection.messageSendingPossible) {

                connection.newMessage()

                waitForNewMessagePopup(0, popupShown)
//            waitForNewMessagePopup(popupShown, popupShown+1)

                def message = new Message().build(connection.firstName)
                def popup = newMessagePopup(0)
//            def popup = newMessagePopup(popupShown)
                popup.subject.value("Personal invitation to the official CSM and CSPO classes in Riga")
                popup.message.value(message)
                popup.send.click()                        // prod
//                popup.close.click()                    // fake
                waitForNewMessagePopupCloses(0)
//            waitForNewMessagePopupCloses(popupShown)
                def file = new File("/Users/EDUARDSI/Desktop/projects/linkedin/${connection.fullName}.txt")
                file.write message                        // prod
                popupShown++
            }

        }
    }
}

class SearchForPeopleAndSentInvite implements BotJob {

    @Delegate
    Browser browser

    @Override
    void perform() {
        def main = to MainPage
        def search = main.advancedSearch()
        def spec = new SearchSpecification()
        def pages = search.perform(spec)
        pages.eachWithIndex { people, pageIndex ->
            println "Processing page $pageIndex"
            people.each { person ->
                def invitationFile = new InvitationFile(spec.countryCode, person.fullName())
                if (!invitationFile.present() && person.openForConnection) {
                    person.connect()
                    if (isAt(AdvancedSearchPage)) {
                        println "Invited"
                        invitationFile.create()
                    }
                    if (isAt(EmailPromptPage)) {
                        invitationFile.create()
                        throw new FailedOnEmailPrompt()
                    }
                    if (isAt(InvitePage)) {

                        throw new InvitationLimitReached()
                    }
                }
            }
        }
    }
}
