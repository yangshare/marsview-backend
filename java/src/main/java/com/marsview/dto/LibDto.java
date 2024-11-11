package com.marsview.dto;


import com.marsview.domain.Lib;
import lombok.Data;

/**
 * <p>说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * createTime: 2024/10/1 13:58
 */
@Data
public class LibDto extends Lib {

    /**
     * 1、我的，2、市场
     */
    private int type;

}
