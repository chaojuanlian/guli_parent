package com.atguigu.eduservice.controller;


import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.service.EduVideoService;
import com.guigu.commonutils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-13
 */
@RestController
@RequestMapping("/eduservice/video")
@CrossOrigin
public class EduVideoController {

    @Autowired
    private EduVideoService videoService;

    // 注入VodClient
    @Autowired
    private VodClient vodClient;

    // 添加小节
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        videoService.save(eduVideo);
        return R.ok();
    }
    // 删除小节 同时把里面的视频删除
    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id){
        // 根据小节id获取视频id，调用方法实现删除
        EduVideo eduVideo = videoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();

        // 判断小节里面是否有视频id
        if (!StringUtils.isEmpty(videoSourceId)){
            // 根据视频id，远程调用实现视频删除
            vodClient.removeAlyVideo(videoSourceId);
        }
        // 删除小节
        boolean b = videoService.removeById(id);
        if (b) {
            return R.ok();
        }else {
            return R.error();
        }
    }


    // 修改小节  TODO
    //  根据 视频id 查询视频
    @GetMapping("getVideoById/{id}")
    public R getVideoById(@PathVariable String id){
        EduVideo video = videoService.getById(id);
        return R.ok().data("video",video);
    }

    // 修改小节
    @PostMapping("updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo){
        boolean b = videoService.updateById(eduVideo);
        if (b) {
            return R.ok();
        }else {
            return R.error();
        }
    }

}

