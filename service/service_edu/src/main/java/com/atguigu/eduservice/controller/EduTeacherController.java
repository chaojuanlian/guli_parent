package com.atguigu.eduservice.controller;


import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQueryVO;
import com.atguigu.eduservice.service.EduTeacherService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-07-11
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")  //类地址/必须加，方法地址/可以不加
@CrossOrigin
public class EduTeacherController {


    //访问地址 http://localhost:8001/eduservice/teacher/findAll
    //把service注入
    @Autowired
    private EduTeacherService eduTeacherService;

    //1.查询讲师表所有数据
    //rest风格
    @ApiOperation(value = "所有讲师列表")
    @GetMapping("findAll")
    public R findAllTeacher(){
        //调用service里面的方法实现查询所有的操作
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items",list);

    }

    //2.逻辑删除讲师的方法
    @ApiOperation(value = "逻辑删除讲师")
    @DeleteMapping("{id}")
    public R removeTeacher(@ApiParam(name = "id", value = "讲师ID",required = true) @PathVariable String id){
        boolean flag = eduTeacherService.removeById(id);
        if (flag) {
            return R.ok();
        }else {
            return R.error();
        }

    }

    //3.分页查询讲师的方法  current 当前页，size 每页记录数
    @ApiOperation(value = "分页查询讲师的方法")
    @GetMapping("pageTeacher/{current}/{size}")
    public R pageListTeacher(@ApiParam(name = "current", value = "当前页",required = true) @PathVariable long current,
                             @ApiParam(name = "size", value = "每页记录数",required = true) @PathVariable long size){
        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current,size);
        //调用方法实现分页
        //调用方式时候，底层封装，吧分页所有数据分装到 pageTeacher对象里面
        eduTeacherService.page(pageTeacher, null);
        long total = pageTeacher.getTotal();        //总记录数
        List<EduTeacher> records = pageTeacher.getRecords();    //数据list结合

        /* 第一种方法
        Map map = new HashMap<>();
        map.put("total",total);
        map.put("rows",records);
        return.R.ok().data(map);
        */
        //第二种方法，两种方法都可以
        return R.ok().data("total",total).data("rows",records);
    }

    //4.条件查询带分页方法
    @PostMapping("pageTeacherCondition/{current}/{size}")
    public R pageTeacherCondition(@PathVariable long current,
                                  @PathVariable long size,
                                  @RequestBody(required = false) TeacherQueryVO teacherQueryVO,
                                  HttpServletResponse response){
        //此处为新增处
        response.setHeader("Access-Control-Allow-Origin", "*");
        //创建page对象
        Page<EduTeacher> page = new Page<>(current,size);
        //构建条件
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        //多条件组合查询
        //mybatis学过 动态sql
        if (teacherQueryVO != null) {
            String name = teacherQueryVO.getName();
            Integer level = teacherQueryVO.getLevel();
            String begin = teacherQueryVO.getBegin();
            String end = teacherQueryVO.getEnd();
            //判断条件值是否为空，如果不为空拼接条件
            if(!StringUtils.isEmpty(name)){
                wrapper.like("name",name);
            }
            if (!StringUtils.isEmpty(level)) {
                wrapper.eq("level", level);
            }
            if (!StringUtils.isEmpty(begin)) {
                wrapper.ge("gmt_create",begin);
            }
            if(!StringUtils.isEmpty(end)){
                wrapper.le("gmt_create",end);
            }
        }
        //排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现条件查询份额与
        eduTeacherService.page(page,wrapper);
        long total = page.getTotal();        //总记录数
        List<EduTeacher> records = page.getRecords();    //数据list结合
        return R.ok().data("total",total).data("rows",records);
    }

    //添加讲师
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        boolean save = eduTeacherService.save(eduTeacher);
        if (save) {
            return R.ok();
        }else {
            return R.error();
        }
    }

    //根据讲师id查询
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        try {
            EduTeacher eduTeacher = eduTeacherService.getById(id);
            return R.ok().data("teacher",eduTeacher);
        } catch (Exception e) {
            //执行自定义异常
            throw new GuliException(20001,"执行了自定义异常处理……");
        }
    }

    //讲师修改
    @PostMapping("updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher){
        boolean update = eduTeacherService.updateById(eduTeacher);
        if (update) {
            return R.ok();
        }else {
            return R.error();
        }
    }
}

