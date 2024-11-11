package com.marsview.dto;

import com.marsview.domain.Projects;
import lombok.Data;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:23
 */
@Data
public class ProjectsDto extends Projects {

    /**
     * 是否可以编辑
     */
    private Boolean is_edit;

    /**
     * 1、我的 2、市场
     */
    private Integer type;

    /**
     * 是否开放 1-我的 2-市场
     */
    private Integer is_public;//: 1,
    /**
     * 菜单模式：inline-内嵌 vertical-垂直  horizontal-水平
     */
    private String menu_mode;//: inline,
    /**
     * 菜单主题色：dark 深色 light-浅色 支持16进制
     */
    private String menu_theme_color;//: dark,
    /**
     * 系统主题色
     */
    private String system_theme_color;//: #1677ff,

}
