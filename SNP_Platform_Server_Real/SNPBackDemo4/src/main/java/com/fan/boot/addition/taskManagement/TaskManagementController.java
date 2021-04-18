package com.fan.boot.addition.taskManagement;


import com.fan.boot.param.*;
import com.fan.boot.service.HiSeekerImpl;
import com.fan.boot.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
public class TaskManagementController {

    // 得到任务管理参数单例对象
    @Autowired
    TaskManagementParam tmp;

    // HiSeeker单例对象
    @Autowired
    HiSeekerParam hsp;

    // ClusterMI单例对象
    @Autowired
    ClusterMIParam cmip;

    // DCHE单例对象
    @Autowired
    DCHEParam dchep;

    // DECMDR单例对象
    @Autowired
    DECMDRParam decmdrp;

    // MACOED单例对象
    @Autowired
    MACOEDParam macoedp;

    // DualWMDR单例对象
    @Autowired
    DualWMDRParam dualwmdrp;

    // MOMDR单例对象
    @Autowired
    MOMDRParam momdrp;

    // EpiMC单例对象
    @Autowired
    EpiMCParam epimcp;

    // 前端删除任务的请求，只能在没有任务正在进行时进行删除
    @GetMapping("/DeleteTask")
    public String deleteTask(@RequestParam Map<String, String> params) {
        tmp.deleteTask(Integer.parseInt(params.get("deleteIndex")));

        return "删除任务请求已执行";
    }

    // 刷新任务控制界面
    @GetMapping("/RefreshTask")
    public Map<String, String>[] refreshTask() {
        return tmp.getTask();
    }

    // HiSeeker算法批处理所需算法参数
    @PostMapping("/HiSeekerBatchRequest")
    public Map<String, Object> hiSeekerGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        HiSeekerParam hsp2 = new HiSeekerParam();
        hsp2.setAllParams(params);
        tmp.addObject(hsp2);

        tmp.addTask(hsp.getQueryId(), CommonUtils.getTime(), "HiSeeker", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", hsp.getQueryId());

        return map;
    }

    // ClusterMI算法批处理所需算法参数
    @PostMapping("/ClusterMIBatchRequest")
    public Map<String, Object> clusterMIGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        ClusterMIParam cmip2 = new ClusterMIParam();
        cmip2.setAllParams(params);
        tmp.addObject(cmip2);

        tmp.addTask(cmip.getQueryId(), CommonUtils.getTime(), "ClusterMI", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", cmip.getQueryId());

        return map;
    }

    // DCHE算法批处理所需算法参数
    @PostMapping("/DCHEBatchRequest")
    public Map<String, Object> dcheGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        DCHEParam dchep2 = new DCHEParam();
        dchep2.setRemainParams(params);
        tmp.addObject(dchep2);

        tmp.addTask(dchep.getQueryId(), CommonUtils.getTime(), "DCHE", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", dchep.getQueryId());

        return map;
    }

    // DECMDR算法批处理所需算法参数
    @PostMapping("/DECMDRBatchRequest")
    public Map<String, Object> decmdrGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        DECMDRParam decmdrp2 = new DECMDRParam();
        decmdrp2.setBasicParams(params);
        tmp.addObject(decmdrp2);

        tmp.addTask(decmdrp.getQueryId(), CommonUtils.getTime(), "DECMDR", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", decmdrp.getQueryId());

        return map;
    }

    // MACOED算法批处理所需算法参数
    @PostMapping("/MACOEDBatchRequest")
    public Map<String, Object> macoedGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        MACOEDParam macoedp2 = new MACOEDParam();
        macoedp2.setBasicParams(params);
        tmp.addObject(macoedp2);

        tmp.addTask(macoedp.getQueryId(), CommonUtils.getTime(), "MACOED", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", macoedp.getQueryId());

        return map;
    }

    // DualWMDR算法批处理所需算法参数
    @PostMapping("/DualWMDRBatchRequest")
    public Map<String, Object> dualWMDRGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        DualWMDRParam dualwmdrp2 = new DualWMDRParam();
        dualwmdrp2.setBasicParams(params);
        tmp.addObject(dualwmdrp2);

        tmp.addTask(dualwmdrp.getQueryId(), CommonUtils.getTime(), "DualWMDR", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", dualwmdrp.getQueryId());

        return map;
    }

    // MOMDR算法批处理所需算法参数
    @PostMapping("/MOMDRBatchRequest")
    public Map<String, Object> momdrGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        MOMDRParam momdrp2 = new MOMDRParam();
        momdrp2.setBasicParams(params);
        tmp.addObject(momdrp2);

        tmp.addTask(momdrp.getQueryId(), CommonUtils.getTime(), "MOMDR", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", momdrp.getQueryId());

        return map;
    }

    // EpiMC算法批处理所需算法参数
    @PostMapping("/EpiMCBatchRequest")
    public Map<String, Object> epiMCGetParams(@RequestParam Map<String, String> params) {
        // 将参数保存到算法参数对象中
        EpiMCParam epimcp2 = new EpiMCParam();
        epimcp2.setBasicParams(params);
        tmp.addObject(epimcp2);

        tmp.addTask(epimcp.getQueryId(), CommonUtils.getTime(), "EpiMC", "等待执行");

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", epimcp.getQueryId());

        return map;
    }

}
