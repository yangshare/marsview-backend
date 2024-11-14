package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.*;
import com.marsview.service.MenuService;
import com.marsview.service.PagesPublishService;
import com.marsview.service.PagesService;
import com.marsview.service.ProjectsService;
import com.marsview.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/9/29 11:01
 */
@RestController
@RequestMapping("api/admin")
@Tag(name = "管理员管理")
public class AdminController extends BasicController {

  private static final Logger LOGGER = LogManager.getLogger(AdminController.class);

  @Autowired
  private ProjectsService projectsService;

  @Autowired
  private MenuService menuService;

  @Autowired
  private PagesService pagesService;

  @Autowired
  private PagesPublishService pagesPublishService;

  /**
   * 获取项目配置
   *
   * @param response  HTTP 响应对象
   * @param project_id 项目 ID
   * @return 项目配置响应
   */
  @GetMapping("getProjectConfig")
  @Operation(summary = "获取项目配置")
  public ResultResponse getProjectConfig(
          HttpServletResponse response,
          @Parameter(description = "项目 ID") @RequestParam Long project_id) {
    return getResponse(projectsService.getById(project_id));
  }

  /**
   * 获取项目对应的菜单
   *
   * @param response  HTTP 响应对象
   * @param project_id 项目 ID
   * @return 菜单列表响应
   */
  @GetMapping("menu/list/{project_id}")
  @Operation(summary = "获取项目对应的菜单")
  public ResultResponse menuList(
          HttpServletResponse response,
          @Parameter(description = "项目 ID") @PathVariable Long project_id) {

    QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("project_id", project_id);
    return getResponse(Map.of("list", menuService.list(queryWrapper)));
  }

  /**
   * 获取页面详情
   *
   * @param request  HTTP 请求对象
   * @param response HTTP 响应对象
   * @param env      环境参数
   * @param page_id  页面 ID
   * @return 页面详情响应
   */
  @GetMapping("page/detail/{env}/{page_id}")
  @Operation(summary = "获取页面详情")
  public ResultResponse pageDetail(
          HttpServletRequest request,
          HttpServletResponse response,
          @Parameter(description = "环境参数") @PathVariable(name = "env") String env,
          @Parameter(description = "页面 ID") @PathVariable(name = "page_id") Long page_id) {
    LOGGER.info("请求参数env[{}],page_id[{}]", env, page_id);
    Users users = SessionUtils.getUser(request);

    Pages pages = pagesService.getById(page_id);
    if (pages == null) {
      return getErrorResponse("页面不存在");
    }
    Long last_publish_id = null;
    switch (env) {
      case "pre":
        last_publish_id = pages.getPrePublishId();
        break;
      case "stg":
        last_publish_id = pages.getStgPublishId();
        break;
      case "prd":
        last_publish_id = pages.getPrdPublishId();
        break;
      default:
        return getErrorResponse("环境参数错误");
    }

    QueryWrapper<PagesPublish> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("env", env);
    queryWrapper.eq("user_id", users.getId());
    queryWrapper.eq("id", last_publish_id);
    queryWrapper.eq("page_id", page_id);
    return getResponse(pagesPublishService.getOne(queryWrapper));
  }

  /**
   * 获取项目列表
   *
   * @param request  HTTP 请求对象
   * @param response HTTP 响应对象
   * @param pageNum  当前页码
   * @param pageSize 每页大小
   * @return 项目列表响应
   */
  @GetMapping("project/list")
  @Operation(summary = "获取项目列表")
  public ResultResponse projectList(
          HttpServletRequest request,
          HttpServletResponse response,
          @Parameter(description = "当前页码") @RequestParam int pageNum,
          @Parameter(description = "每页大小") @RequestParam int pageSize) {
    Users users = SessionUtils.getUser(request);
    QueryWrapper<Projects> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_id", users.getId());

    Page<Projects> page = new Page<>(pageNum, pageSize);
    IPage<Projects> pageInfo = projectsService.page(page, queryWrapper);
    return Builder.of(ResultResponse::new).with(ResultResponse::setData,
            Map.of("list", pageInfo.getRecords(),
                    "pageNum", pageInfo.getCurrent(),
                    "pageSize", pageInfo.getSize(), // 注意这里应该是 getSize() 而不是 getPages()
                    "total", pageInfo.getTotal())
    ).build();
  }
}
