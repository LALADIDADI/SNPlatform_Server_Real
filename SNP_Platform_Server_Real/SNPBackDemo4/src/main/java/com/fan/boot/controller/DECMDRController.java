package com.fan.boot.controller;

import com.fan.boot.config.MyConst;
import com.fan.boot.param.DECMDRParam;
import com.fan.boot.service.DECMDRImpl;
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
public class DECMDRController {
    // 得到单例DECMDRParam对象
    @Autowired
    DECMDRParam decmdrp;

    // 文件批量上传而使用的全局变量
    int inputFileCount = 0; // 一次性上传文件的数量

    // DECMDR参数上传方法
    @PostMapping("/DECMDRParamsUpload")
    public Map<String, Object> getParams(@RequestParam Map<String, String> params) throws IOException {

        // 将参数保存到算法参数对象中
        decmdrp.setBasicParams(params);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                DECMDRImpl.batchRun(decmdrp);
            }
        });
        thread.start();

        System.out.println("我跳过了算法运行，直接执行");
        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", decmdrp.getQueryId());

        // 重置inputFileCount,finishedFileCount
        decmdrp.setFinishedCount(0);
        decmdrp.setFilesCount(inputFileCount);
        inputFileCount = 0;

        return map;
    }

    // DECMDR文件上传方法
    @PostMapping("/DECMDRInputDataUpload")
    public String getFile(@RequestPart("txtFile") MultipartFile dataFile) throws IOException {

        log.info("上传的信息：InputData={}", dataFile);
        System.out.println(dataFile.isEmpty());

        // 删除上一个请求的文件夹,如果是第一次，也没问题
        if(inputFileCount == 0){
            String deletePath = MyConst.TEM_DATA_PATH + decmdrp.getQueryId();
            FileDeleteUtils.delete(deletePath);
        }

        if(!dataFile.isEmpty()){
            // 保存到本地文件服务器
            String originalFilename = dataFile.getOriginalFilename();
            System.out.println("originalFilename: "+originalFilename);

            if(inputFileCount == 0){
                decmdrp.setQueryId(CommonUtils.createQueryId());
                decmdrp.setInputDataName(originalFilename);
                decmdrp.setInputDataPath(CommonUtils.getInputPath(decmdrp.getQueryId(), originalFilename));// 得到完整路径，批处理用不到
                // 创建文件夹
                CommonUtils.createDir(decmdrp.getQueryId());
                // 不就是再新建一个属性嘛，新建不带文件名属性
                decmdrp.setInputDataPath_i(CommonUtils.getInputPath_i(decmdrp.getQueryId())); // 批处理需要用到的路径
                decmdrp.setResDataPath_i(CommonUtils.getResultPath_i(decmdrp.getQueryId())); // 批处理需要用到的返回文件不完整路径
            }

            String transferToPath = decmdrp.getInputDataPath_i() + originalFilename;

            dataFile.transferTo(new File(transferToPath));

        }
        inputFileCount++;
        System.out.println("已经上传 " + inputFileCount + " 个文件啦！");
        return "我收到你的文件啦lalala！";
    }


    // 前端轮询相应方法
    @PostMapping("/DECMDRPollResultData")
    public Map<String, Object> DECMDRFinished(@RequestParam Map<String, String> params) throws IOException {

        log.info("是否得到请求号：queryId={}", params);

        // 判断对应文件夹是否为空
        String queryId = params.get("queryId");
        String finishedPath = MyConst.TEM_DATA_PATH + queryId + "/haveFinished";
        String goalPath = MyConst.TEM_DATA_PATH + queryId + "/resultData";

        // CommonUtils.haveDir(goalPath)判断算法是否完成
        String finished = "false";

        float percent;
        // todo:process 100以及提高并行性
        System.out.println("getFinishedCount: "+ decmdrp.getFinishedCount());
        System.out.println("getFilesCount: " + decmdrp.getFilesCount());
        percent = CommonUtils.decFormatPer(decmdrp.getFinishedCount(), decmdrp.getFilesCount());

        if(decmdrp.getFinishedCount() == decmdrp.getFilesCount()){
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
    @GetMapping(value = "/DECMDRResultDataDownload", consumes = MediaType.ALL_VALUE)
    void downloadFile(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        String resDataDownloadPath = "D:\\SNPPlatfromData\\";
        String resName = "\\resultData.zip";
        String downloadQueryId = decmdrp.getQueryId(); // 请求号,早没想到，这不就简洁了

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
    @PostMapping("/DECMDRResultShow")
    public Map<String,String>[] hiSeekerResultShow(@RequestParam Map<String, String> params) {
        // 找到相应位置
        String filePath = CalParamsUtils.resShowPath(decmdrp.getQueryId());
        // 文件输出流
        Map[] res = ReadFileUtils.decmdrReadTxtFile(filePath,15);
        // 返回
        return res;
    }

    /***
     * 任务控制界面附加的方法
     * 包括一个只执行程序的方法和一个只上传参数的方法
     * DECMDR使用
     */

    @GetMapping("/DECMDRJustRun")
    public Map<String, Object> decmdrJustRun() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                DECMDRImpl.batchRun(decmdrp);
            }
        });
        thread.start();

        Map<String, Object> map = new HashMap<>();
        map.put("method", "DECMDR");
        map.put("queryId", decmdrp.getQueryId());

        // 重置inputFileCount,finishedFileCount
        decmdrp.setFinishedCount(0);
        decmdrp.setFilesCount(inputFileCount);
        inputFileCount = 0;

        return map;
    }

    @PostMapping("/DECMDRJustSetParams")
    public Map<String, Object> decmdrJustSetParams(@RequestParam Map<String, String> params) {

        // 将参数保存到算法参数对象中
        decmdrp.setBasicParams(params);

        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", decmdrp.getQueryId());

        return map;
    }
}
