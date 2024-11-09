package com.marsview.dto;

import com.marsview.domain.Roles;
import lombok.Data;

@Data
public class RolesDto extends Roles {
    private String project_id;
}
