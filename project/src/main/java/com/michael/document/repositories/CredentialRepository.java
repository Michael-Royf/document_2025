package com.michael.document.repositories;

import com.michael.document.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository  extends JpaRepository<CredentialEntity, Long> {
    Optional<CredentialEntity> getCredentialEntityByUserEntityId(Long userId);
}
