package com.example.cv_reranking.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveProfileImage(String userKey, MultipartFile file);

    String saveCvPrivate(String userKey, MultipartFile file);
}