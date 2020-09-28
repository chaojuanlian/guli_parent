package com.atguigu.vod.controller;

import com.atguigu.vod.service.VodService;
import com.guigu.commonutils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;


    // 上传视频到阿里云
    @PostMapping("uploadAlyVideo")
    public R uploadAlyVideo(MultipartFile file){
        // 返回上传后的视频id
        String videoId = vodService.uploadVideoAly(file);

        return R.ok().data("videoId",videoId);
    }

    // 根据视频id删除阿里云视频
    @DeleteMapping("removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable String id){
        vodService.deleteVideoById(id);
        return R.ok();
    }
}
