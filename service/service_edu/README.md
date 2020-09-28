1.创建controller  
~~~ java
@RestController
@RequestMapping("/eduservice/teacher")  //类地址/必须加，方法地址/可以不加
public class EduTeacherController {
    //访问地址 http://localhost:8001/eduservice/teacher/findAll
    //把service注入
    @Autowired
    private EduTeacherService eduTeacherService;
    //1.查询讲师表所有数据
    //rest风格
    @GetMapping("findAll")
    public List<EduTeacher> findAllTeacher(){
        //调用service里面的方法实现查询所有的操作
        List<EduTeacher> list = eduTeacherService.list(null);
        return list;
    }
}
~~~
---    
2.创建启动类  
~~~ java
@SpringBootApplication
public class EduApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class, args);
    }
}
~~~  
3.创建配置类  
~~~ java 
@Configuration
@MapperScan("com.atguigu.eduservice.mapper")
public class EduConfig {
}
~~~  
4. 修改json的全局时间格式
~~~ xml

#返回json的全局时间格式
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
~~~  
###逻辑删除  
>第一步 表添加逻辑删除字段，对应实体类添加属性,属性添加注解 @TableLogic  
``` 
alter table edu_teacher 
   add is_deleted tinyint(1) default 0 null comment '逻辑删除 1（true） 0（false）';
```  
``` 
@TableLogic
    private Integer deleted;    //逻辑删除
```  
> 第二步 配置逻辑删除插件
```
   /**
    * 逻辑删除插件
    */
   @Bean
   public ISqlInjector sqlInjector(){
       return new LogicSqlInjector();
   }
```
```
#配置逻辑删除 删除 1  不删除 0，默认就是这个值，可以不写
mybatis-plus.global-config.db-config.logic-delete-value=1
mybatis-plus.global-config.db-config.logic-not-delete-value=0
```    
>3.编写controller中的逻辑删除方法  
```
@DeleteMapping("{id}")   -->  id值通过路径进行传递   localhost:8001/edu/delete/1
    public boolean removeTeacher(@PathVariable String id){   --> 通过 @PathVariable  获取路径中的id值
```  
>4.如何测试  因为delete提交浏览器不支持  
```
借助一些工具进行测试 
   1.swagger测试(重点)
   2.postman(了解)
```

###整合swagger2进行接口测试  
#####注意，swagger版本为2.9.2
1.生成在线接口文档  
2.方便接口测试  
--创建公共模块，整合swagger，为了所有模块都能进行使用  
--guli_parent创建子模块common，在common创建子模块 service_base  

