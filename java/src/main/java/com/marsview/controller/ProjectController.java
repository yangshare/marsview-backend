package com.marsview.controller;

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
   * @param response
   * @param projects
   */
  @PostMapping("create")
  public ResultResponse create(HttpServletRequest request, HttpServletResponse response, @RequestBody Projects projects) {
    Users users = SessionUtils.getUser(request);
    projects.setCreatedAt(new Date());
    projects.setUserId(users.getId());
    projects.setUserName(users.getUserName());
    return getUpdateResponse(projectsService.save(projects), "项目创建失败");
  }

  /**
   * 获取页面列表
   *
   * @param response
   * @param page_id
   */
  @GetMapping("/detail/{page_id}")
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
  public ResultResponse update(HttpServletResponse response, @RequestBody Projects projects) {
    projects.setUpdatedAt(new Date());
    return getUpdateResponse(projectsService.updateById(projects), "保存失败");
  }
}
