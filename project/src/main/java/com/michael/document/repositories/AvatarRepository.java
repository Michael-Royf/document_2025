package com.michael.document.repositories;

import com.michael.document.entity.AvatarEntity;
import com.michael.document.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<AvatarEntity, Long> {
    Optional<AvatarEntity> findAvatarEntityByUser(UserEntity user);

    Optional<AvatarEntity> findAvatarEntityByFileName(String filename);

    Optional<AvatarEntity> findAvatarEntityByAvatarURL(String profileImageURL);
}
