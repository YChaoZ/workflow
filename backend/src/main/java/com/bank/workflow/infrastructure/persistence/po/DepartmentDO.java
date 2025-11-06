package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 部门持久化对象
 * 注意：字段名与数据库列名完全一致，使用MyBatis-Plus的驼峰转下划线
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_department")
public class DepartmentDO {
    
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 部门编码
     */
    private String deptCode;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 父部门ID（0表示根部门）
     */
    private Long parentId;
    
    /**
     * 部门层级
     */
    private Integer deptLevel;
    
    /**
     * 部门路径
     */
    private String deptPath;
    
    /**
     * 部门负责人ID
     */
    private Long managerId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}

