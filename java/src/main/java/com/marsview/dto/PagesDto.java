package com.marsview.dto;

import com.marsview.domain.Pages;
import lombok.Data;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 17:11
 */
@Data
public class PagesDto extends Pages {

    /**
     * 1、我的 2、市场
     */
    private Integer type;

    /**
     * 环境
     */
    private String env;

    /**
     * push_id
     */
    private Long last_publish_id;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 页面数据
     */
    private String page_data;

    /**
     * 是否可编辑
     */
    private Integer is_edit;

    /**
     * 是否公开
     */
    private Integer is_public;
    /**
     * 预览图片
     */
    private String preview_img;

}
