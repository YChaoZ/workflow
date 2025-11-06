package com.bank.workflow.domain.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据字典类型实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_dict_type")
public class DictType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典编码（唯一）
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否系统字典（不可删除）
     */
    private Boolean isSystem;

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

    /**
     * 关联的字典数据（非数据库字段）
     */
    @TableField(exist = false)
    private List<DictData> dictDataList;
}

