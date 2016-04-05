package bulkedin

import geb.Browser

def buildMessage = { name ->
    """Hi $name!

I just wanted to let you know about the great opportunity to deep-dive into Scrum and become a Certified Scrum Master and a Certified Scrum Product Owner by attending the official Scrum Alliance training in Riga this May.

We have invited a well-known, energizing Agile coach Alexey Krivitsky - the author of "Agile Retrospective Kickstarter" book and inventor of one of the most popular Scrum simulations - "lego4scrum".

Registration is running right behind the corner:
- 3-4 May, CSPO: http://devchampions.com/training/cspo/
- 5-6 May, CSM: http://devchampions.com/training/csm/

You can use a special promo code (valid until 07.04 midnight), that gives 200 EUR discount: 4FRIENDS_$name

I am looking forward to meeting you in person at the camp!"""
}

Browser.drive {
    def welcome = to WelcomePage
    def main = welcome.login()

    def connections = main.connections()
    def popupShown = 1
//    def popupShown = 0
    (0..800).each { rowIndex ->
        def connection = connections.connection(rowIndex)
        if (connection.messageSendingPossible) {

            connection.newMessage()

            waitForNewMessagePopup(0, popupShown)
//            waitForNewMessagePopup(popupShown, popupShown+1)

            def message = buildMessage(connection.firstName)
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



