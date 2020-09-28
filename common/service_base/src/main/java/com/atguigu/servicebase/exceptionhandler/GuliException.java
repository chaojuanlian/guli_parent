package com.atguigu.servicebase.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 *
 * @program: GuliException
 * @description: 自定义异常
 * @author: WangYuChao
 * @create: 2020/7/26 19:28
 */

@Data
@AllArgsConstructor //生成有参数的构造方法
@NoArgsConstructor  //生成无参数的构造方法
public class GuliException extends RuntimeException{
    private Integer code;//状态码
    private String msg;//异常信息
}
