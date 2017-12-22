package com.happyshop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ming
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
