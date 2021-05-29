package com.fan.boot.controller;

import com.fan.boot.config.MyConst;
import com.fan.boot.param.MOMDRParam;
import com.fan.boot.service.MOMDRImpl;
import com.fan.boot.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
public class MOMDRController {
    // 得到单例DCHEParam对象
    @Autowired
    MOMDRParam momdrp;

    // 文件批量上传而使用的全局变量
    int inputFileCount = 0; // 一次性上传文件的数量
    String finished = "false"; // 判断多个参数文件是否均已运算完成。

    // MOMDR参数上传方法
    @PostMapping("/MOMDRParamsUpload")
    public Map<String, Object> getParams(@RequestParam Map<String, String> params) throws IOException {

        // 将参数保存到算法参数对象中
        momdrp.setBasicParams(params);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                MOMDRImpl.batchRun(momdrp);
            }
        });
        thread.start();

        System.out.println("我跳过了算法运行，直接执行");
        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", momdrp.getQueryId());

        // 重置inputFileCount,finishedFileCount
        momdrp.setFinishedCount(0);
        momdrp.setFilesCount(inputFileCount);
        inputFileCount = 0;

        return map;
    }

    // MOMDR文件上传方法
    @PostMapping("/MOMDRInputDataUpload")
    public String getFile(@RequestPart("txtFile") MultipartFile dataFile) throws IOException {

        log.info("上传的信息：InputData={}", dataFile);
        System.out.println(dataFile.isEmpty());

        // 删除上一个请求的文件夹,如果是第一次，也没问题
        if(inputFileCount == 0){
            String deletePath = MyConst.TEM_DATA_PATH + momdrp.getQueryId();
            FileDeleteUtils.delete(deletePath);
        }

        if(!dataFile.isEmpty()){
            // 保存到本地文件服务器
            String originalFilename = dataFile.getOriginalFilename();
            System.out.println("originalFilename: "+originalFilename);

            if(inputFileCount == 0){
                momdrp.setQueryId(CommonUtils.createQueryId());
                momdrp.setInputDataName(originalFilename);
                momdrp.setInputDataPath(CommonUtils.getInputPath(momdrp.getQueryId(), originalFilename));// 得到完整路径，批处理用不到
                // 创建文件夹
                CommonUtils.createDir(momdrp.getQueryId());
                // 不就是再新建一个属性嘛，新建不带文件名属性
                momdrp.setInputDataPath_i(CommonUtils.getInputPath_i(momdrp.getQueryId())); // 批处理需要用到的路径
                momdrp.setResDataPath_i(CommonUtils.getResultPath_i(momdrp.getQueryId())); // 批处理需要用到的返回文件不完整路径
            }

            String transferToPath = momdrp.getInputDataPath_i() + originalFilename;

            dataFile.transferTo(new File(transferToPath));

        }
        inputFileCount++;
        System.out.println("已经上传 " + inputFileCount + " 个文件啦！");
        return "我收到你的文件啦lalala！";
    }


    // 前端轮询相应方法
    @PostMapping("/MOMDRPollResultData")
    public Map<String, Object> DCHEFinished(@RequestParam Map<String, String> params) throws IOException {

        log.info("是否得到请求号：queryId={}", params);

        // 判断对应文件夹是否为空
        String queryId = params.get("queryId");
        String finishedPath = MyConst.TEM_DATA_PATH + queryId + "/haveFinished";
        String goalPath = MyConst.TEM_DATA_PATH + queryId + "/resultData";

        // CommonUtils.haveDir(goalPath)判断算法是否完成

        float percent;
        // todo:process 100以及提高并行性
        System.out.println("getFinishedCount: "+ momdrp.getFinishedCount());
        System.out.println("getFilesCount: " + momdrp.getFilesCount());
        percent = CommonUtils.decFormatPer(momdrp.getFinishedCount(), momdrp.getFilesCount());

        if(momdrp.getFinishedCount() == momdrp.getFilesCount()){
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
    @GetMapping(value = "/MOMDRResultDataDownload", consumes = MediaType.ALL_VALUE)
    void downloadFile(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        String resDataDownloadPath = "D:\\SNPPlatfromData\\";
        String resName = "\\resultData.zip";
        String downloadQueryId = momdrp.getQueryId(); // 请求号,早没想到，这不就简洁了

        // 测试用
        String testdownloadQueryId = "20210318232201";

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
    @PostMapping("/MOMDRResultShow")
    public Map<String,String>[] MOMDRResultShow(@RequestParam Map<String, String> params) {
        // 找到相应位置
        String filePath = CalParamsUtils.resShowPath(momdrp.getQueryId());
        // 文件输出流
        Map[] res = ReadFileUtils.momdrReadTxtFile(filePath,255);
        // 返回
        return res;
    }

    // 强制终止按钮
    /**
     * 该方法会删除相应数据文件，并重置参数界面。原理为在遍历函数中插入布尔变量，
     * 并强制返回轮询结果为true
     */
    @GetMapping("/MOMDRForceStop")
    public String momdrForceStop(@RequestParam Map<String, String> params) throws FileNotFoundException {

        // 强制终止所有进程
        // 保存目前计算的结果，打包成压缩包
        String queryId = momdrp.getQueryId();
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
     * MOMDR使用
     */

    @GetMapping("/MOMDRJustRun")
    public Map<String, Object> MOMDRJustRun() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                MOMDRImpl.batchRun(momdrp);
            }
        });
        thread.start();

        Map<String, Object> map = new HashMap<>();
        map.put("method", "MOMDR");
        map.put("queryId", momdrp.getQueryId());

        // 重置inputFileCount,finishedFileCount
        momdrp.setFinishedCount(0);
        momdrp.setFilesCount(inputFileCount);
        inputFileCount = 0;

        return map;
    }

    @PostMapping("/MOMDRJustSetParams")
    public Map<String, Object> MOMDRJustSetParams(@RequestParam Map<String, String> params) {

        // 将参数保存到算法参数对象中
        momdrp.setBasicParams(params);

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", momdrp.getQueryId());

        return map;
    }
}
