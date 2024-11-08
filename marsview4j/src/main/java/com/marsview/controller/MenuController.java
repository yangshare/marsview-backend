package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Lib;
import com.marsview.domain.Menu;
import com.marsview.domain.Users;
import com.marsview.controller.basic.BasicController;
import com.marsview.service.MenuService;
import com.marsview.util.HtmlUtil;
import com.marsview.util.SessionUtils;
import com.marsview.mapper.MenuMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class MenuController extends BasicController {

    @Autowired
    private MenuService menuService;

    /**
     * 创建菜单
     *
     * @param request
     * @param menu
     */
    @PostMapping("create")
    public ResultResponse create(HttpServletRequest request, @RequestBody Menu menu) {
        Users user = SessionUtils.getUser(request);
        menu.setUserId(user.getId());
        menu.setUserName(user.getUserName());
        menu.setCreatedAt(new Date());
        return getUpdateResponse(menuService.save(menu), "新增失败");
    }

    /**
     * 更新菜单
     *
     * @param menu
     */
    @PostMapping("update")
    public ResultResponse update(@RequestBody Menu menu) {
        menu.setUpdatedAt(new Date());
        return getUpdateResponse(menuService.updateById(menu), "保存失败");
    }

    /**
     * 删除菜单
     *
     * @param menu
     */
    @PostMapping("delete")
    public ResultResponse delete(@RequestBody Menu menu) {
        return getUpdateResponse(menuService.removeById(menu), "保存失败");
    }

    /**
     * 获取菜单列表
     *
     * @param menu
     */
    @PostMapping("list")
    public ResultResponse list(@RequestBody Menu menu) {
        /**
         * 项目ID判断
         */
        if (menu.getProjectId() == null || menu.getProjectId() == 0) {
            return getErrorResponse("项目ID不能为空");
        }
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", menu.getProjectId());
        if (StringUtils.hasText(menu.getName())) {
            queryWrapper.like("name", menu.getName());
        }
        if (menu.getStatus() != -1) {
            queryWrapper.eq("status", menu.getStatus());
        }
        return getResponse(Map.of("list", menuService.list(queryWrapper)));
    }

    /**
     * 复制
     *
     * @param menu
     */
    @PostMapping("copy")
    public ResultResponse copy(HttpServletRequest request, @RequestBody Menu menu) {
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
