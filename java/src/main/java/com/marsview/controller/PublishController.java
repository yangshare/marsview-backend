package com.marsview.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Pages;
import com.marsview.domain.PagesPublish;
import com.marsview.domain.Users;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.dto.PagesPublishDto;
import com.marsview.service.PagesPublishService;
import com.marsview.service.PagesService;
import com.marsview.util.HtmlUtil;
import com.marsview.util.SessionUtils;
import com.marsview.mapper.PagesMapper;
import com.marsview.mapper.PagesPublishMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/9/28 18:59
 */
@Tag(name = "发布管理")
@RestController
@RequestMapping("api/publish")
public class PublishController extends BasicController {

    @Autowired
    private PagesPublishService pagesPublishService;

    @Autowired
    private PagesService pagesService;

    @Operation(summary = "创建发布")
    @PostMapping("create")
    public ResultResponse create(HttpServletRequest request, @Parameter(description = "发布信息") @RequestBody PagesPublishDto publishDto) {
        Users users = SessionUtils.getUser(request);
        Pages pages = pagesService.getById(publishDto.getPageId());
        if (pages == null) {
            return getErrorResponse("页面不存在");
        } else {
            QueryWrapper<PagesPublish> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("page_id", publishDto.getPageId());
            long count = pagesPublishService.count(queryWrapper);
            PagesPublish publish = new PagesPublish();
            publish.setPageId(pages.getId());
            publish.setPageName(pages.getName());
            publish.setUserId(users.getId());
            publish.setUserName(users.getUserName());
            publish.setCreatedAt(new Date());
            publish.setUpdatedAt(new Date());
            publish.setVersion((count + 1) + "");
            publish.setPageData(pages.getPageData());
            boolean result = pagesPublishService.save(publish);
            if (result) {
                // 更新页面信息
                Pages pagesNew = new Pages();
                BeanUtils.copyProperties(pages, pagesNew);
                pagesNew.setStgPublishId(StringUtils.equals("stg", publishDto.getEnv()) ? publish.getId() : null);
                pagesNew.setStgState(StringUtils.equals("stg", publishDto.getEnv()) ? 3 : null);
                pagesNew.setPrePublishId(StringUtils.equals("pre", publishDto.getEnv()) ? publish.getId() : null);
                pagesNew.setPreState(StringUtils.equals("pre", publishDto.getEnv()) ? 3 : null);
                pagesNew.setPrdPublishId(StringUtils.equals("prd", publishDto.getEnv()) ? publish.getId() : null);
                pagesNew.setPrdState(StringUtils.equals("prd", publishDto.getEnv()) ? 3 : null);
                pagesNew.setPreviewImg(publishDto.getPreviewImg());
                pagesService.updateById(pagesNew);
            }
            return getUpdateResponse(result, "发布失败");
        }
    }

    @Operation(summary = "分页获取发布记录")
    @PostMapping("list")
    public ResultResponse list(@Parameter(description = "发布信息") @RequestBody PagesPublishDto publishDto) {

        String env = publishDto.getEnv(); // 环境
        Long page_id = publishDto.getPageId(); // 页面ID
        String userName = publishDto.getUserName(); // 发布人名称 TODO

        if (page_id == null || page_id == 0) {
            return getErrorResponse("页面ID不能为空");
        }
        if (StringUtils.isEmpty(env)) {
            return getErrorResponse("环境不能为空");
        }
        QueryWrapper<PagesPublish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("env", env);
        queryWrapper.eq("page_id", page_id);
        if (publishDto.getStart() != null && publishDto.getEnd() != null) {
            queryWrapper.between("created_at", publishDto.getStart(), publishDto.getEnd());
        }
        if (!StringUtils.isEmpty(userName)) {
            queryWrapper.like("user_name", userName);
        }

        Page<PagesPublish> page = new Page<>(publishDto.getPageNum(), publishDto.getPageSize());
        IPage<PagesPublish> pageInfo = pagesPublishService.page(page, queryWrapper);
        List<PagesPublish> records = pageInfo.getRecords();
        List<PagesPublishDto> pagesPublishDtos = new ArrayList<>(records.size());
        for (PagesPublish record : records) {
            pagesPublishDtos.add(new PagesPublishDto(record));
        }
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pagesPublishDtos,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }
}
