package com.watch.watch_mall.controller;

import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthCheck(role = "admin")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile file,
                                           @RequestParam("biz") String biz) {
        return ResultUtils.success(fileService.uploadFile(file, biz));
    }
}
