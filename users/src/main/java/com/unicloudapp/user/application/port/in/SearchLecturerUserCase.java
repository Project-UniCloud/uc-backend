package com.unicloudapp.user.application.port.in;

import com.unicloudapp.common.user.UserFullNameAndLoginProjection;

import java.util.List;

public interface SearchLecturerUserCase {

    List<UserFullNameAndLoginProjection> searchLecturers(String containsQuery);
}
