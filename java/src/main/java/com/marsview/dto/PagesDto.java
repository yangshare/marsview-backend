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

    private Long user_id;
    private String user_name;
    private Long stg_publish_id;
    private Long pre_publish_id;
    private Long prd_publish_id;
    private Integer stg_state;
    private Integer pre_state;
    private Integer prd_state;
    private Long project_id;
    private Date updated_at;

    /**
     * 实体对象转换成视图对象
     *
     * @param pages
     */
    public PagesDto(Pages pages) {
        BeanUtils.copyProperties(pages, this);
        this.page_data = pages.getPageData();
        this.preview_img = pages.getPreviewImg();
        this.is_edit = pages.getIsEdit();
        this.is_public = pages.getIsPublic();
        this.page_id = pages.getId();
        this.user_id = pages.getUserId();
        this.user_name = pages.getUserName();
        this.updated_at = pages.getUpdatedAt();

        this.stg_publish_id = pages.getStgPublishId();
        this.pre_publish_id = pages.getPrePublishId();
        this.prd_publish_id = pages.getPrdPublishId();
        this.stg_state = pages.getStgState();
        this.pre_state = pages.getPreState();
        this.prd_state = pages.getPrdState();
        this.project_id = pages.getProjectId();

    }

    /**
     * 视图对象转换成实体对象
     *
     * @return
     */
    public Pages toBean() {
        Pages pages = new Pages();
        BeanUtils.copyProperties(this, pages);
        pages.setId(this.page_id);
        pages.setPageData(this.page_data);
        pages.setPreviewImg(this.preview_img);
        pages.setIsEdit(this.is_edit);
        pages.setIsPublic(this.is_public);
        pages.setUserId(this.user_id);
        pages.setUserName(this.user_name);
        pages.setUpdatedAt(this.updated_at);
        pages.setStgPublishId(this.stg_publish_id);
        pages.setPrePublishId(this.pre_publish_id);
        pages.setPrdPublishId(this.prd_publish_id);
        pages.setStgState(this.stg_state);
        pages.setPreState(this.pre_state);
        pages.setPrdState(this.prd_state);
        pages.setProjectId(this.project_id);

        return pages;
    }

}
