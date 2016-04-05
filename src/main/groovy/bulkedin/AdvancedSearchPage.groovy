package bulkedin

import bulkedin.domain.bot.SearchLimitReached
import geb.Module
import geb.Page

class AdvancedSearchPage extends Page {

    static at = { title == "Search | LinkedIn" }

    static content = {

        popupIsActive {
            $("#advs").hasClass("active")
        }

        waitUntilLoaded {
            waitFor {
                !$("#srp_main_").hasClass("loading")
            }
        }

        locationPopup { $("#advs-locationType") }
        locationInOrNear { $("#advs-locationType option", value: 'I') }

        countryCodePopup { $("#advs-countryCode") }
        countryCodeCheckboxes { countryCode -> $("#advs-countryCode option", value: countryCode) }
        waitForCountryCode(wait: true) { $("#advs-countryCode").displayed }

        jobTitle { $("#advs-title") }
        search() { $(".submit-advs") }

        alert {
            $("#global-alert-queue .alert strong")
        }

        pagination(required: false) { $("#results-pagination").module(Pagination) }

        people {
            $("#results .people").moduleList(DiscoveredPerson)
        }

        showIndustries {
            $("#facet-I button.facet-toggle")
        }

        addIndustry {
            $("#facet-I button.add-facet-button")
        }

        addIndustryTextPrompt {
            $("#facet-I input", name: "f_I")
        }

        addIndustryAutoSuggest(wait: true) {
            $(".yui-ac-container .yui-ac-content div").find { it.displayed }
        }

        industryCheckboxes(required: false) { industryCode ->
            $("#facet-I input", value: "$industryCode")
        }

        resultsCount {
            $("#results_count .search-info strong")
        }
    }

    def waitForLoading() {
        waitFor { popupIsActive }
    }

    def perform(SearchSpecification searchSpec) {
        jobTitle << "\"$searchSpec.jobTitle\""
        locationPopup.click()
        locationInOrNear.click()
        waitForCountryCode


        countryCodePopup.click()

        countryCodeCheckboxes(searchSpec.countryCode).click()

        search.click()
        waitUntilLoaded

        showIndustries.click()
        searchSpec.industry.each { industry ->
            addIndustry.click()
            browser.report("yyo")
            def element = browser.driver.switchTo().activeElement()
            println "$element.tagName"
            element.sendKeys(industry)
            addIndustryAutoSuggest.click()
            waitUntilLoaded
        }

        println "Results: ${resultsCount.text()}"

        new Iterable<Collection<DiscoveredPerson>>() {

            int iteration = 0

            @Override
            Iterator<Collection<DiscoveredPerson>> iterator() {
                new Iterator<Collection<DiscoveredPerson>>() {
                    @Override
                    boolean hasNext() {
                        if (iteration == 0) {
                            true
                        } else {
                            pagination.displayed && pagination.hasNextPage
                        }
                    }

                    @Override
                    Collection<DiscoveredPerson> next() {
                        if (iteration == 0) {
                            iteration++
                        } else {
                            pagination.nextPage.click()
                            waitUntilLoaded
                        }
                        people
                    }
                }
            }
        }
    }

    boolean checkLimit() {
        boolean limit = alert.text() == "Due to excessive searching, your people search results are limited to your 1st-degree connections for security reasons. This restriction will be lifted in 24 hours."
        if (limit) {
            throw new SearchLimitReached()
        }
    }
}

class Pagination extends Module {
    static content = {
        activePage { $("li.active") }
        nextPage { $(".next") }
        hasNextPage { $(".next").displayed }
        pages { $("li") }

    }


}

class DiscoveredPerson extends Module {

    private static content = {
        name { $("a.main-headline") }
        description { $(".description") }
        button { $(".srp-actions a.primary-action-button") }
    }


    String fullName() {
        name.text()
    }

    boolean isOpenForConnection() {
        button.text() == "Connect"
    }

    def connect() {
        button.click()
    }
}
