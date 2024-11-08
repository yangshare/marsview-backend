package com.marsview.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Lib;
import com.marsview.domain.Users;
import com.marsview.dto.LibDto;
import com.marsview.service.LibService;
import com.marsview.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/10/1 13:55
 */
@RestController
@RequestMapping("api/lib")
@Slf4j
public class LibController extends BasicController {


    @Autowired
    private LibService libService;

    /**
     * 组件库列表
     *
     * @param request
     * @param pageNum
     * @param pageSize
     * @param type
     */
    @GetMapping("list")
    public ResultResponse list(HttpServletRequest request, int pageNum, int pageSize, String keyword, int type) {
        Users users = SessionUtils.getUser(request);
        QueryWrapper<Lib> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword);
        }
        if (type == 1) {//展示自己的组件库
            queryWrapper.eq("user_id", users.getId());
        } else {
            queryWrapper.ne("user_id", users.getId());
        }

        Page<Lib> page = new Page<>(pageNum, pageSize);
        IPage<Lib> pageInfo = libService.page(page, queryWrapper);

        // 将 Projects 转换为 ProjectsDto
        List<LibDto> records = pageInfo.getRecords().stream()
                .map(lib -> {
                    LibDto dto = new LibDto();
                    BeanUtils.copyProperties(lib, dto); // 使用 Apache Commons BeanUtils 进行属性复制
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
     * 组件安装列表 TODO
     */

    /**
     * 获取组件库详情
     *
     * @param lib_id
     */
    @GetMapping("detail/{lib_id}")
    public ResultResponse libDetail(@PathVariable("lib_id") Long lib_id) {
        return getResponse(libService.getById(lib_id));
    }


    /**
     * 创建组件库
     *
     * @param request
     * @param lib
     */
    @PostMapping("create")
    public ResultResponse create(HttpServletRequest request, @RequestBody Lib lib) {
        Users users = SessionUtils.getUser(request);
        lib.setCreatedAt(new Date());
        lib.setUserId(users.getId());
        lib.setUserName(users.getUserName());
        return getUpdateResponse(libService.save(lib), "创建失败");
    }

    /**
     * 删除组件库 TODO
     */


    /**
     * 更新组件库
     *
     * @param lib
     */
    @PostMapping("update")
    public ResultResponse update(@RequestBody Lib lib) {
        return getUpdateResponse(libService.updateById(lib), "处理失败");
    }

    /**
     * 发布组件库
     *
     * @param lib
     */
    @PostMapping("publish")
    public ResultResponse publish(@RequestBody Lib lib) {
        return getUpdateResponse(libService.updateById(lib), "发布失败");
    }
}
