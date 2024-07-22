package Podogonnet.App.enums;


import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Rol {
    USER(Arrays.asList(RolPermission.READE_MY_PROFILE
            )), ADMIN(Arrays.asList(RolPermission.CREATE_ONE_SERVICIO,
            RolPermission.UPDATE_ONE_SERVICIO,
            RolPermission.READE_ALL_SERVICIO,
            RolPermission.READE_ONE_SERVICIO,
            RolPermission.DISABLE_ONE_TURNO,
            RolPermission.UPDATE_ONE_SERVICIO));

    public void setPermissions(List<RolPermission> permissions) {
        this.permissions = permissions;
    }

    public List<RolPermission> getPermissions() {
        return permissions;
    }

    List<RolPermission>permissions;
}
