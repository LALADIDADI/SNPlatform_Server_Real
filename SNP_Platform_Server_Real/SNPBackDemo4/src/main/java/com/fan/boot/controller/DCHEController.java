package com.fan.boot.controller;

import com.fan.boot.param.DCHEParam;
import com.fan.boot.service.DCHEImpl;
import com.fan.boot.utils.CalParamsUtils;
import com.fan.boot.utils.CommonUtils;
import com.fan.boot.utils.FileDeleteUtils;
import com.fan.boot.utils.ZipUtils;
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
public class DCHEController {
    // 得到单例DCHEParam对象
    @Autowired
    DCHEParam dchep;


    // DCHE参数上传方法
    @PostMapping("/DCHEParamsUpload")
    public Map<String, Object> getParams(@RequestParam Map<String, String> params) throws IOException {

        // 将参数保存到算法参数对象中
        dchep.setRemainParams(params);

        // 调出相应的Service，传递参数，运行算法
        // 因为需要执行的时间超过5秒，所以出现了uncaught错误
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("为运行算法开启一个新线程");
                DCHEImpl.runDCHE(dchep);
            }
        });
        thread.start();
        System.out.println("我跳过了算法运行，直接执行");
        //返回参数以便测试是否上传成功
        log.info("上传的参数：params={}", params);
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("queryId", dchep.getQueryId());
        return map;
    }

    // DCHE文件上传方法
    @PostMapping("/DCHEInputDataUpload")
    public String getFile(@RequestPart("txtFile") MultipartFile dataFile) throws IOException {

        log.info("上传的信息：InputData={}", dataFile);
        System.out.println(dataFile.isEmpty());

        // 删除上一个请求的文件夹,如果是第一次，也没问题
        String deletePath = "D:/SNPPlatfromData/" + dchep.getQueryId();
        FileDeleteUtils.delete(deletePath);

        if(!dataFile.isEmpty()){
            // 保存到本地文件服务器
            String originalFilename = dataFile.getOriginalFilename();
            System.out.println("originalFilename: "+originalFilename);

            // 这里开始，DCHEParam单例对象介入
            dchep.setQueryId(CommonUtils.createQueryId());
            dchep.setInputDataName(originalFilename);
            dchep.setInputDataPath(CommonUtils.createDirAndPath(dchep.getQueryId(), originalFilename));
            dchep.setResDataPath(CommonUtils.getResultDataPathAndName(dchep.getQueryId(), "resultData.txt"));
            String inputDataPath = dchep.getInputDataPath();

            // 将finished属性重新设置为false
            dchep.setFinished(false);

            // 很重要的一句话，但是不知道在干啥
            dataFile.transferTo(new File(inputDataPath));

            // DCHEParam单例对象，插入通过上传的文件计算出来的输入参数
            System.out.println("inputDataPath: " + inputDataPath);
            CalParamsUtils.calParams(inputDataPath);
            dchep.setNoSNPs(CalParamsUtils.getNumSnp());
            dchep.setNoControls(CalParamsUtils.getNumControl());
            dchep.setNoCases(CalParamsUtils.getNumCase());
            dchep.setNoSamples(CalParamsUtils.getNumCase() + CalParamsUtils.getNumControl());

        }
        return "我收到你的文件啦lalala！";
    }


    // 前端轮询相应方法
    // todo: 因为是java了，虽然需要仍然需要轮询，但是要重写方法，一个boolean值的事情
    @PostMapping("/DCHEPollResultData")
    public Map<String, Object> DCHEFinished(@RequestParam Map<String, String> params) throws IOException {

        log.info("是否得到请求号：queryId={}", params);

        String finished = "false";
        if(dchep.isFinished() == true){
            finished = "true";
        }
        //返回参数以便测试是否上传成功
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("finished", finished);
        return map;
    }


    // 单纯的文件下载方法，输入：文件地址 返回结果
    // 可能要传入的参数
    @GetMapping(value = "/DCHEResultDataDownload", consumes = MediaType.ALL_VALUE)
    void downloadFile(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        String resDataDownloadPath = "D:\\SNPPlatfromData\\";
        String resName = "\\resultData\\resultData.txt";
        String downloadQueryId = dchep.getQueryId(); // 请求号,早没想到，这不就简洁了

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
}
