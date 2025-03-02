package com.michael.document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.michael.document.entity.base.Auditable;
import com.michael.document.enumerations.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoleEntity  extends Auditable {
    private String name;
    private Authority authorities;
}
