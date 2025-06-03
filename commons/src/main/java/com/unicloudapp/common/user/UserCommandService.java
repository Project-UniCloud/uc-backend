package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.UserId;

import java.util.List;

public interface UserCommandService {

    List<UserId> importStudents(List<StudentBasicData> studentBasicData);

    UserId createStudent(StudentBasicData studentBasicData);
}
