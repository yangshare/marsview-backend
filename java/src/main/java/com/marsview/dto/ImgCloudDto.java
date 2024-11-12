package com.marsview.dto;

import com.marsview.domain.ImgCloud;
import com.marsview.domain.ImgCloud;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
@NoArgsConstructor
public class ImgCloudDto extends ImgCloud {
    private Date created_at;
    private String user_name;
    private String user_id;
    private String origin_name;
    private String file_name;


    /**
     * 实体对象转换成视图对象
     *
     * @param imgCloud
     */
    public ImgCloudDto(ImgCloud imgCloud) {
        BeanUtils.copyProperties(imgCloud, this);
        this.created_at = imgCloud.getCreatedAt();
        this.user_name = imgCloud.getUserName();
        this.user_id = imgCloud.getUserId().toString();
        this.origin_name = imgCloud.getOriginName();
        this.file_name = imgCloud.getFileName();

    }

    /**
     * 视图对象转换成实体对象
     *
     * @return
     */
    public ImgCloud toBean() {
        ImgCloud imgCloud = new ImgCloud();
        BeanUtils.copyProperties(this, imgCloud);
        imgCloud.setCreatedAt(this.created_at);
        imgCloud.setUserName(this.user_name);
        imgCloud.setUserId(Long.valueOf(this.user_id));
        imgCloud.setOriginName(this.origin_name);
        imgCloud.setFileName(this.file_name);


        return imgCloud;
    }
}
