package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Menu;
import com.marsview.domain.Pages;
import com.marsview.domain.Users;
import com.marsview.dto.MenuDto;
import com.marsview.service.MenuService;
import com.marsview.service.PagesService;
import com.marsview.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Date;
import java.util.Map;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:51
 */
@RestController
@RequestMapping("api/menu")
@Slf4j
@Tag(name = "菜单管理")
public class MenuController extends BasicController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private PagesService pagesService;

    /**
     * 创建菜单
     *
     * @param request
     * @param menuDto
     */
    @PostMapping("create")
    @Operation(summary = "创建菜单")
    public ResultResponse create(
            HttpServletRequest request,
            @Parameter(description = "菜单信息") @RequestBody MenuDto menuDto) {
        Users user = SessionUtils.getUser(request);
        // 只有菜单和页面类型支持自动创建页面
        Long pageId = 0L;
        if (menuDto.getType() != 2 && menuDto.getIs_create() == 1) {
            Pages pages = new Pages();
            pages.setName(menuDto.getName());
            pages.setUserId(user.getId());
            pages.setUserName(user.getUserName());
            pages.setIsPublic(1);
            pages.setIsEdit(2);
            pages.setProjectId(menuDto.getProject_id());
            pagesService.save(pages);
            pageId = pages.getId();
        }

        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        menu.setUserId(user.getId());
        menu.setUserName(user.getUserName());
        menu.setCreatedAt(new Date());
        menu.setProjectId(menuDto.getProject_id());
        menu.setPageId(menuDto.getPage_id());
        menu.setSortNum(menuDto.getSort_num());
        menu.setPageId(pageId);

        return getUpdateResponse(menuService.save(menu), "新增失败");
    }

    /**
     * 更新菜单
     *
     * @param menu
     */
    @PostMapping("update")
    @Operation(summary = "更新菜单")
    public ResultResponse update(
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        menu.setUpdatedAt(new Date());
        return getUpdateResponse(menuService.updateById(menu), "保存失败");
    }

    /**
     * 删除菜单
     *
     * @param menu
     */
    @PostMapping("delete")
    @Operation(summary = "删除菜单")
    public ResultResponse delete(
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        return getUpdateResponse(menuService.removeById(menu), "保存失败");
    }

    /**
     * 获取菜单列表
     *
     * @param menu
     */
    @PostMapping("list")
    @Operation(summary = "获取菜单列表")
    public ResultResponse list(
            @Parameter(description = "菜单查询条件") @RequestBody MenuDto menu) {
        /**
         * 项目ID判断
         */
        if (menu.getProject_id() == null || menu.getProject_id() == 0) {
            return getErrorResponse("项目ID不能为空");
        }
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", menu.getProject_id());
        if (StringUtils.hasText(menu.getName())) {
            queryWrapper.like("name", menu.getName());
        }
        if (menu.getStatus() != null && menu.getStatus() != -1) {
            queryWrapper.eq("status", menu.getStatus());
        }
        return getResponse(Map.of("list", menuService.list(queryWrapper)));
    }

    /**
     * 复制
     *
     * @param request
     * @param menu
     */
    @PostMapping("copy")
    @Operation(summary = "复制菜单")
    public ResultResponse copy(
            HttpServletRequest request,
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        Users user = SessionUtils.getUser(request);
        menu = menuService.getById(menu.getId());
        if (menu == null) {
            return getErrorResponse("菜单不存在，无法执行复制操作");
        }
        menu.setId(null);
        menu.setName(menu.getName() + "-副本");
        menu.setCreatedAt(new Date());
        menu.setUserId(user.getId());
        menu.setUserName(user.getUserName());
        return getUpdateResponse(menuService.save(menu), "复制失败");
    }
}
