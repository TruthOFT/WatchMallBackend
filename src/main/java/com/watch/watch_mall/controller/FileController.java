package com.watch.watch_mall.controller;

import com.watch.watch_mall.common.BaseResponse;
import com.watch.watch_mall.common.ResultUtils;
import com.watch.watch_mall.model.dto.file.UploadFileRequest;
import com.watch.watch_mall.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile file,
                                           @RequestParam("biz") String biz) {
        return ResultUtils.success(fileService.uploadFile(file, biz));
    }
}
