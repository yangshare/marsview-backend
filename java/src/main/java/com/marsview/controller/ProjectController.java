package com.marsview.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Projects;
import com.marsview.domain.Users;
import com.marsview.dto.ProjectsDto;
import com.marsview.service.ProjectsService;
import com.marsview.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 15:11
 */
@RestController
@RequestMapping("api/project")
@Tag(name = "项目管理")
public class ProjectController extends BasicController {

    @Autowired
    private ProjectsService projectsService;


    /**
     * 分页获取项目列表
     *
     * @param response
     * @param type
     * @param pageNum
     * @param pageSize
     * @param keyword
     */
    @GetMapping("list")
    @Operation(summary = "分页获取项目列表")
    public ResultResponse list(HttpServletRequest request, HttpServletResponse response, int type, int pageNum, int pageSize, String keyword) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Projects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", users.getId());
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword);
        }
        if (type != 0) {
            queryWrapper.eq("is_public", type);
        }

        Page<Projects> page = new Page<>(pageNum, pageSize);
        IPage<Projects> pageInfo = projectsService.page(page, queryWrapper);

        // 将 Projects 转换为 ProjectsDto
        List<ProjectsDto> records = pageInfo.getRecords().stream()
                .map(projects -> {
                    ProjectsDto dto = new ProjectsDto();
                    BeanUtils.copyProperties(projects, dto); // 使用 Apache Commons BeanUtils 进行属性复制
                    dto.setIs_edit(projects.getUserId().longValue() == users.getId().longValue());
                    return dto;
                })
                .collect(Collectors.toList());

        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", records,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    /**
     * 创建项目
     *
     * @param request
     * @param projectsDto
     */
    @PostMapping("create")
    @Operation(summary = "创建项目")
    public ResultResponse create(HttpServletRequest request, @RequestBody JSONObject projectsDto) {
        Users users = SessionUtils.getUser(request);
        Projects projects = new Projects();
        projects.setCreatedAt(new Date());
        projects.setUserId(users.getId());
        projects.setUserName(users.getUserName());

        projects.setBreadcrumb(projectsDto.getBoolean("breadcrumb") ? 1 : 0);
        projects.setFooter(projectsDto.getBoolean("footer") ? 1 : 0);
        projects.setIsPublic(projectsDto.getInteger("is_public"));
        projects.setLayout(projectsDto.getInteger("layout"));
        projects.setLogo(projectsDto.getString("logo"));
        projects.setMenuMode(projectsDto.getString("menu_mode"));
        projects.setMenuThemeColor(projectsDto.getString("menu_theme_color"));
        projects.setName(projectsDto.getString("name"));
        projects.setRemark(projectsDto.getString("remark"));
        projects.setSystemThemeColor(projectsDto.getString("system_theme_color"));
        projects.setTag(projectsDto.getBoolean("tag") ? 1 : 0);
        return getUpdateResponse(projectsService.save(projects), "项目创建失败");
    }

    /**
     * 获取页面列表
     *
     * @param response
     * @param page_id
     */
    @GetMapping("/detail/{page_id}")
    @Operation(summary = "获取页面列表")
    public ResultResponse detail(HttpServletRequest request, HttpServletResponse response, @PathVariable("page_id") Long page_id) {
        return getResponse(projectsService.getById(page_id));
    }

    /**
     * 更新项目
     *
     * @param response
     * @param projects
     */
    @PostMapping("update")
    @Operation(summary = "更新项目")
    public ResultResponse update(HttpServletResponse response, @RequestBody Projects projects) {
        projects.setUpdatedAt(new Date());
        return getUpdateResponse(projectsService.updateById(projects), "保存失败");
    }

    /**
     * 删除项目
     *
     * @param projects
     */
    @PostMapping("delete")
    @Operation(summary = "删除项目")
    public ResultResponse delete(@RequestBody Projects projects) {
        return getUpdateResponse(projectsService.removeById(projects.getId()), "删除失败");
    }
}
