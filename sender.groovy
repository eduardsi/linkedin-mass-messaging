


def buildMessage =  { name -> """Hi $name!

I just wanted to let you know about the exciting opportunity to become a Certified Scrum Master and a Certified Scrum Product Owner by attending the official ScrumAlliance training in Riga this May.

In order to make the training practical and entertaining, we have exists a famous Agile coach Alexey Krivitsky - the author of "Agile Retrospective Kickstarter" book.

Many product and software development professionals from local companies already applied (me including) and we have only 5 seats remaining.

You can find more details about both classes here:
- CSM: http://devchampions.com/training/csm/
- CSPO: http://devchampions.com/training/cspo/

If you apply before 13th March, use the following 10% discount code: AGILE_EXPERT


See you there (and help us spread a word)!""" }

Browser.drive(baseUrl: "http://linkedin.com") {
	to WelcomePage
	username.value("eduards.sizovs@gmail.com")
	password.value("qwerty123")
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

    def avoid = ["Tatiana Endrjukaite", "Jānis Lāma", "Jelena Veide", "Sanda Linde", "Ljubov Bezan"]
    def skipAt = "Igors Ņikonovs"

    def popupShown = 0
    (0..5).each { rowIndex ->
    
        def row = contacts(rowIndex)
        def fullName = row.fullName()
        if (skipAt == fullName) {
            throw new RuntimeException("Skipped at $skipAt")
        }

        def file = new File("./${fullName}.txt")
        if (!file.exists() && !avoid.contains(fullName)) {
            println "Preparing to createMissing message to $fullName"
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



