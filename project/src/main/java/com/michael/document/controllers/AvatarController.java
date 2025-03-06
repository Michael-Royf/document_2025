package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.domain.response.Response;
import com.michael.document.services.AvatarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static com.michael.document.utils.ResponseUtils.getResponse;

@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
@Slf4j
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping(value = "/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<?> getAvatar(@PathVariable("filename") String filename) throws IOException {
        byte[] profileImage = avatarService.getAvatar(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(new ByteArrayResource(profileImage));
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> updateAvatar(@AuthenticationPrincipal User user,
                                                       @RequestParam("file") MultipartFile file,
                                                       HttpServletRequest request) throws IOException {
        var avatarUrl = avatarService.updateAvatar(user, file);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("avatarUrl", avatarUrl),
                        "Avatar update successfully.",
                        OK));
    }



    @PatchMapping("/delete")
    public ResponseEntity<Response> deleteProfileImage(@AuthenticationPrincipal User user,
                                                       HttpServletRequest request) throws IOException {
        var avatarUrl = avatarService.deleteUserAvatarAndSetDefaultAvatar(user);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("avatarUrl", avatarUrl),
                        "Avatar delete successfully.",
                        OK));
    }
}
