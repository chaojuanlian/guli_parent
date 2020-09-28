package com.atguigu.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.utils.ConstantVodUtil;
import com.atguigu.vod.utils.InitObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class VodServiceImpl implements VodService {
    // 上传视频到阿里云
    @Override
    public String uploadVideoAly(MultipartFile file) {
        try {
            // accessKeyId, accessKeySecret
            // title,           上传到阿里云视频后显示的文件名
            // fileName,        上传文件原始文件名
            // inputStream  文件的输入流
            String accessKeyId = ConstantVodUtil.ACCESS_KEY_ID;
            String accessKeySecret = ConstantVodUtil.ACCESS_KEY_SECRET;
            String fileName = file.getOriginalFilename();
            String title = fileName.substring(0,fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(accessKeyId,accessKeySecret, title, fileName, inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
            }
            return videoId;
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001,"上传到阿里云视频失败");
        }
    }

    // 根据视频id删除阿里云视频
    @Override
    public void deleteVideoById(String id) {
        try {
            String accessKeyId = ConstantVodUtil.ACCESS_KEY_ID;
            String accessKeySecret = ConstantVodUtil.ACCESS_KEY_SECRET;
            // 1. 初始化对象
            DefaultAcsClient client = InitObject.initVodClient(accessKeyId, accessKeySecret);
            //2. 创建对应的 request和response对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            DeleteVideoResponse response = new DeleteVideoResponse();
            //3. 设置视频id id可以是多个，用 逗号 隔开
            request.setVideoIds(id);
            // 调用方法删除视频
            response = client.getAcsResponse(request);

        } catch (Exception e){
            throw new GuliException(20001,"删除阿里云视频出错！");
        }
    }
}
