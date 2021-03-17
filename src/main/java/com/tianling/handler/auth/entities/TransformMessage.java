package com.tianling.handler.auth.entities;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 18:32
 */
@Data
public class TransformMessage implements Serializable {
    private String name;
    private String value;
}
