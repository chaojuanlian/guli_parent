package com.atguigu.demo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @program: CodeGenerator
 * @description: 代码生成器
 * @author: WangYuChao
 * @create: 2020/7/11 18:54
 */
public class CodeGenerator {

    @Test
    public void run(){
        // 1.创建代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 2.全局配置
        GlobalConfig gc = new GlobalConfig();
        /*String projectPath =System.getProperty("user.dir"); //获取代码的绝对路径  有可能出错，建议手写绝对路径
        gc.setOutputDir(projectPath + "/src/main/java");*/
        String projectPath =System.getProperty("user.dir"); //获取代码的绝对路径  有可能出错，建议手写绝对路径
        gc.setOutputDir("F:\\王宇超\\学习\\Git\\Code\\guili_parent\\service\\service_edu" + "/src/main/java");


        gc.setAuthor("testjava");
        gc.setOpen(false); //生成后是否打开资源挂利器
        gc.setFileOverride(false); //重新生成时文件是否覆盖


        gc.setServiceName("%sService"); //去掉Service接口的首字母i IUserService UserService

        gc.setIdType(IdType.ID_WORKER_STR); //主键策略
        gc.setDateType(DateType.ONLY_DATE); //定义生成的实体类中日期类型
        gc.setSwagger2(true);   //开启Swagger2模式

        mpg.setGlobalConfig(gc);

        // 3.数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/guli?serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4.包配置
        PackageConfig pc = new PackageConfig();

        // 包 com.atguigu.eduservice
        pc.setParent("com.atguigu");
        pc.setModuleName("eduservice");    //模块名

        // 包 com.atguigu.eduservice.controller
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 5.策略配置
        StrategyConfig strategy = new StrategyConfig();
        //需要生成的 表的名称  多张表 ， 分隔
        //  strategy.setInclude("edu_teacher","edu_son","edy_student");
        strategy.setInclude("edu_chapter","edu_course","edu_course_description","edu_video");

        strategy.setNaming(NamingStrategy.underline_to_camel);  //数据库表映射到实体的命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_");   //生成实体时去掉表前缀

        strategy.setColumnNaming(NamingStrategy.underline_to_camel);    //数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型  @Accessors(chain = true) setter链式操作

        strategy.setRestControllerStyle(true); // restful api 风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符

        mpg.setStrategy(strategy);


        // 6.执行
        mpg.execute();

    }
}
