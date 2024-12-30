package org.example.carsharingapi.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enums.UserRole;

@Getter
@Setter
public class UpdateUserRoleDto {
    @NotEmpty
    private Set<UserRole> roles;
}
