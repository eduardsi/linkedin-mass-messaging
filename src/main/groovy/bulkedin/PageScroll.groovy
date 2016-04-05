package bulkedin

import geb.Page

class PageScroll {

    Page page

    def scrollDownAndWaitUntil(Closure<Boolean> exitConditionSatisfied) {
        if (!exitConditionSatisfied()) {
            page.js.exec('window.scrollBy(0, 2000)')
            page.waitFor {
                exitConditionSatisfied()
            }
        }
    }

}
