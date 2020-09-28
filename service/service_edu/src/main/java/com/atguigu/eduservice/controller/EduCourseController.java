package com.atguigu.eduservice.controller;


import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVO;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.commonutils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-13
 */
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    // 课程列表，基本实现
    // TODO  完善条件查询带分页
    @GetMapping("findAll")
    public R getCourseList(){
        List<EduCourse> list = courseService.list(null);
        return R.ok().data("list",list);
    }

    // 条件查询带分页方法
    @PostMapping("pageCourseCondition/{current}/{size}")
    public R pageCourseCondition(@PathVariable long current,
                                  @PathVariable long size,
                                  @RequestBody(required = false) CourseInfoVo courseInfoVo,
                                  HttpServletResponse response){
        //此处为新增处
        response.setHeader("Access-Control-Allow-Origin", "*");
        //创建page对象
        Page<EduCourse> page = new Page<>(current,size);
        //构建条件
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //多条件组合查询
        //mybatis学过 动态sql
        if (courseInfoVo != null) {
            String title = courseInfoVo.getTitle();
            String status = courseInfoVo.getStatus();
            //判断条件值是否为空，如果不为空拼接条件
            if(!StringUtils.isEmpty(title)){
                wrapper.like("title",title);
            }
            if (!StringUtils.isEmpty(status)) {
                wrapper.eq("status", status);
            }
        }
        //排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现条件查询与分页
        courseService.page(page,wrapper);
        long total = page.getTotal();        //总记录数
        List<EduCourse> records = page.getRecords();    //数据list结合
        return R.ok().data("total",total).data("rows",records);
    }


    //添加课程基本信息的方法
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId",id);
    }

    // 根据课程id查询课程基本信息
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo",courseInfoVo);
    }

    //修改课程信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }


    // 根据课程id查询课程确认信息
    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVO coursePublishVO = courseService.publicCourseInfo(id);
        return R.ok().data("publishCourse",coursePublishVO);
    }

    // 课程最终发布
    // 修改课程状态
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse course = new EduCourse();
        course.setId(id);
        course.setStatus("Normal");   // '课程状态 Draft未发布  Normal已发布'
        courseService.updateById(course);
        return R.ok();
    }

    // 删除课程
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }

}

