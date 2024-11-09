package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.ImgCloud;
import com.marsview.domain.Pages;
import com.marsview.domain.Users;
import com.marsview.service.ImgcloudService;
import com.marsview.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud")
public class ImgCloudController extends BasicController {

    @Autowired
    private ImgcloudService imgcloudService;

    //  @PostMapping("/upload/files")
//  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
//    try {
//      Map<String, Object> tokenData = util.decodeToken(request);
//      Long userId = (Long) tokenData.get("userId");
//      String userName = (String) tokenData.get("userName");
//
//      int total = imgCloudMapper.getTotalByUserId(userId);
//      String message = total > 0 && userId == 49 ? "Demo用户只能上传1个文件" :
//        total > 10 && userId != 50 ? "普通用户最多可以上传10个文件" : "";
//
//      if (message != null && !message.isEmpty()) {
//        file.delete();
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
//      }
//
//      BosClient client = new BosClient(config.getOssConfig());
//      File tempFile = new File(file.getOriginalFilename());
//      file.transferTo(tempFile);
//
//      FileInputStream fis = new FileInputStream(tempFile);
//      PutObjectResponse response = client.putObject(config.getOssBucket2(), file.getOriginalFilename(), fis, file.getContentType());
//
//      String url = config.getOssCdnDomain2() + "/" + file.getOriginalFilename();
//      imgCloudMapper.create(userId, userName, file.getOriginalFilename(), file.getOriginalFilename(), file.getContentType(), file.getSize(), url);
//
//      tempFile.delete();
//      return ResponseEntity.ok().build();
//    } catch (Exception e) {
//      file.delete();
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//    }
//  }
//
    @GetMapping("/list")
    public ResultResponse list(HttpServletRequest request, int pageNum, int pageSize) {
        Users users = SessionUtils.getUser(request);

        QueryWrapper<ImgCloud> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());

        Page<ImgCloud> page = new Page<>(pageNum, pageSize);
        IPage<ImgCloud> pageInfo = imgcloudService.page(page, queryWrapper);
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pageInfo.getRecords(),
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    @PostMapping("/delete")
    public ResultResponse delete(@RequestBody ImgCloud imgCloud) {
        return getUpdateResponse(imgcloudService.removeById(imgCloud.getId()), "删除失败");
    }
}
