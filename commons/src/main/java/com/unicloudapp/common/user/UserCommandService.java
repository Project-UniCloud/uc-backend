package com.unicloudapp.common.user;

import com.unicloudapp.common.vo.user.UserId;
import java.util.List;

public interface UserCommandService {

    List<UserId> importStudents(List<StudentBasicData> studentBasicData);

    UserId createStudent(StudentBasicData studentBasicData);

    void createUser(UserCreateCommand userCreateCommand);
}
