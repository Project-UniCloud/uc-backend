package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.projection.UserRowProjection;
import org.springframework.data.domain.Page;

public interface FindAllLecturersUseCase {

    Page<UserRowProjection> findAllLecturers(int offset, int size);
}
