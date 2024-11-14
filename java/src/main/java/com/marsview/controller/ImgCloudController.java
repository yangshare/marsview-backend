package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.ImgCloud;
import com.marsview.domain.Users;
import com.marsview.dto.ImgCloudDto;
import com.marsview.service.ImgcloudService;
import com.marsview.util.SessionUtils;
import com.marsview.util.storage.QiniuStorage;
import com.zhouzifei.tool.config.SimpleFsProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud")
@Tag(name = "图片云管理")
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
    @Operation(summary = "上传文件")
    public ResultResponse uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
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

    @GetMapping("/list")
    @Operation(summary = "获取图片列表")
    public ResultResponse list(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页大小") @RequestParam int pageSize) {
        Users users = SessionUtils.getUser(request);

        QueryWrapper<ImgCloud> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());

        Page<ImgCloud> page = new Page<>(pageNum, pageSize);
        IPage<ImgCloud> pageInfo = imgcloudService.page(page, queryWrapper);
        List<ImgCloud> imgClouds = pageInfo.getRecords();
        List<ImgCloudDto> imgCloudDtos = new ArrayList<>(imgClouds.size());
        for (ImgCloud imgCloud : imgClouds) {
            imgCloudDtos.add(new ImgCloudDto(imgCloud));
        }
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", imgCloudDtos,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除图片")
    public ResultResponse delete(
            @Parameter(description = "图片信息") @RequestBody ImgCloud imgCloud) {
        return getUpdateResponse(imgcloudService.removeById(imgCloud.getId()), "删除失败");
    }
}
