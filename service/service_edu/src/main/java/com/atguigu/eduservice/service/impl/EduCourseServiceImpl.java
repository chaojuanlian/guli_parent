package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVO;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-13
 */
@Service
@Transactional
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    //注入小节和章节service
    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private EduVideoService videoService;


    //添加课程基本信息的方法
    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        // 1. 向课程表添加课程基本信息

        EduCourse course = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,course);
        int insert = baseMapper.insert(course);  //返回值为影响行数

        if (insert == 0){
            throw new GuliException(20001,"添加课程基本信息失败");
        }

        // 2. 向课程简介表添加课程简介
        EduCourseDescription courseDescription = new EduCourseDescription();
        BeanUtils.copyProperties(courseInfoVo,courseDescription);
        //设置描述id就是课程id
        courseDescription.setId(course.getId());
        courseDescriptionService.save(courseDescription);

        return course.getId();
    }

    // 根据课程id查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {

        // 1 查询课程表
        EduCourse course = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(course,courseInfoVo);

        // 2 查询描述表
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());
        return courseInfoVo;
    }

    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        // 1.修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int i = baseMapper.updateById(eduCourse);
        if (i == 0){
            throw new GuliException(20001,"修改课程信息失败");
        }

        // 2.修改描述表
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoVo.getId());
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    @Override
    public CoursePublishVO publicCourseInfo(String id) {
        CoursePublishVO coursePublishVO = baseMapper.getPublicCourseInfo(id);
        return coursePublishVO;
    }

    // 删除课程
    @Override
    public void removeCourse(String courseId) {
        // 1 根据课程id删除小节
        videoService.removeVideoByCourseId(courseId);
        // 2 根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);
        // 3 根据课程id删除描述
        courseDescriptionService.removeById(courseId);
        // 4 根据课程id删除课程本身
        int result = baseMapper.deleteById(courseId);
        if (result==0) {    // 失败返回
            throw new GuliException(20001,"删除课程失败");
        }

    }
}
