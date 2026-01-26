package com.watch.watch_mall.service.impl;

import com.watch.watch_mall.common.ErrorCode;
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
    @Value("${server.servlet.context-path}")
    String contextPath;

    @Override
    public String uploadFile(MultipartFile file, String biz) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名非法");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        try {
            // 项目根目录
            String projectPath = System.getProperty("user.dir");

            // upload/{biz}
            File dir = new File(projectPath + "/upload/" + biz);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建目录失败");
            }

            File dest = new File(dir, fileName);
            file.transferTo(dest);

            return contextPath + "/uploads/" + biz + "/" + fileName;

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
    }
}

