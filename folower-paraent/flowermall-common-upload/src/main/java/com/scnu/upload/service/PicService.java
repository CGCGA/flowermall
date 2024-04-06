package com.scnu.upload.service;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.scnu.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scnu.vo.PicUploadResult;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class PicService {

    @Value("${pic.pathDirPrefix}")
    private String pathDirPrefix;

    @Value("${pic.urlPrefix}")
    private String urlPreparePrefix;

    public PicUploadResult picUpload(MultipartFile pic) {
        PicUploadResult result = new PicUploadResult();

        String oringnName = pic.getOriginalFilename();
        String extName = oringnName.substring(oringnName.lastIndexOf("."));
        boolean isok = extName.matches(".(jpg|png|gif|JPG|PNG|GIF)$");
        if(!isok) {
            result.setError(1);
            return  result;
        }

        try {
            BufferedImage bufImg = ImageIO.read(pic.getInputStream());
            bufImg.getWidth();
            bufImg.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            result.setError(1);
            return result;
        }
        //创建upload路径
        String dir = "/"+ UploadUtil.getUploadPath(oringnName, "upload")+"/";
        //创建静态目录
        String pathDir = pathDirPrefix+dir;
        File file = new File(pathDir);
        if(!file.exists()) {
            file.mkdirs();
        }

        String urlPrefix = urlPreparePrefix + dir;

        String fileName = UUID.randomUUID().toString()+extName;

        System.out.println(urlPrefix);
        System.out.println(fileName);

        try {
            pic.transferTo(new File(pathDir+fileName));
        } catch (Exception e){
            e.printStackTrace();
            result.setError(1);
            return result;
        }

        result.setUrl(urlPrefix + fileName);
        return result;

    }
}
