package bulkedin.domain.bot

import bulkedin.SearchForPeopleAndSentInvite
import bulkedin.Messenger

import static bulkedin.domain.bot.BotSignal.*

class BotJobCommander {

    BotJob assignJob(BotSignal botSignal) {
        switch (botSignal) {
            case NONE:
                new SearchForPeopleAndSentInvite()
                break
            case PROTECTED_PERSON_INVITED:
                new SearchForPeopleAndSentInvite()
                break
            case INVITATION_LIMIT_REACHED:
                new Messenger()
                break
        }
    }

}
