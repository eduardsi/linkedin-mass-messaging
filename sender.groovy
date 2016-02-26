@Grab('org.gebish:geb-core:0.13.1')
@Grab('org.seleniumhq.selenium:selenium-chrome-driver:2.52.0')
import org.openqa.selenium.Keys

@GrabExclude('org.codehaus.groovy:groovy-all')    
import geb.Browser
import geb.Page
import geb.Module



class WelcomePage extends Page {

	static at = { title == "World’s Largest Professional Network | LinkedIn" }

	static content = {
        loginButton(to: MainPage) { $("input", type: "submit", name: "submit") }
        username { $("input", name: "session_key") }
        password { $("input", name: "session_password") }
    }

}

class MainPage extends Page {
	static at = { title == "Welcome! | LinkedIn" }

	static content = {
		myNetwork { $(".main-nav .nav-item:nth-child(3)") }
		myConnections(to: ConnectionPage) { $("a", text: "Connections") }
	}
}

class ConnectionPage extends Page {

    static at = { title == "Contacts | LinkedIn" } 

    static content = {
        sorter { $("#contact-list-organizer-menu .sort .arrow-down") }
        sortNewFirst { $("li[data-li-item-id='new']") }


        filter { $("#contact-list-organizer-menu .filter .arrow-down") }
        filterConnectionsOnly { $("li[data-li-item-id='connections']") }

        footer { $("footer") }

        waitForLoading() { 
            waitFor { $(".engagement-card").size() == 9 }
            waitFor { $(".engagement-card")*.displayed }
        	waitFor { $(".contact-item-view").size() == 10 }
            waitFor { $(".contact-item-view")*.displayed }
        }
        
        contacts { row ->
            def rowVisible = $(".contact-item-view", row).displayed
            if (!rowVisible) {
                browser.js.exec('window.scrollBy(0, 1000)')
                waitFor {
                    $(".contact-item-view", row).displayed
                }
            }
            $(".contact-item-view", row).module(ConnectionRow) 
        }

        loadPages { numberOfPages, Closure onPageLoad ->
        	1.upto(numberOfPages + 1) {
				browser.js.exec('window.scrollBy(0, 1000)')
				waitFor { ((it + 1) * 10) == contacts.size() }


				def from = (it - 1) * 10
				def to = from + 9
				onPageLoad(from, to)	        		
        	}
        }

        waitForNewMessagePopup { index -> 
            waitFor { $(".contacts-dialog", index).displayed }
        }

        waitForNewMessagePopupCloses { index ->
            waitFor { !$(".contacts-dialog h3", index).displayed }
        }

        newMessagePopup { index -> 
            $(".contacts-dialog", index).module(NewMessageModule) 
        }
    }
}

class ConnectionRow extends Module {
	static content = {
    	cell { $("td", it) }
    	fullName { $("h3.name").text() }
    	firstName { fullName().split().first() }
    	lastName { fullName().split().last() }
    	newMessage { $("li[data-li-item-id='message'] a") }
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

def buildMessage =  { name -> """Hi $name!  

I just wanted to let you know about the exciting opportunity to become a Certified Scrum Master and a Certified Scrum Product Owner by attending the official ScrumAlliance training in Riga this May. 

In order to make the training practical and entertaining, we have invited a famous Agile coach Alexey Krivitsky - the author of "Agile Retrospective Kickstarter" book.

Many product and software development professionals from local companies already applied (me including) and we have only 5 seats remaining.

You can find more details about both classes here:
- CSM: http://devchampions.com/training/csm/  
- CSPO: http://devchampions.com/training/cspo/

If you apply before 13th March, use the following 10% discount code: AGILE_EXPERT

See you there (and help us spread a word)!""" }

Browser.drive(baseUrl: "http://linkedin.com") {
	to WelcomePage
	username.value("______")
	password.value("______")
	loginButton.click()

	at MainPage
	interact {
            moveToElement myNetwork
    }

	myConnections.click()
	at ConnectionPage
	waitForLoading()

	sorter.click()
	sortNewFirst.click()
	waitForLoading()

	filter.click()
	filterConnectionsOnly.click()
	waitForLoading()

    def avoid = ["Tatiana Endrjukaite", "Jānis Lāma", "Jelena Veide", "Sanda Linde"]
    def skipAt = "Jānis Dambis"

    def popupShown = 0
    (0..5).each { rowIndex ->
    
        def row = contacts(rowIndex)
        def fullName = row.fullName()
        if (skipAt == fullName) {
            throw new RuntimeException("Skipped at $skipAt")
        }

        def file = new File("./${fullName}.txt")
        if (!file.exists() && !avoid.contains(fullName)) {
            println "Preparing to send message to $fullName"
            interact {
                moveToElement row
            }  
            row.newMessage.click()

            waitForNewMessagePopup(popupShown)

            def message = buildMessage(row.firstName())
            def popup = newMessagePopup(popupShown)
            popup.subject.value("CSM/CSPO classes in Riga")
            popup.message.value(message)            
            popup.send.click()
            waitForNewMessagePopupCloses(popupShown)
            // popup.close.click()
            file.write message
            popupShown++
            // waitForNewMessagePopupCloses(fullName)
        }
        
    }
	
}



