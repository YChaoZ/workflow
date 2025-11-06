package com.bank.workflow.domain.department.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门领域实体
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class Department {
    
    /**
     * 部门ID
     */
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
     * 部门路径（如：/ROOT/IT/DEV）
     */
    private String deptPath;
    
    /**
     * 部门负责人ID（对应数据库的manager_id）
     */
    private Long managerId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 部门描述
     */
    private String description;
    
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
     * 子部门列表（用于构建树形结构）
     */
    private List<Department> children = new ArrayList<>();
    
    /**
     * 判断是否为根部门
     */
    public boolean isRoot() {
        return parentId == null || parentId == 0;
    }
    
    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}

