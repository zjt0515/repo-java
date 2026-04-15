package com.zjt.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author genshinya
 * @time 2025-06-04 14:40:19
 * @description RPC响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private Object data;

    private Class<?> dataType;

    private String message;

    private Exception exception;
}
