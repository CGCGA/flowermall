package com.scnu.upload.controller;


import com.scnu.upload.service.PicService;
import com.scnu.vo.PicUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PicController {
    @Autowired
    private PicService picService;

    @RequestMapping("/pic/upload")
    public PicUploadResult picUpload(MultipartFile pic) {
        PicUploadResult result = picService.picUpload(pic);
        return result;
    }

}
