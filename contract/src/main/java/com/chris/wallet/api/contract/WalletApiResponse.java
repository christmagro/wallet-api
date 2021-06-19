package com.chris.wallet.api.contract;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WalletApiResponse<T extends Serializable> {

    @ApiModelProperty(position = -2, value = "Object used to return message codes.", required = true)
    private ErrorDto error;

    @ApiModelProperty(position = -1, value = "Data payload containing the result for the API operation.")
    private T data;

    @ApiModelProperty(position = 1, value = "Returns a list of bean validation errors.")
    private List<CustomFieldError> fieldErrors;


    private WalletApiResponse(final T data, final int code, final String key, final Map<String, Object> keyParams) {
        this.data = data;
        this.error = buildMessageDto(code, key, keyParams);
    }


    public static <T extends Serializable> WalletApiResponse<T> build(final T data) {
        return new WalletApiResponse<>(data, 0, "SUCCESS", null);
    }

    private ErrorDto buildMessageDto(int code, String key, Map<String, Object> keyParams) {
        Map<String, Object> params = new HashMap<>();
        if (keyParams != null) {
            params.putAll(keyParams);
        }
        return ErrorDto.builder()
                         .code(code)
                         .message(params.toString())
                         .build();
    }
}
