package com.atguigu.eduservice.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @program: TeacherQueryVO
 * @description: 多条件查询条件对象
 * @author: WangYuChao
 * @create: 2020/7/26 16:33
 */

@Data
public class TeacherQueryVO {

    @ApiModelProperty(value = "教室名称，模糊查询")
    private String name;
    @ApiModelProperty(value = "头衔 1高级讲师  2首席讲师")
    private Integer level;
    @ApiModelProperty(value = "查询开始时间",example = "2019-01-01 10:10:10")
    private String begin;
    @ApiModelProperty(value = "查询结束时间",example = "2019-01-01 10:10:10")
    private String end;
}
