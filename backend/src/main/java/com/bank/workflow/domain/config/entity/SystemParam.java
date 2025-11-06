package com.bank.workflow.domain.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统参数实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_param")
public class SystemParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 参数键（唯一）
     */
    private String paramKey;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数类型
     */
    private ParamType paramType;

    /**
     * 参数分组
     */
    private String paramGroup;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 是否系统参数（不可删除）
     */
    private Boolean isSystem;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 参数类型枚举
     */
    public enum ParamType {
        STRING, NUMBER, BOOLEAN, JSON
    }

    /**
     * 获取类型化的参数值
     *
     * @return 类型化的值
     */
    public Object getTypedValue() {
        if (paramValue == null) {
            return null;
        }

        return switch (paramType) {
            case NUMBER -> {
                try {
                    yield paramValue.contains(".")
                            ? Double.parseDouble(paramValue)
                            : Long.parseLong(paramValue);
                } catch (NumberFormatException e) {
                    yield paramValue;
                }
            }
            case BOOLEAN -> Boolean.parseBoolean(paramValue);
            case JSON -> paramValue; // 返回JSON字符串，由调用方解析
            default -> paramValue;
        };
    }
}

