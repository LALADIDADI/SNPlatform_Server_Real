package com.fan.boot.addition.dataGeneration;


import com.fan.boot.service.HiSeekerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
public class DataGenerationController {

    // 监听并调起GameTes
    @GetMapping("/OpenGameTes")
    public String getParams() throws IOException {
        OpenGameTes.runGameTes();
        return "成功调起GameTes";
    }

    // 提供用户手册下载
    @GetMapping(value = "/downloadUserManual", consumes = MediaType.ALL_VALUE)
    void downloadUserManual(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String userManualPath = AbsolutePath + "\\src\\main\\resources\\static\\GameTesExe\\Users_Guide.pdf";
        String userManualPath2 = AbsolutePath + "\\model\\GameTesExe\\Users_Guide.pdf"; // 打包jar包使用

        // 获取文件
        File file = new File(userManualPath);
        System.out.println("下载的结果文件路径：" + userManualPath);
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

    // 提供源文件下载
    @GetMapping(value = "/downloadGameTes", consumes = MediaType.ALL_VALUE)
    void downloadGameTes(final HttpServletResponse response)
            throws Exception {
        // 一些固定的参数
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String userManualPath = AbsolutePath + "\\src\\main\\resources\\static\\GameTesExe\\GAMETES_2.1.jar";
        String userManualPath2 = AbsolutePath + "\\model\\GameTesExe\\GAMETES_2.1.jar"; // 打包jar包使用

        // 获取文件
        File file = new File(userManualPath);
        System.out.println("下载的结果文件路径：" + userManualPath);
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
