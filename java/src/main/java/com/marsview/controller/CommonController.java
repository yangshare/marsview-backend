package com.marsview.controller;

import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.ImgCloud;
import com.marsview.domain.Users;
import com.marsview.service.ImgcloudService;
import com.marsview.util.SessionUtils;
import com.marsview.util.storage.QiniuStorage;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.dto.VirtualFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 公共接口
 */
@RestController
@RequestMapping("/api")
public class CommonController extends BasicController {

    @Autowired
    private SimpleFsProperties simpleFsProperties;


    /**
     * 上传文件
     * 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
     **/
    @PostMapping("/upload/files")
    public ResultResponse uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Users users = SessionUtils.getUser(request);
        Long userId = users.getId();
        String userName = users.getUserName();

        VirtualFile virtualFile = QiniuStorage.uploadFileAndReturnFile(simpleFsProperties, file);


        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "name", virtualFile.getOriginalFileName(),
                        "size", file.getSize(),
                        "type", file.getContentType(),
                        "url", virtualFile.getFullFilePath()
                ))
                .build();

    }


}