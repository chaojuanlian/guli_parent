package com.atguigu.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    // 上传文件到 oss
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        // 工具类获取值
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtils.END_POINT;
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;


        try {

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取上传文件 的 输入流。
            InputStream inputStream = file.getInputStream();

            //获取文件名称
            String filename = file.getOriginalFilename();

            // 1. 在文件名称前面添加唯一随机值，让每个文件名称不同
            // 生成的 uuid有很多 下划线，去除下划线，不去也可以
            String uuid = UUID.randomUUID().toString().replaceAll("-","");

            // 2. 把文件按照日期进行分类
            // 2020/09/07/01.jpg
            // 获取当前日期  DateTime 是 joda.time 工具类提供的生成日期的接口
            String datePath = new DateTime().toString("yyyy/MM/dd");
            filename = datePath + "/" + uuid + filename;

            // 调用oss的方法 实现上传
            // 第一个参数    BucketName  Bucket名称
            // 第二个参数    上传到oss的文件 路径和文件名称   /aa/bb/file.jpg
            // 第三个参数    上传文件的输入流
            ossClient.putObject(bucketName, filename, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            // 把上传之后文件拼接出来
            // 需要把上传到阿里云oss路径手动拼接出来
            // https://edu-chao.oss-cn-beijing.aliyuncs.com/01.jpg
            String url = "https://" + bucketName + "." + endpoint + "/" + filename;
            return url;
        } catch (Exception e){
            new GuliException(20001,"文件上传失败");
            return null;
        }



    }
}
