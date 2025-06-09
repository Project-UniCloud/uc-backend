package com.unicloudapp.group.domain

import spock.lang.Specification

class StartDateTest extends Specification {

    def "Of"() {
        when: "A valid date is provided"
        StartDate.of(null)

        then: "It should return a StartDate instance with the provided date"
        thrown(IllegalArgumentException.class)
    }
}
