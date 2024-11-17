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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
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
@Tag(name = "项目用户管理")
@RestController
@RequestMapping("api/project/user")
public class ProjectUserController extends BasicController {

    @Autowired
    private ProjectUserService projectUserService;

    @Operation(summary = "创建项目用户")
    @PostMapping("create")
    public ResultResponse create(HttpServletResponse response, @Parameter(description = "项目用户信息") @RequestBody ProjectUser projectUser) {
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

    @Operation(summary = "获取用户列表")
    @GetMapping("list")
    public ResultResponse detail(HttpServletResponse response,
                                 @Parameter(description = "项目ID") Long projectId,
                                 @Parameter(description = "页码") int pageNum,
                                 @Parameter(description = "项目ID") String userName,
                                 @Parameter(description = "每页大小") int pageSize) {

        QueryWrapper<ProjectUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("user_name", userName);
        }

        Page<ProjectUser> page = new Page<>(pageNum, pageSize);
        IPage<ProjectUser> pageInfo = projectUserService.page(page, queryWrapper);
        return Builder.of(ResultResponse::new).with(ResultResponse::setData,
                Map.of("list", pageInfo.getRecords(),
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal())
        ).build();
    }

    @Operation(summary = "创建项目用户")
    @PostMapping("update")
    public ResultResponse update(HttpServletResponse response, @Parameter(description = "项目用户信息") @RequestBody ProjectUser projectUser) {
        return getUpdateResponse(projectUserService.updateById(projectUser), "新增失败");
    }

    @Operation(summary = "删除项目用户")
    @PostMapping("delete")
    public ResultResponse delete(HttpServletResponse response, @Parameter(description = "项目用户信息") @RequestBody ProjectUser projectUser) {
        return getUpdateResponse(projectUserService.removeById(projectUser.getId()), "删除失败");
    }

}
