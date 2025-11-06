package com.bank.workflow.domain.department.gateway;

import com.bank.workflow.domain.department.entity.Department;

import java.util.List;

/**
 * 部门网关接口
 *
 * @author Workflow Team
 * @since 1.0.0
 */
public interface DepartmentGateway {
    
    /**
     * 创建部门
     *
     * @param department 部门实体
     * @return 部门ID
     */
    Long createDepartment(Department department);
    
    /**
     * 更新部门
     *
     * @param department 部门实体
     */
    void updateDepartment(Department department);
    
    /**
     * 删除部门
     *
     * @param deptId 部门ID
     */
    void deleteDepartment(Long deptId);
    
    /**
     * 根据ID查询部门
     *
     * @param deptId 部门ID
     * @return 部门实体
     */
    Department getDepartmentById(Long deptId);
    
    /**
     * 根据编码查询部门
     *
     * @param deptCode 部门编码
     * @return 部门实体
     */
    Department getDepartmentByCode(String deptCode);
    
    /**
     * 查询所有部门（平铺列表）
     *
     * @return 部门列表
     */
    List<Department> listAllDepartments();
    
    /**
     * 查询指定父部门下的子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<Department> listChildrenDepartments(Long parentId);
    
    /**
     * 统计指定父部门下的子部门数量
     *
     * @param parentId 父部门ID
     * @return 子部门数量
     */
    Long countChildrenDepartments(Long parentId);
    
    /**
     * 查询部门树（带子部门的树形结构）
     *
     * @return 部门树
     */
    List<Department> getDepartmentTree();
    
    /**
     * 根据负责人ID查询部门
     *
     * @param managerId 负责人ID
     * @return 部门列表
     */
    List<Department> listDepartmentsByManagerId(Long managerId);
}

