package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.ProjectUser;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.domain.Projects;
import com.marsview.service.ProjectUserService;
import com.marsview.service.ProjectsService;
import com.marsview.util.HtmlUtil;
import com.marsview.mapper.ProjectUserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/9/29 07:59
 */
@RestController
@RequestMapping("api/project/user")
public class ProjectUserController extends BasicController {

    @Autowired
    private ProjectUserService projectUserService;

    /**
     * 创建项目用户
     *
     * @param response
     * @param projectUser
     */
    @PostMapping("create")
    public ResultResponse create(HttpServletResponse response, @RequestBody ProjectUser projectUser) {
        QueryWrapper<ProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectUser.getProjectId());
        queryWrapper.eq("user_id", projectUser.getUserId());
        if (projectUserService.count(queryWrapper) > 0) {
            return getErrorResponse("该用户已存在");
        } else {
            projectUser.setCreatedAt(new Date());
            return getUpdateResponse(projectUserService.save(projectUser), "新增失败");
        }
    }

    /**
     * 获取用户列表
     *
     * @param response
     * @param project_id
     * @param pageNum
     * @param pageSize
     */
    @GetMapping("list")
    public ResultResponse detail(HttpServletResponse response, Long project_id, int pageNum, int pageSize) {

        QueryWrapper<ProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", project_id);

        Page<ProjectUser> page = new Page<>(pageNum, pageSize);
        IPage<ProjectUser> pageInfo = projectUserService.page(page, queryWrapper);
        return Builder.of(ResultResponse::new).with(ResultResponse::setData,
                Map.of("list", pageInfo.getRecords(),
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal())
        ).build();
    }
}