--创建配置类
```
@Configuration //配置类注解
@EnableSwagger2 //swagger注解
public class com.atguigu.servicebase.SwaggerConfig {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-课程中心API文档")
                .description("本文的描述了课程中心微服务接口定义")
                .version("1.0")
                .contact(new Contact("java","http://atguigu.com","1499805280@qq.com"))
                .build();
    }
}
.paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*"))) //路径包括 admin error不显示
```  
--引入依赖  
```
在service模块中引入service_base 依赖
<dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>service_base</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```  
--在service_edu启动类添加注解，设置包扫描规则
```
@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu"})
public class EduApplication {
```  
-- 访问swagger
```
http://localhost:8001/swagger-ui.html
```  
2.配置swagger2生成API接口文档  
定义在雷伤：@Api
定义在方法是：@ApiOperation
定义在参数是：@ApiParam
```
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")  //类地址/必须加，方法地址/可以不加
public class EduTeacherController {
```  
```
    @ApiOperation(value = "所有讲师列表")
    @GetMapping("findAll")
    public List<EduTeacher> findAllTeacher(){
```  
```
    @ApiOperation(value = "逻辑删除讲师")
    @DeleteMapping("{id}")
    public boolean removeTeacher(@ApiParam(name = "id", value = "讲师ID",required = true) @PathVariable String id){
    
```  
###4.统一返回数据格式  
json数据格式 2种（两种格式混合使用）  
对象  
数组  
```
{
  "success": 布尔, //响应是否成功
  "code": 数字, //响应码
  "message": 字符串, //返回消息
  "data": HashMap //返回数据，放在键值对中
}
```  
第一步、在common模块创建子模块 common_utils  
第二步、创建interface，定义数据返回状态码  
```
* 成功 20000
* 失败 20001
public interface ResultCode {
    public static Integer SUCCESS = 20000;      //成功
    public static Integer ERROR = 20001;        //失败

}
```  
第三步 定义返回数据格式  
```java
package com.guigu.commonutils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @program: R
 * @description: 返回结果 类
 * @author: WangYuChao
 * @create: 2020/7/14 20:52
 */
@Data
public class R {
    @ApiModelProperty(value = "是否成功")
    private boolean success;
    @ApiModelProperty(value = "返回码")
    private Integer code;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private Map<String,Object> data = new HashMap<String,Object>();
    
    //把构造方法私有
    private R(){}
    
    //链式编程
    
    public static R ok(){
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }
    
    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }
    
    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }
    
    public R message(String message){
        this.setMessage(message);
        return this;
    }
    
    public R code(Integer code){
        this.setCode(code);
        return this;
    }
    
    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }
    
    public R data(Map<String,Object> map){
        this.setData(map);
        return this;
    }
        
}

```  
第四步 使用统一结果  
```
(1)在service中引入common_utils的依赖
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>common_utils</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>


    @ApiOperation(value = "所有讲师列表")
    @GetMapping("findAll")
    public R findAllTeacher(){
        //调用service里面的方法实现查询所有的操作
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items",list);

    }
```  
第五步 处理swagger显示日期为时间戳  
在响应的属性上，添加注解  
```
    @ApiModelProperty(value = "创建时间",example = "2018-10-01 12:18:48")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间",example = "2018-10-01 12:18:48")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date gmtModified;

```  
###5讲师分页功能  
1.配置mp分页插件  
```
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        // paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

```  
2.编写讲师分页查询接口的方法  
```

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
```  
###6条件查询带分页  
第一步、把条件值传递到接口里面
    把条件值封装到对象里面，把对象传递到接口里面  VO
`
```java
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
```  
第二步、 根据条件进行判断 拼接条件
```
//4.条件查询带分页方法
    @GetMapping("pageTeacherCondition/{current}/{size}")
    public R pageTeacherCondition(@PathVariable long current,
                                  @PathVariable long size,
                                  TeacherQueryVO teacherQueryVO){
        //创建page对象
        Page<EduTeacher> page = new Page<>(current,size);
        //构建条件
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        //多条件组合查询
        //mybatis学过 动态sql
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
            wrapper.ge("gmt_create",begin)
        }
        if(!StringUtils.isEmpty(end)){
            wrapper.le("gmt_create",end);
        }

        //调用方法实现条件查询份额与
        eduTeacherService.page(page,wrapper);
        long total = page.getTotal();        //总记录数
        List<EduTeacher> records = page.getRecords();    //数据list结合
        return R.ok().data("total",total).data("rows",records);
    }
