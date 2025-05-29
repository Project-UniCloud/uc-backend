package com.unicloudapp.group.application;

import com.unicloudapp.common.user.StudentBasicData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StudentImporterPort {

    List<StudentBasicData> parseCsv(MultipartFile file) throws IOException;
}
