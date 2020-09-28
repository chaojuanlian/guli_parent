package com.atguigu.vod.service;

import org.springframework.web.multipart.MultipartFile;

public interface VodService {
    // 上传视频到阿里云
    String uploadVideoAly(MultipartFile file);

    // 根据视频id删除阿里云视频
    void deleteVideoById(String id);
}
