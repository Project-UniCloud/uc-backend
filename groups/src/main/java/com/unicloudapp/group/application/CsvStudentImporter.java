package com.unicloudapp.group.application;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.unicloudapp.common.user.StudentBasicData;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
class CsvStudentImporter implements StudentImporterPort {

    public List<StudentBasicData> parseCsv(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CsvToBean<StudentCsv> csvToBean = new CsvToBeanBuilder<StudentCsv>(reader)
                    .withType(StudentCsv.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .withThrowExceptions(true)
                    .build();


            List<StudentBasicData> studentBasicDataList = csvToBean.parse()
                    .stream()
                    .map(studentCsv -> new StudentBasicData(
                            studentCsv.getFirstName(),
                            studentCsv.getLastName(),
                            "s" + studentCsv.getLogin(),
                            studentCsv.getEmail()
                    ))
                    .toList();
            if (studentBasicDataList.isEmpty() || studentBasicDataList.stream().anyMatch(s -> s.getLogin() == null)) {
                throw new RuntimeException("CSV format is invalid or missing required columns");
            }
            return studentBasicDataList;

        }
    }
}
