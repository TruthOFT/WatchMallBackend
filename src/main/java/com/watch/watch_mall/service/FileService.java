package com.watch.watch_mall.service;

import com.watch.watch_mall.common.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String biz);
}
