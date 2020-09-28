package com.atguigu.oss.controller;


import com.atguigu.oss.service.OssService;
import com.guigu.commonutils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/eduoss/fileoss")
@CrossOrigin
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping
    public R uploadOssFile(MultipartFile file){
        // 获取上次文件
        // 方法返回 上传到 oss的文件路径
        String filePath = ossService.uploadFileAvatar(file);

        return R.ok().data("url",filePath);
    }
}
