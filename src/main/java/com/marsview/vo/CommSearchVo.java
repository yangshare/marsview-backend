package com.marsview.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommSearchVo {

    @Schema(description = "关键字")
    private String keyword;

}
