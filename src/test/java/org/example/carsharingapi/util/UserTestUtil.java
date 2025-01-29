package org.example.carsharingapi.util;

import java.util.HashSet;
import java.util.Set;
import org.example.carsharingapi.dto.user.UserDto;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;

public class UserTestUtil {
    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Old First Name");
        user.setSecondName("Old Second Name");
        user.setRoles(new HashSet<>(Set.of(UserRole.CUSTOMER)));
        return user;
    }

    public static UserDto getUserDto() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setFirstName("Old First Name");
        user.setSecondName("Old Second Name");
        user.setRoles(new HashSet<>(Set.of(UserRole.CUSTOMER)));
        return user;
    }
}
