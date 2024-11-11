package com.marsview.controller;

import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.ImgCloud;
import com.marsview.domain.Users;
import com.marsview.service.ImgcloudService;
import com.marsview.util.SessionUtils;
import com.marsview.util.storage.QiniuStorage;
import com.zhouzifei.tool.config.SimpleFsProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cloud")
public class ImgCloudController extends BasicController {
    @Autowired
    private ImgcloudService imgcloudService;

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

        String url = QiniuStorage.uploadFile(simpleFsProperties, file);
        ImgCloud imgCloud = new ImgCloud();
        imgCloud.setUserId(userId);
        imgCloud.setUserName(userName);
        imgCloud.setOriginName(file.getOriginalFilename());
        imgCloud.setFileName(file.getOriginalFilename());
        imgCloud.setType(file.getContentType());
        imgCloud.setSize(Long.valueOf(file.getSize()).intValue());
        imgCloud.setUrl(url);

        return getUpdateResponse(imgcloudService.save(imgCloud), "上传失败");
    }

//    @GetMapping("/list")
//    public ResultResponse list(HttpServletRequest request, int pageNum, int pageSize) {
//        Users users = SessionUtils.getUser(request);
//
//        QueryWrapper<ImgCloud> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", users.getId());
//
//        Page<ImgCloud> page = new Page<>(pageNum, pageSize);
//        IPage<ImgCloud> pageInfo = imgcloudService.page(page, queryWrapper);
//        return Builder.of(ResultResponse::new)
//                .with(ResultResponse::setData, Map.of(
//                        "list", pageInfo.getRecords(),
//                        "pageNum", pageInfo.getCurrent(),
//                        "pageSize", pageInfo.getSize(),
//                        "total", pageInfo.getTotal()
//                ))
//                .build();
//    }
//
//    @PostMapping("/delete")
//    public ResultResponse delete(@RequestBody ImgCloud imgCloud) {
//        return getUpdateResponse(imgcloudService.removeById(imgCloud.getId()), "删除失败");
//    }

}