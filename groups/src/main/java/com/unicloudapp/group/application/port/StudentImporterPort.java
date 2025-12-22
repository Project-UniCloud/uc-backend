package com.unicloudapp.group.application.port;

import com.unicloudapp.common.user.StudentBasicData;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface StudentImporterPort {

    List<StudentBasicData> parseCsv(MultipartFile file) throws IOException;
}
