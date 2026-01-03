package com.watch.watch_mall.service.impl;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String uploadFile(MultipartFile file, String biz) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        try {
            File dir = new File(uploadPath + biz);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(uploadPath + biz + "/" + fileName);
            file.transferTo(dest);
            return "/api/uploads/" + biz + "/" + fileName;

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
    }
}