```  
@RequestBody    需要使用post提交方式
    使用json传递数据，吧json数据封装到对应对象里面  
@ResponseBody
    返回json数据
```
 @PostMapping("pageTeacherCondition/{current}/{size}")
 public R pageTeacherCondition(@PathVariable long current,
                               @PathVariable long size,
                               @RequestBody(required = false) TeacherQueryVO teacherQueryVO){
```  
###7添加讲师  
__自动填充__
```
不需要set到对象里面值，使用mp方式实现数据添加
具体实现
    第一步  在实体类里面进行自动填充属性上添加注解
        @TableField(fill = FieldFill.INSERT)
        private Date createTime;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Date updateTime;
    第二步 创建类，实现元对象处理器接口：com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
          ，实现接口里面的方法
            insertFill
            updateFill
        this.setFieldValByName("createTime",new Date(),metaObject);
        设置属性值通过名称，第一个参数，属性名称，第二个是 值，第三个元数据
```
````
@Component //@Component 交给spring管理
public class MyMetaObjectHandler implements MetaObjectHandler{

    //使用mp实现添加操作，这个方法执行
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

    //使用mp实现修改操作，这个方法执行
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime",new Date(),metaObject);

    }
}
````    
编辑controller
```
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
```   
###八、讲师修改功能  
1.根据讲师id进行查询  
```
//根据讲师id查询
    @GetMapping("getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        EduTeacher eduTeacher = eduTeacherService.getById(id);
        return R.ok().data("teacher",eduTeacher);
    }
```  
2.讲师修改  
```
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
```  
###九、统一异常处理  
>(1)全局异常处理  
>(2)特定异常处理  
>(3)自定义异常处理  
>异常处理级别 自定义异常>特定异常>全局异常，优先查找自定义异常，以此类推  
1.创建统一异常处理器
在service-base总创建统一异常处理类GlobalExceptionHandler。java  
(1)全局异常处理  
```
@ControllerAdvice
public class GlobalExceptionHandler {

    //指定出现什么异常执行这个方法
    @ExceptionHandler(Exception.class)
    //因为不在controller中，切面编程，加上注释，可以返回数据
    @ResponseBody
    public R error(Exception e){
        e.printStackTrace();
        return R.error().message("执行了全局异常处理!!!");
    }
}
```   
注意，需要在service-base中引入common_utils依赖
```
    <dependencies>
        <dependency>
            <groupId>com.atguigu</groupId>
            <artifactId>common_utils</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```  
之前在service模块中引入service-base和common_utils两个依赖，现   
在由于service-base中引入common_utils依赖，依赖传递，所以需要去  
掉common_utils依赖,避免出现依赖重复  
```
@ControllerAdvice ，很多初学者可能都没有听说过这个注解，实际上，这是一个非常有用的注解，顾名思义，这是一个增强的 Controller。使用这个 Controller ，可以实现三个方面的功能：

全局异常处理
全局数据绑定
全局数据预处理
灵活使用这三个功能，可以帮助我们简化很多工作，需要注意的是，这是 SpringMVC 提供的功能，在 Spring Boot 中可以直接使用，
```  
(2)特殊异常处理  
```
    //特定异常处理  ArithmeticException
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public R error(ArithmeticException e){
        e.printStackTrace();
        return R.error().message("执行了ArithmeticException异常处理!!!");
    }
```  
(3)自定义异常处理  
* 第一步 创建自定义异常类，继承RuntimeException,  
写异常属性 
```
@Data               //生成get set方法
@AllArgsConstructor //生成有参数的构造方法
@NoArgsConstructor  //生成无参数的构造方法
public class GuliException extends RuntimeException{
    private Integer code;//状态码
    private String msg;//异常信息
}
```  
* 第二步 在异常处理中，加入自定义异常  
```
//自定义异常处理  GuliException
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMsg());
    }
```  
* 第三步 需要手动捕获异常，抛出自定义异常
```
        try {
            int i = 5/0;
        } catch (Exception e) {
            //执行自定义异常
            throw new GuliException(20001,"执行了自定义异常处理……");
        }
```  
###十、统一日志处理  
1. 配置日志级别  
日志记录器（logger）的行为是分等级的，如下所示：  
分为:OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL  
默认情况下，sprign boot从空盒子太打印出来的日志界别只有INFO及以上级别，可以配置日志级别  
2.设置日志级别  
```
#设置日志级别 默认是INFO 级别，设置级别包含前面的级别
logging.level.root=warn
```  
3.把日志不仅输出到控制台，也可以输出到文件中，使用日志工具  
* log4j
* logback  
第一步 删除application。properties中的日志配置
```
#设置日志级别
#logging.level.root=info

#mybatis日志
#mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

