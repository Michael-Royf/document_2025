package com.michael.document.repositories;

import com.michael.document.entity.ConfirmationEntity;
import com.michael.document.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {
    Optional<ConfirmationEntity> findByKey(String key);

    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
}
