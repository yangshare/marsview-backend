package com.marsview.dto;

import com.marsview.domain.PagesPublish;
import lombok.Data;

import java.util.Date;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:23
 */
@Data
public class PagesPublishDto extends PagesPublish {

    private Date start;//创建时间始
    private Date end;//创建时间末
    private Integer pageNum;//页码
    private Integer pageSize;//每页数量
    private Long page_id;//页面id
    private String publish_user_id;//发布人名称 TODO

}
