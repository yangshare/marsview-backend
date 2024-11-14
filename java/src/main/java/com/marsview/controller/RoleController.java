package com.marsview.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Pages;
import com.marsview.domain.Roles;
import com.marsview.domain.Users;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.dto.RolesDto;
import com.marsview.service.RolesService;
import com.marsview.util.HtmlUtil;
import com.marsview.util.SessionUtils;
import com.marsview.mapper.RolesMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:52
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("api/role")
public class RoleController extends BasicController {

    @Autowired
    private RolesService rolesService;

    @Operation(summary = "创建角色")
    @PostMapping("create")
    public ResultResponse create(HttpServletRequest request, @Parameter(description = "角色信息") @RequestBody RolesDto rolesDto) {
        if (rolesDto.getProjectId()==null|| rolesDto.getProjectId() == 0) {
            return getErrorResponse("项目id不能为空");
        }
        Users users = SessionUtils.getUser(request);
        Roles roles = new Roles();
        BeanUtils.copyProperties(rolesDto, roles);
        roles.setUserId(users.getId());
        roles.setUserName(users.getUserName());
        roles.setCreatedAt(new Date());
        roles.setProjectId(rolesDto.getProjectId());
        return getUpdateResponse(rolesService.save(roles), "新增失败");
    }

    @Operation(summary = "获取角色列表")
    @GetMapping("list")
    public ResultResponse list(@Parameter(description = "当前页数") int pageNum, @Parameter(description = "每页大小") int pageSize, @Parameter(description = "项目id") Long project_id) {
        if (project_id == null || project_id == 0) {
            return getErrorResponse("项目id不能为空");
        }

        QueryWrapper<Roles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", project_id);

        Page<Roles> page = new Page<>(pageNum, pageSize);
        IPage<Roles> pageInfo = rolesService.page(page, queryWrapper);
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", pageInfo.getRecords(),
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("listAll")
    public ResultResponse listAll(HttpServletResponse response, @Parameter(description = "项目id") Long project_id) {
        if (project_id == null || project_id == 0) {
            return getErrorResponse("项目id不能为空");
        }

        QueryWrapper<Roles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", project_id);

        List<Roles> list = rolesService.list(queryWrapper);
        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", list
                ))
                .build();
    }

    @Operation(summary = "更新角色信息")
    @PostMapping("updateLimits")
    public ResultResponse updateLimits(HttpServletResponse response, @Parameter(description = "角色信息") @RequestBody Roles roles) {
        roles.setUpdatedAt(new Date());
        return getUpdateResponse(rolesService.updateById(roles), "设置失败");
    }

    @Operation(summary = "删除角色")
    @PostMapping("delete")
    public ResultResponse delete(@Parameter(description = "角色信息") @RequestBody RolesDto rolesDto) {
        return getUpdateResponse(rolesService.removeById(rolesDto.getId()), "删除失败");
    }
}
