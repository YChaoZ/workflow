package com.bank.workflow.app.department.command;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建部门命令
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class CreateDepartmentCmd {
    
    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;
    
    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;
    
    /**
     * 父部门ID（0表示根部门）
     */
    @NotNull(message = "父部门ID不能为空")
    private Long parentId;
    
    /**
     * 部门负责人ID
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
}

