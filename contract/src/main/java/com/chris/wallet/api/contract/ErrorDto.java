package com.chris.wallet.api.contract;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

    @ApiModelProperty(position = 1, value = "Integer for error codes.", required = true)
    private int code;


    @ApiModelProperty(position = 2, value = "String for error message", required = true)
    private String message;
}
