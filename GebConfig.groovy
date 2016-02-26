import geb.navigator.NonEmptyNavigator
import geb.navigator.EmptyNavigator
import geb.Browser
import org.openqa.selenium.WebElement

class LinkedinNavigator extends NonEmptyNavigator {

	LinkedinNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
        super(browser, contextElements)
    }

	void scrollDownWhile(Closure<Integer> condition) {
		while(true) {
			def numberOfVisibleConnections = condition()
			browser.js.exec('window.scrollBy(0, 1000)')
			waitFor { numberOfVisibleConnections <= condition() }
		} 
	}    
}

innerNavigatorFactory = { Browser browser, List<WebElement> elements ->
    elements ? new LinkedinNavigator(browser, elements) : new EmptyNavigator(browser)
}


