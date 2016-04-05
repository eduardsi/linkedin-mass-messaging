package bulkedin


class Message {


    String build(String firstName) {
        firstName = firstName.toLowerCase().capitalize()
        """Hi $firstName!

I just wanted to let you know about the great opportunity to deep-dive into Scrum and become a Certified Scrum Master and a Certified Scrum Product Owner by attending the official Scrum Alliance training in Riga this May.

We have invited a well-known, energizing Agile coach Alexey Krivitsky - the author of "Agile Retrospective Kickstarter" book and inventor of one of the most popular Scrum simulations - "lego4scrum".

Registration is running right behind the corner:
- 3-4 May, CSPO: http://devchampions.com/training/cspo/
- 5-6 May, CSM: http://devchampions.com/training/csm/

You can use a special promo code (valid until 07.04 midnight), that gives 200 EUR discount: 4FRIENDS_$firstName

I am looking forward to meeting you in person at the camp!"""
    }

}