```  
第二步 安装dea彩色日志插件：grep-console  
resource中创建 logback-spring.xml,名称是固定的  
```
<?xml version="1.0" encoding="UTF-8"?>
<!-- 日志级别从低到高分为TRACE < DEBUG < INFO < WARN < ERROR < FATAL，如果设置为WARN，则低于WARN的信息都不会输出 -->
<!-- scan:当此属性设置为true时，配置文档如果发生改变，将会被重新加载，默认值为true -->
<!-- scanPeriod:设置监测配置文档是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。
                 当scan为true时，此属性生效。默认的时间间隔为1分钟。 -->
<!-- debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。 -->
<configuration  scan="true" scanPeriod="10 seconds">
    <contextName>logback</contextName>

    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="D:/guli_logs/edu" />

    <!--0. 日志格式和颜色渲染 -->
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
    <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN" value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    <!--1. 输出到控制台-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>debug</level>
        </filter>
        <encoder>
            <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!--2. 输出到文档-->
    <!-- 2.1 level为 DEBUG 日志，时间滚动输出  -->
    <appender name="DEBUG_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/web_debug.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/web-debug-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录debug级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>debug</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.2 level为 INFO 日志，时间滚动输出  -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/web_info.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天日志归档路径以及格式 -->
            <fileNamePattern>${log.path}/web-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录info级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>info</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.3 level为 WARN 日志，时间滚动输出  -->
    <appender name="WARN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/web_warn.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/web-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录warn级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- 2.4 level为 ERROR 日志，时间滚动输出  -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文档的路径及文档名 -->
        <file>${log.path}/web_error.log</file>
        <!--日志文档输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/web-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文档保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文档只记录ERROR级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!--
        <logger>用来设置某一个包或者具体的某一个类的日志打印级别、
        以及指定<appender>。<logger>仅有一个name属性，
        一个可选的level和一个可选的addtivity属性。
        name:用来指定受此logger约束的某一个包或者具体的某一个类。
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              还有一个特俗值INHERITED或者同义词NULL，代表强制执行上级的级别。
              如果未设置此属性，那么当前logger将会继承上级的级别。
        addtivity:是否向上级logger传递打印信息。默认是true。
        <logger name="org.springframework.web" level="info"/>
        <logger name="org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor" level="INFO"/>
    -->

    <!--
        使用mybatis的时候，sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
        第一种把<root level="info">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
        第二种就是单独给dao下目录配置debug模式，代码如下，这样配置sql语句会打印，其他还是正常info级别：
        【logging.level.org.mybatis=debug logging.level.dao=debug】
     -->

    <!--
        root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
        不能设置为INHERITED或者同义词NULL。默认是DEBUG
        可以包含零个或多个元素，标识这个appender将会添加到这个logger。
    -->

    <!-- 4. 最终的策略 -->
    <!-- 4.1 开发环境:打印控制台-->
    <springProfile name="dev">
        <logger name="com.sdcm.pmp" level="debug"/>
    </springProfile>

    <root level="info">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="DEBUG_FILE" />
        <appender-ref ref="INFO_FILE" />
        <appender-ref ref="WARN_FILE" />
        <appender-ref ref="ERROR_FILE" />
    </root>

    <!-- 4.2 生产环境:输出到文档
    <springProfile name="pro">
        <root level="info">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
        </root>
    </springProfile> -->

</configuration>
```  
3.2. 把日志输入web_error.log文件中
```
 @Slf4j
 log.error(e.getMessage());

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//自定义异常处理  GuliException
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
        log.error(e.getMessage());
        e.printStackTrace();
        return R.error().code(e.getCode()).message(e.getMsg());
    }
}
```  
3.3. 异常信息更详细  
（1）自定义异常工具类
```
package com.guigu.commonutils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @program:
 * @description:
 * @author: WangYuChao
 * @create: 2020/7/26 20:12
 */
public class ExceptionUtil {
    public static String getMessage(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            //将异常信息输出在控制台
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}

```  
调用
```
log.error(ExceptionUtil.getMessage(e));
```

 

