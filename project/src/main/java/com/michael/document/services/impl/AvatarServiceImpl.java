package com.michael.document.services.impl;

import com.michael.document.domain.User;
import com.michael.document.entity.AvatarEntity;
import com.michael.document.entity.UserEntity;
import com.michael.document.exception.payload.ApiException;
import com.michael.document.exception.payload.NotFoundException;
import com.michael.document.repositories.AvatarRepository;
import com.michael.document.repositories.UserRepository;
import com.michael.document.services.AvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static com.michael.document.utils.FileCompressor.compressData;
import static com.michael.document.utils.FileCompressor.decompressData;
import static org.springframework.http.MediaType.*;
import static com.michael.document.constants.AppConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;

    @Override
    public void saveTempAvatar(UserEntity user) throws IOException {
        var filename = user.getUserId() + PNG_EXTENSION;
        AvatarEntity avatar = AvatarEntity.builder()
                .user(user)
                .fileName(filename)
                .fileType(IMAGE_JPEG_VALUE)
                .data(compressData(getDefaultAvatar(user.getUsername())))
                .avatarURL(setAvatarUrl(filename))
                .build();
        avatar = avatarRepository.save(avatar);
        user.setAvatarUrl(avatar.getAvatarURL());
        userRepository.save(user);
        log.info("Saved avatar in database by name: {}", avatar.getFileName());
    }

    @Override
    public byte[] getAvatar(String fileName) {
        return decompressData(findAvatarInDB(fileName).getData());
    }

    @Override
    public String updateAvatar(User user, MultipartFile avatar) throws IOException {
        if (avatar != null) {
            if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(avatar.getContentType())) {
                throw new ApiException(String.format(NOT_AN_IMAGE_FILE, avatar.getOriginalFilename()));
            }
            AvatarEntity avatarEntityDB = findAvatarByURL(user.getAvatarUrl());
            avatarEntityDB.setData(compressData(avatar.getBytes()));
            avatarEntityDB.setFileType(avatar.getContentType());
            avatarRepository.save(avatarEntityDB);
            log.info("Saved new avatar in database by name: {}", avatar.getOriginalFilename());
            return avatarEntityDB.getAvatarURL();
        } else {
            throw new ApiException(AVATAR_NOT_FOUND);
        }
    }

    @Override
    public String deleteUserAvatarAndSetDefaultAvatar(User user) throws IOException {
        var userEntity = userRepository.findUserEntityByUserId(user.getUserId())
                .orElseThrow(()-> new NotFoundException(String.format(NO_USER_FOUND_BY_ID, user.getUserId())));
        AvatarEntity avatarEntity = findAvatarByURL(userEntity.getAvatarUrl());
        avatarEntity.setData(compressData(getDefaultAvatar(user.getUsername())));
        avatarEntity.setFileType(IMAGE_JPEG_VALUE);
        avatarRepository.save(avatarEntity);
        return avatarEntity.getAvatarURL();
    }


    private AvatarEntity findAvatarByUserEntity(UserEntity userEntity) {
        return avatarRepository.findAvatarEntityByUser(userEntity)
                .orElseThrow(() -> new NotFoundException(String.format(NO_AVATAR_BY_USER, userEntity.getUsername())));
    }

    private AvatarEntity findAvatarInDB(String fileName) {
        return avatarRepository.findAvatarEntityByFileName(fileName)
                .orElseThrow(() -> new NotFoundException(String.format(NO_AVATAR_FOUND_BY_FILENAME, fileName)));
    }

    private AvatarEntity findAvatarByURL(String url) {
        return avatarRepository.findAvatarEntityByAvatarURL(url)
                .orElseThrow(() -> new NotFoundException(String.format(NO_AVATAR_FOUND_BY_URL, url)));
    }

    private byte[] getDefaultAvatar(String username) throws IOException {
        URL url;
        try {
            url = new URL(TEMP_AVATAR_BASE_URL + username);
        } catch (MalformedURLException e) {
            log.error("Invalid URL for username: " + username);
            return getDefaultAvatarFromFileSystem();
        }
        try (InputStream inputStream = url.openStream()) {
            return readBytesFromStream(inputStream);
        } catch (IOException e) {
            log.error("Error while fetching avatar from URL: " + url);
            return getDefaultAvatarFromFileSystem();
        }
    }


    // Метод для получения дефолтной аватарки из ресурсов проекта
    private byte[] getDefaultAvatarFromFileSystem() throws IOException {
        String defaultAvatarPath = "/defaultAvatar/149071.png";
        try (InputStream inputStream = getClass().getResourceAsStream(defaultAvatarPath)) {
            if (inputStream == null) {
                log.error("Default avatar not found at path: " + defaultAvatarPath);
                throw new ApiException("Default avatar not found.");
            }
            return readBytesFromStream(inputStream);
        }
    }

    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] chunk = new byte[1024];
        while ((bytesRead = inputStream.read(chunk)) > 0) {
            byteArrayOutputStream.write(chunk, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }


    private String setAvatarUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_AVATAR_PATH + filename).toUriString();
    }
}
