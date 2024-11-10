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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:28
 */
@RestController
@RequestMapping("api/page")
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
    public ResultResponse create(HttpServletRequest request, HttpServletResponse response, @RequestBody Pages pages) {
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
    public ResultResponse list(HttpServletRequest request, HttpServletResponse response, int pageNum, int pageSize, Integer type) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (type != 0) {
            queryWrapper.eq("is_public", type);
        }

        Page<Pages> page = new Page<>(pageNum, pageSize);
        IPage<Pages> pageInfo = pagesService.page(page, queryWrapper);
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pageInfo.getRecords(),
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    /**
     * 获取页面角色列表
     *
     * @param response
     * @param menu
     */
    @PostMapping("/role/list")
    public ResultResponse list(HttpServletRequest request, HttpServletResponse response, @RequestBody Menu menu) {
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
     * @param response
     * @param page_id
     */
    @GetMapping("/detail/{page_id}")
    public ResultResponse detail(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("page_id") Long page_id) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (page_id == null) {
            return getErrorResponse("参数错误");
        }
        queryWrapper.eq("id", page_id);
        return getResponse(pagesService.getOne(queryWrapper));
    }

    /**
     * 更新页面信息
     *
     * @param response
     * @param pagesDto
     */
    @PostMapping("update")
    public ResultResponse update(HttpServletResponse response, @RequestBody PagesDto pagesDto) {
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagesDto, pages);
        pages.setUpdatedAt(new Date());
        pages.setIsEdit(pagesDto.getIs_edit());
        pages.setIsPublic(pagesDto.getIs_public());
        pages.setPageData(pagesDto.getPage_data());
        return getUpdateResponse(pagesService.updateById(pages), "保存失败");
    }

    /**
     * 页面回滚
     *
     * @param response
     * @param dto
     */
    @PostMapping("rollback")
    public ResultResponse rollback(HttpServletResponse response, @RequestBody PagesDto dto) {
        return getUpdateResponse(
                pagesService.updateById(Builder.of(Pages::new)
                        .with(Pages::setId, dto.getPage_id())
                        .with(Pages::setStgPublishId, "stg".equals(dto.getEnv()) ? dto.getLast_publish_id() : null)
                        .with(Pages::setStgState, "stg".equals(dto.getEnv()) ? 3 : null)
                        .with(Pages::setPrePublishId, "pre".equals(dto.getEnv()) ? dto.getLast_publish_id() : null)
                        .with(Pages::setPreState, "pre".equals(dto.getEnv()) ? 3 : null)
                        .with(Pages::setPrdPublishId, "prd".equals(dto.getEnv()) ? dto.getLast_publish_id() : null)
                        .with(Pages::setPrdState, "prd".equals(dto.getEnv()) ? 3 : null).build()) ? 1 : 0, "操作失败");
    }
}
