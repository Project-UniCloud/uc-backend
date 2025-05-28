package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.projection.UserFullNameProjection;

import java.util.List;

public interface SearchLecturerUserCase {

    List<UserFullNameProjection> searchLecturers(String containsQuery);
}
