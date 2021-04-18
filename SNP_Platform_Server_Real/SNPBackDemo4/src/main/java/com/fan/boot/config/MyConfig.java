package com.fan.boot.config;


import com.fan.boot.addition.taskManagement.TaskManagementParam;
import com.fan.boot.param.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1、Configuration: 告诉Spring这是一个配置类 === Spring的配置文件。
 * 2、Bean: 给容器中添加组件，以方法名作为组件id，返回值就是组件类型。返回的值，就是组件在容器中的实例，默认为单实例
 * 3、配置类MyConfig本身也是组件
 * 4、proxyBeanMethods默认为true，即带来Bean的方法默认为true。外部无论对类中的这个组件调用多少次，都是同一单实例对象
 *      Full(proxyBeanMethods = true): 代理对象，每次调用都在我们的MyConfig文件中找，只有一个单实例对象
 *      Lite(proxyBeanMethods = false): 非代理对象，每次调用都会产生新的对象
 *      Full模式和Lite模式即：组件依赖，SpringBoot2的一大特点
 * 5、@Import({User.class, DBHelper.class})
 *      给容器中自动创建出这两个类型的组件，默认组件的名字为全类名，应该也是创建的新对象
 *
 * 6、@ConditionalOnBean(name = "Tom"),只有有Tom组件时才进行其他配置，也可以放在Bean上面，这里放在了类上面
 *    ConditionalOnMissingBean,只有不存在某某组件时，才进行其他配置，与上面那个正好相反
 */

@Configuration(proxyBeanMethods = true)
public class MyConfig {

    // 任务控制界面参数
    @Bean
    public TaskManagementParam TaskManagementParam01() {
        return new TaskManagementParam();
    }

    // HiSeeker算法
    @Bean
    public HiSeekerParam HiSeekerParam01() {
        return new HiSeekerParam();
    }

    // ClusterMI算法
    @Bean
    public ClusterMIParam ClusterParam01() {
        return new ClusterMIParam();
    }

    // DCHE算法
    @Bean
    public DCHEParam DCHEParam01() {
        return new DCHEParam();
    }

    // DECMDR算法
    @Bean
    public DECMDRParam DECMDRParam01() {
        return new DECMDRParam();
    }

    // MOCOED算法
    @Bean
    public MACOEDParam MACOEDParam01() {
        return new MACOEDParam();
    }

    // DualWMDR算法
    @Bean
    public DualWMDRParam DualWMDRParam01() {
        return new DualWMDRParam();
    }

    // MOMDR算法
    @Bean
    public MOMDRParam MOMDRParam01() {
        return new MOMDRParam();
    }

    // EpiMC算法
    @Bean
    public EpiMCParam EpiMCParam01() {
        return new EpiMCParam();
    }

}
