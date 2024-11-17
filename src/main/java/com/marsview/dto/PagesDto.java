package com.marsview.dto;

import com.marsview.domain.Pages;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 17:11
 */
@Data
@NoArgsConstructor
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
    private Long lastPublishId;

    /**
     * 页面id
     */
    private Long pageId;


    /**
     * 实体对象转换成视图对象
     *
     * @param pages
     */
    public PagesDto(Pages pages) {
        BeanUtils.copyProperties(pages, this);

    }

    /**
     * 视图对象转换成实体对象
     *
     * @return
     */
    public Pages toBean() {
        Pages pages = new Pages();
        BeanUtils.copyProperties(this, pages);

        return pages;
    }

}
