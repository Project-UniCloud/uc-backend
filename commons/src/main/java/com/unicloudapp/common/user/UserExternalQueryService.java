package com.unicloudapp.common.user;

import java.util.List;

public interface UserExternalQueryService {

    List<UserFullNameAndLoginProjection> searchLecturers(String containsQuery);    
}
