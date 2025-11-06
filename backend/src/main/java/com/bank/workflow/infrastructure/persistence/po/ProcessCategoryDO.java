package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 流程分类持久化对象
 * 注意：字段名与V1表定义保持一致（category_name, category_code）
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("wf_process_category")
public class ProcessCategoryDO {
    
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 分类编码
     */
    private String categoryCode;
    
    /**
     * 父分类ID（0表示根节点）
     */
    private Long parentId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}

