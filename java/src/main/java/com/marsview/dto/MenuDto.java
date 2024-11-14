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
     * 是否创建页面：1创建、2不创建
     */
    private  Integer isCreate;

}
