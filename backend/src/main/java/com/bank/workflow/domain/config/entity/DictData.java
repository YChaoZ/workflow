package com.bank.workflow.domain.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典数据实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_dict_data")
public class DictData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型ID
     */
    private Long dictTypeId;

    /**
     * 字典类型编码（冗余字段）
     */
    private String dictCode;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 标签类型：primary/success/info/warning/danger
     */
    private String tagType;

    /**
     * CSS类名
     */
    private String cssClass;

    /**
     * 是否默认值
     */
    private Boolean isDefault;

    /**
     * 状态：0=禁用，1=启用
     */
    private Boolean status;

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
}

