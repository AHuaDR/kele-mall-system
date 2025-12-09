package com.my.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/10/29 15:44
 * @description  采用R类封装后端返回的结果
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class R {
    private  int code;
    private String message;
    private Object data;

    public static R ok() {
        return R.builder().code(CodeEnum.OK.getCode()).message(CodeEnum.OK.getMsg()).data(null).build();
    }

    public static R ok(Object data) {
        return R.builder().code(CodeEnum.OK.getCode()).message(CodeEnum.OK.getMsg()).data(data).build();
    }

    public static R error() {
        return R.builder().code(CodeEnum.FALL.getCode()).message(CodeEnum.FALL.getMsg()).data(null).build();
    }

    public static R error(String message) {
        return R.builder().code(CodeEnum.FALL.getCode()).message(message).data(null).build();
    }

    public static R error(CodeEnum codeEnum) {
        return R.builder()
                .code(codeEnum.getCode())
                .message(codeEnum.getMsg())
                .build();
    }
}
