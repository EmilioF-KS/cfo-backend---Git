package com.cfo.reporting.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public String storeFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return null;
    }
}
