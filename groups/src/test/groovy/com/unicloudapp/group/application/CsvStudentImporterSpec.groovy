package com.unicloudapp.group.application

import com.opencsv.exceptions.CsvRequiredFieldEmptyException
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification
import spock.lang.Subject

class CsvStudentImporterSpec extends Specification {

    @Subject
    def csvStudentImporter = new CsvStudentImporter()

    def "should parse valid CSV file with student data"() {
        given:
        def csvContent = """firstName,lastName,login,email
John,Doe,john.doe,john.doe@example.com
Jane,Smith,jane.smith,jane.smith@example.com"""
        def file = new MockMultipartFile(
                "students.csv",
                "students.csv",
                "text/csv",
                csvContent.getBytes()
        )

        when:
        def result = csvStudentImporter.parseCsv(file)

        then:
        result.size() == 2
        result[0].firstName == "John"
        result[0].lastName == "Doe"
        result[0].login == "john.doe"
        result[0].email == "john.doe@example.com"
        result[1].firstName == "Jane"
        result[1].lastName == "Smith"
        result[1].login == "jane.smith"
        result[1].email == "jane.smith@example.com"
    }

    def "should throw IOException when file has invalid format"() {
        given:
        def csvContent = """invalid,format
John,Doe,john.doe,john.doe@example.com"""
        def file = new MockMultipartFile(
                "students.csv",
                "students.csv",
                "text/csv",
                csvContent.getBytes()
        )

        when:
        csvStudentImporter.parseCsv(file)

        then:
        thrown(RuntimeException)
    }
} 