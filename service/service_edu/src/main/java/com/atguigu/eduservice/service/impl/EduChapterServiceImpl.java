package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.chapter.ChapterVo;
import com.atguigu.eduservice.entity.chapter.VideoVo;
import com.atguigu.eduservice.mapper.EduChapterMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-13
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {
    @Autowired
    private EduVideoService videoService;       //注入小节服务

    //课程大纲列表  根据课程id进行查询
    @Override
    public List<ChapterVo> getChapterVideo(String courseId) {

        // 1 查询所有 章节信息
        QueryWrapper<EduChapter> queryChapter = new QueryWrapper<>();
        queryChapter.eq("course_id",courseId);
        List<EduChapter> eduChapterList = baseMapper.selectList(queryChapter);

        // 2 查询所有 小节信息
        QueryWrapper<EduVideo> queryVideo = new QueryWrapper<>();
        queryVideo.eq("course_id",courseId);
        List<EduVideo> videoList = videoService.list(queryVideo);

        // 创建list 用于最后封装数据
        List<ChapterVo> finalList = new ArrayList<>();

        // 3 封装章节信息

        for (EduChapter chapter : eduChapterList) {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter,chapterVo);
            finalList.add(chapterVo);

            // 4 封装所有小节信息
            // 创建集合，用于封装 章节中的小节
            List<VideoVo> videoVoList = new ArrayList<>();
            for (EduVideo eduVideo : videoList) {
                if (eduVideo.getChapterId().equals(chapter.getId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo,videoVo);
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
        }
        return finalList;
    }

    @Override
    public boolean deleteChapter(String chapterId) {
        // 根据章节id查询是否有小节
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("chapter_id",chapterId);
        int count = videoService.count(queryWrapper);

        if (count > 0){  // 大于0 说明章节中有小节  不进行删除
            throw new GuliException(20001,"不能删除章节，请先删除小节");
        }else {     //没有数据，进行删除
            //删除章节
            int result = baseMapper.deleteById(chapterId);
            return result > 0;
        }
    }

    // 2 根据课程id删除章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
