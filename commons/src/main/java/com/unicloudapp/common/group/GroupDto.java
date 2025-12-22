package com.unicloudapp.common.group;

import com.unicloudapp.common.vo.user.UserId;
import java.util.Set;

public record GroupDto(Set<UserId> lecturers) {}
