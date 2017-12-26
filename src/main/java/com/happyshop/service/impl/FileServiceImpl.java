package com.happyshop.service.impl;

import com.google.common.collect.Lists;
import com.happyshop.service.IFileService;
import com.happyshop.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/12/22.
 */
@Service("iFileService")
public class FileServiceImpl  implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String path) {
        //先保存到服务器upload文件夹
        String fileName = file.getOriginalFilename();//获取文件时间
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//获取扩展名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;//为了避免同样的文件名 覆盖
        File fileDir = new File(path);
        if(!fileDir.exists()){//如果文件夹不存在 就创建
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了到Tomcat/upload文件夹

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上

            targetFile.delete();//删除tomcat的文件
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }
}
