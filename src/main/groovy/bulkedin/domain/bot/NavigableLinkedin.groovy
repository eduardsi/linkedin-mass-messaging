package bulkedin.domain.bot

import bulkedin.Messenger
import bulkedin.SearchForPeopleAndSentInvite
import bulkedin.WelcomePage
import geb.Browser
import groovy.transform.TailRecursive

class NavigableLinkedin {

    @Delegate
    Browser browser

    NavigableLinkedin(Browser browser) {
        this.browser = browser
    }


    def navigate() {
        perform(new SearchForPeopleAndSentInvite(browser: browser))
    }


    @TailRecursive
    def perform(BotJob job) {
        try {
            job.perform()
        } catch (SearchLimitReached ignored) {
            println "Search limit reached"
            job = new Messenger(browser: browser)
        } catch (InvitationLimitReached ignored) {
            println "Invitation Limit Reached"
            job = new Messenger(browser: browser)
        } catch (FailedOnEmailPrompt ignored) {
            println "Failed on email prompt"
            job = new SearchForPeopleAndSentInvite(browser: browser)
        }
        perform(job)
    }

    def authenticate() {
        def welcome = to WelcomePage
        welcome.login()
    }

}

//Due to excessive searching, your people search results are limited to your 1st-degree connections for security reasons. This restriction will be lifted in 24 hours.