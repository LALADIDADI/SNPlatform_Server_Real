package com.fan.boot.controller;

import com.fan.boot.config.MyConfig;
import com.fan.boot.config.MyConst;
import com.fan.boot.param.ClusterMIParam;
import com.fan.boot.service.ClusterMIImpl;
import com.fan.boot.service.HiSeekerImpl;
import com.fan.boot.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class ClusterMIController {
    // 得到单例ClusterMIParam对象
    @Autowired
    ClusterMIParam cmip;

    // 全局变量
    int inputFileCount = 0; // 一次性上传文件的数量
    float temFileCount = 0; // 暂存的文件数量，只在计算百分比时被调用一次
    String finished = "false"; // 判断多个参数文件是否均已运算完成。


    // ClusterMI参数上传方法
    @PostMapping("/ClusterMIParamsUpload")
    public Map<String, Object> getParams(@RequestParam Map<String, String> params) {

        // 将参数保存到算法参数对象中
        cmip.setAllParams(params);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                try {
                    ClusterMIImpl.batchRun(cmip);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", cmip.getQueryId());

        // 重置inputFileCount
        temFileCount = inputFileCount;
        inputFileCount = 0;

        return map;
    }

    // ClusterMI文件上传方法
    @PostMapping("/ClusterMIInputDataUpload")
    public String getFile(@RequestPart("txtFile") MultipartFile dataFile) throws IOException {

        log.info("上传的信息：InputData={}", dataFile);
        System.out.println(dataFile.isEmpty());

        // 上传意味着finished为false
        finished = "false";

        // 删除上一个请求的文件夹,如果是第一次，也没问题
        if(inputFileCount == 0){
            String deletePath = MyConst.TEM_DATA_PATH + cmip.getQueryId();
            FileDeleteUtils.delete(deletePath);
        }

        if(!dataFile.isEmpty()){
            // 保存到本地文件服务器
            String originalFilename = dataFile.getOriginalFilename();
            System.out.println("originalFilename: "+originalFilename);

            if(inputFileCount == 0){
                cmip.setQueryId(CommonUtils.createQueryId());
                cmip.setInputDataName(originalFilename);
                cmip.setInputDataPath(CommonUtils.getInputPath(cmip.getQueryId(), originalFilename));// 得到完整路径
                // 创建文件夹
                CommonUtils.createDir(cmip.getQueryId());
                // 不就是再新建一个属性嘛，新建不带文件名属性
                cmip.setInputDataPath_i(CommonUtils.getInputPath_i(cmip.getQueryId()));
            }

            String transferToPath = cmip.getInputDataPath_i() + originalFilename;

            dataFile.transferTo(new File(transferToPath));

        }
        inputFileCount++;
        System.out.println("已经上传 " + inputFileCount + " 个文件啦！");
        return "我收到你的文件啦lalala！";
    }

    // ClusterMI结果文件下载方法,不急，先等等，还要先打包和请求号返回，以及轮询

    // 前端轮询相应方法
    @PostMapping("/ClusterMIPollResultData")
    public Map<String, Object> ClusterMIFinished(@RequestParam Map<String, String> params) throws IOException {

        log.info("是否得到请求号：queryId={}", params);

        // 判断对应文件夹是否为空
        String queryId = params.get("queryId");
        String finishedPath = MyConst.TEM_DATA_PATH + queryId + "/haveFinished";
        String goalPath = MyConst.TEM_DATA_PATH + queryId + "/resultData";

        // CommonUtils.haveDir(goalPath)判断算法是否完成
        float fileCount = 0;
        float percent = 0;
        if(CheckUtils.isDir(finishedPath)){
            fileCount = CheckUtils.getDirCount(finishedPath);
            percent = CommonUtils.decFormatPer(fileCount, temFileCount);
        }

        if(fileCount == temFileCount){
            finished = "true";
            ZipUtils.dirToZip(goalPath);
        }

        //返回参数以便测试是否上传成功
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("finished", finished);
        map.put("progress", percent);
        return map;
    }


    // 单纯的文件下载方法，输入：文件地址 返回结果
    // 可能要传入的参数
    @GetMapping(value = "/ClusterMIResultDataDownload", consumes = MediaType.ALL_VALUE)
    void downloadFile(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        String resDataDownloadPath = "D:\\SNPPlatfromData\\";
        String resName = "\\resultData.zip";
        String downloadQueryId = cmip.getQueryId(); // 请求号,早没想到，这不就简洁了

        // 获取文件
        File file = new File(resDataDownloadPath + downloadQueryId + resName);
        System.out.println("下载的结果文件路径：" + resDataDownloadPath + downloadQueryId + resName);
        //文件名
        String fileName = file.getName();

        // 清空缓冲区，状态码和响应头(headers)
        response.reset();
        // 设置ContentType，响应内容为二进制数据流，编码为utf-8，此处设定的编码是文件内容的编码
        response.setContentType("application/octet-stream;charset=utf-8");
        // 以（Content-Disposition: attachment; filename="filename.jpg"）格式设定默认文件名，设定utf编码，此处的编码是文件名的编码，使能正确显示中文文件名
        response.setHeader("Content-Disposition", "attachment;fileName="+ fileName +";filename*=utf-8''"+ URLEncoder.encode(fileName,"utf-8"));

        // 实现文件下载
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            // 获取字节流
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            System.out.println("Download successfully!");
        }
        catch (Exception e) {
            System.out.println("Download failed!");
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 结果展示方法
    @PostMapping("/ClusterMIResultShow")
    public Map<String, String>[] clusterMIResultShow(@RequestParam Map<String, String> params) {
        // 找到相应位置
        String filePath = CalParamsUtils.resShowPath(cmip.getQueryId());
        // 文件输出流
        Map[] maps = ReadFileUtils.clusterMIReadTxtFile(filePath, 10);
        // 返回
        return maps;
    }

    // 强制终止按钮
    /**
     * 该方法会删除相应数据文件，并重置参数界面。原理为在遍历函数中插入布尔变量，
     * 并强制返回轮询结果为true
     */

    @GetMapping("/ClusterMIForceStop")
    public String clusterMIForceStop(@RequestParam Map<String, String> params) throws FileNotFoundException {

        // 强制终止所有进程
        ClusterMIImpl.destroyOb();
        // 保存目前计算的结果，打包成压缩包
        String queryId = cmip.getQueryId();
        String goalPath = MyConst.TEM_DATA_PATH + queryId + "/resultData";
        ZipUtils.dirToZip(goalPath);
        // 返回轮询结果为true
        finished = "true";

        // 返回
        return "强制暂停成功";
    }

    /***
     * 任务控制界面附加的方法
     * 包括一个只执行程序的方法和一个只上传参数的方法
     * ClusterMI使用
     */

    @GetMapping("/ClusterMIJustRun")
    public Map<String, Object> clusterMIJustRun() {

        // 调出相应的Service，传递参数，运行算法
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                try {
                    ClusterMIImpl.batchRun(cmip);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        //返回参数以便测试是否上传成功
        Map<String, Object> map = new HashMap<>();
        map.put("method", "ClusterMI");
        map.put("queryId", cmip.getQueryId());

        // 重置inputFileCount
        temFileCount = inputFileCount;
        inputFileCount = 0;

        return map;
    }

    @PostMapping("/ClusterMIJustSetParams")
    public Map<String, Object> clusterMIJustSetParams(@RequestParam Map<String, String> params) {

        cmip.setAllParams(params);

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", cmip.getQueryId());

        return map;
    }

}
