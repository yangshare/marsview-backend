package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Menu;
import com.marsview.domain.Pages;
import com.marsview.domain.Users;
import com.marsview.dto.PagesDto;
import com.marsview.mapper.RolesMapper;
import com.marsview.service.MenuService;
import com.marsview.service.PagesService;
import com.marsview.util.SessionUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:28
 */
@RestController
@RequestMapping("api/page")
@Slf4j
@Tag(name = "页面管理")
public class PageController extends BasicController {

    @Autowired
    private PagesService pagesService;

    @Autowired
    private MenuService menuService;

    @Resource
    private RolesMapper rolesMapper;

    /**
     * 创建页面
     *
     * @param request
     * @param response
     * @param pages
     */
    @PostMapping("create")
    @Operation(summary = "创建页面")
    public ResultResponse create(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "页面信息") @RequestBody Pages pages) {
        Users users = SessionUtils.getUser(request);
        pages.setUserId(users.getId());
        pages.setUserName(users.getUserName());
        pages.setCreatedAt(new Date());
        return getUpdateResponse(pagesService.save(pages), "创建失败");
    }

    /**
     * 获取页面列表
     *
     * @param request
     * @param response
     * @param pageNum
     * @param pageSize
     * @param type
     */
    @GetMapping("list")
    @Operation(summary = "获取页面列表")
    public ResultResponse list(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "页码") int pageNum,
            @Parameter(description = "每页大小") int pageSize,
            @Parameter(description = "类型") Integer type) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (type != 0) {
            queryWrapper.eq("is_public", type);
        }

        Page<Pages> page = new Page<>(pageNum, pageSize);
        IPage<Pages> pageInfo = pagesService.page(page, queryWrapper);
        List<Pages> records = pageInfo.getRecords();
        List<PagesDto> pagesDtos = new ArrayList<>(records.size());
        for (Pages record : records) {
            pagesDtos.add(new PagesDto(record));
        }
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pagesDtos,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    /**
     * 获取页面角色列表
     *
     * @param request
     * @param response
     * @param menu
     */
    @PostMapping("/role/list")
    @Operation(summary = "获取页面角色列表")
    public ResultResponse list(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (menu != null) {
            queryWrapper.eq("page_id", menu.getPageId());
        }
        menu.setUserId(users.getId());
        return getResponse(Map.of("list", menuService.list(queryWrapper)));
    }

    /**
     * 获取页面信息
     *
     * @param request
     * @param response
     * @param page_id
     */
    @GetMapping("/detail/{page_id}")
    @Operation(summary = "获取页面信息")
    public ResultResponse detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "页面ID") @PathVariable("page_id") Long page_id) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (page_id == null) {
            return getErrorResponse("参数错误");
        }
        queryWrapper.eq("id", page_id);
        Pages pages = pagesService.getOne(queryWrapper);
        if (pages == null) {
            return getErrorResponse("页面不存在");
        }
        return getResponse(new PagesDto(pages));
    }

    /**
     * 更新页面信息
     *
     * @param response
     * @param pagesDto
     */
    @PostMapping("update")
    @Operation(summary = "更新页面信息")
    public ResultResponse update(
            HttpServletResponse response,
            @Parameter(description = "页面信息") @RequestBody Pages pages) {
        pages.setUpdatedAt(new Date());
        return getUpdateResponse(pagesService.updateById(pages), "保存失败");
    }

    /**
     * 页面回滚
     *
     * @param response
     * @param dto
     */
    @PostMapping("rollback")
    @Operation(summary = "页面回滚")
    public ResultResponse rollback(
            HttpServletResponse response,
            @Parameter(description = "回滚信息") @RequestBody PagesDto dto) {
        return getUpdateResponse(
                pagesService.updateById(Builder.of(Pages::new)
                        .with(Pages::setId, dto.getPageId())
                        .with(Pages::setStgPublishId, "stg".equals(dto.getEnv()) ? dto.getLastPublishId() : null)
                        .with(Pages::setStgState, "stg".equals(dto.getEnv()) ? 3 : null)
                        .with(Pages::setPrePublishId, "pre".equals(dto.getEnv()) ? dto.getLastPublishId() : null)
                        .with(Pages::setPreState, "pre".equals(dto.getEnv()) ? 3 : null)
                        .with(Pages::setPrdPublishId, "prd".equals(dto.getEnv()) ? dto.getLastPublishId() : null)
                        .with(Pages::setPrdState, "prd".equals(dto.getEnv()) ? 3 : null).build()) ? 1 : 0, "操作失败");
    }

    /**
     * 获取页面模板列表
     */
    @GetMapping("getPageTemplateList")
    @Operation(summary = "获取页面模板列表")
    public ResultResponse getPageTemplateList(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页大小") @RequestParam int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", 3);
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword);
        }

        Page<Pages> page = new Page<>(pageNum, pageSize);
        IPage<Pages> pageInfo = pagesService.page(page, queryWrapper);
        List<Pages> records = pageInfo.getRecords();
        List<PagesDto> pagesDtos = new ArrayList<>(records.size());
        for (Pages record : records) {
            pagesDtos.add(new PagesDto(record));
        }
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pagesDtos,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }
}
