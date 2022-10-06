package com.hwf.reggie.controller;

import com.hwf.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 主要进行文件的上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R upload(MultipartFile file) { //spring框架封装了multipartFile，使用该参数就可已自动装载前端上传的文件，且注意这里的参数名必须与前端保持一致
        log.info(file.toString());   //注意此时的file是一个临时文件，需要转存到指定位置，否则该次请求完成后就会被删除

        //获得原始文件名
        String originalFilename = file.getOriginalFilename();

        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));


        //防止basePath不存在，是否需要创建一个目录对象
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();  //mkdirs会创建所有父目录，mkdir则只会创建最后一级目录，当目录缺失多级时会失败
        }

        try {
            file.transferTo(new File(basePath + fileName)); //将临时文件转存到指定位置
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
