package com.michael.document.services;

import com.michael.document.domain.User;
import com.michael.document.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    void saveTempAvatar(UserEntity user) throws IOException;

    byte[] getAvatar(String fileName);

    String updateAvatar(User user, MultipartFile avatar) throws IOException;

    String deleteUserAvatarAndSetDefaultAvatar(User user) throws IOException;
}
