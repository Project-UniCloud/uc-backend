package com.unicloudapp.group.application


import org.springframework.mock.web.MockMultipartFile
import spock.lang.Specification
import spock.lang.Subject

class CsvStudentImporterSpec extends Specification {

    @Subject
    def csvStudentImporter = new CsvStudentImporter()

    def "should parse valid CSV file with student data"() {
        given:
        def csvContent = """"os_id";"nr_albumu";"nr_karty_bibl";"nazwisko";"imie";"imie2";"email";"termin1_ocena";"termin1_komentarz_pryw";"termin1_komentarz_pub";"termin1_data_uzyskania";"termin2_ocena";"termin2_komentarz_pryw";"termin2_komentarz_pub";"termin2_data_uzyskania"
"545578";"473601";"";"Bialko";"Pavel";"";"pavbia@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555866";"481841";"";"Głowacki";"Przemysław";"";"przglo@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555898";"481873";"";"Hornung";"Bartosz";"";"barhor1@st.amu.edu.pl";"";"";"";"";"";"";"";""
"546199";"474191";"";"Jarocki";"Stanisław";"Marcel";"stajar1@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555881";"481856";"";"Jóźwiak";"Szymon";"";"szyjoz1@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555834";"481809";"";"Kuczerenko";"Władysław";"";"wlakuc@st.amu.edu.pl";"";"";"";"";"";"";"";""
"558375";"153523";"";"Lytvynenko";"Stanislav";"";"stalyt@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555896";"481871";"";"Łuczak";"Michał";"Tomasz";"micluc2@st.amu.edu.pl";"";"";"";"";"";"";"";""
"555858";"481833";"";"Miszczak";"Ksawery";"";"ksamis@st.amu.edu.pl";"";"";"";"";"";"";"";""
"534989";"464833";"";"Przysowa";"Konrad";"";"konprz5@st.amu.edu.pl";"";"";"";"";"";"";"";""
"546144";"474141";"";"Putsik";"Yan";"";"yanput@st.amu.edu.pl";"";"";"";"";"";"";"";""
"559869";"485704";"";"Wieczór";"Jakub";"Leszek";"jakwie13@st.amu.edu.pl";"";"";"";"";"";"";"";""
"""
        def file = new MockMultipartFile(
                "students.csv",
                "students.csv",
                "text/csv",
                csvContent.getBytes()
        )

        when:
        def result = csvStudentImporter.parseCsv(file)

        then:
        result.size() == 12
        result[0].firstName == "Pavel"
        result[0].lastName == "Bialko"
        result[0].login == "s473601"
        result[0].email == "pavbia@st.amu.edu.pl"
    }

    def "should throw IOException when file has invalid format"() {
        given:
        def csvContent = """invalid,format
John;Doe,john.doe,john.doe@example.com"""
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