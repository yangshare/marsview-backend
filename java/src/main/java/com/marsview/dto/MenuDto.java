package com.marsview.dto;

import com.marsview.domain.Menu;
import lombok.Data;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/9/29 10:28
 */
@Data
public class MenuDto extends Menu {
    /**
     * 项目ID(兼容前端传递参数)
     */
    private Long project_id;

    /**
     * 是否创建页面：1创建、2不创建
     */
    private  Integer is_create;

    /**
     * 页面ID(兼容前端传递参数)
     */
    private Long page_id;

    /**
     * 排序(兼容前端传递参数)
     */
    private Integer sort_num;
}
