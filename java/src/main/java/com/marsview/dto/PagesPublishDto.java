package com.marsview.dto;

import com.marsview.domain.PagesPublish;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 16:23
 */
@Data
@NoArgsConstructor
public class PagesPublishDto extends PagesPublish {

    private Date start;//创建时间始
    private Date end;//创建时间末
    private Integer pageNum;//页码
    private Integer pageSize;//每页数量
    private Long page_id;//页面id
    private String publish_user_id;//发布人名称 TODO
    private String preview_img;//预览图


    /**
     * 实体对象转换成视图对象
     *
     * @param pagesPublish
     */
    public PagesPublishDto(PagesPublish pagesPublish) {
        BeanUtils.copyProperties(pagesPublish, this);
        this.page_id = pagesPublish.getPageId();
        this.publish_user_id = pagesPublish.getUserName();

    }

    /**
     * 视图对象转换成实体对象
     *
     * @return
     */
    public PagesPublish toBean() {
        PagesPublish pagesPublish = new PagesPublish();
        BeanUtils.copyProperties(this, pagesPublish);
        pagesPublish.setPageId(this.page_id);
        pagesPublish.setUserName(this.publish_user_id);

        return pagesPublish;
    }

}
