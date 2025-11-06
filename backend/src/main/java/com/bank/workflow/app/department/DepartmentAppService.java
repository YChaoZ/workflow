package com.bank.workflow.app.department;

import com.bank.workflow.app.department.command.CreateDepartmentCmd;
import com.bank.workflow.app.department.command.UpdateDepartmentCmd;
import com.bank.workflow.domain.department.entity.Department;
import com.bank.workflow.domain.department.gateway.DepartmentGateway;
import com.bank.workflow.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门应用服务
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentAppService {
    
    private final DepartmentGateway departmentGateway;
    private final UserGateway userGateway;
    
    /**
     * 创建部门
     *
     * @param cmd 创建命令
     * @return 部门ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(CreateDepartmentCmd cmd) {
        log.info("接收创建部门请求: cmd={}", cmd);
        
        // 检查部门编码是否已存在
        Department existingDept = departmentGateway.getDepartmentByCode(cmd.getDeptCode());
        if (existingDept != null) {
            throw new IllegalArgumentException("部门编码已存在: " + cmd.getDeptCode());
        }
        
        // 构建部门实体
        Department department = new Department();
        BeanUtils.copyProperties(cmd, department);
        
        // 计算部门层级和路径
        if (cmd.getParentId() == null || cmd.getParentId() == 0) {
            // 根部门
            department.setDeptLevel(1);
            department.setDeptPath("/" + cmd.getDeptCode());
        } else {
            // 子部门
            Department parent = departmentGateway.getDepartmentById(cmd.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父部门不存在: " + cmd.getParentId());
            }
            department.setDeptLevel(parent.getDeptLevel() + 1);
            department.setDeptPath(parent.getDeptPath() + "/" + cmd.getDeptCode());
        }
        
        // 默认启用
        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        
        // 保存部门
        Long deptId = departmentGateway.createDepartment(department);
        
        log.info("部门创建成功: deptId={}", deptId);
        return deptId;
    }
    
    /**
     * 更新部门
     *
     * @param cmd 更新命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(UpdateDepartmentCmd cmd) {
        log.info("接收更新部门请求: cmd={}", cmd);
        
        // 检查部门是否存在
        Department existingDept = departmentGateway.getDepartmentById(cmd.getId());
        if (existingDept == null) {
            throw new IllegalArgumentException("部门不存在: " + cmd.getId());
        }
        
        // 检查部门编码是否被其他部门占用
        Department deptByCode = departmentGateway.getDepartmentByCode(cmd.getDeptCode());
        if (deptByCode != null && !deptByCode.getId().equals(cmd.getId())) {
            throw new IllegalArgumentException("部门编码已被占用: " + cmd.getDeptCode());
        }
        
        // 更新部门实体
        Department department = new Department();
        BeanUtils.copyProperties(cmd, department);
        
        // 如果修改了父部门或部门编码，需要重新计算层级和路径
        if (!existingDept.getParentId().equals(cmd.getParentId()) 
                || !existingDept.getDeptCode().equals(cmd.getDeptCode())) {
            if (cmd.getParentId() == null || cmd.getParentId() == 0) {
                department.setDeptLevel(1);
                department.setDeptPath("/" + cmd.getDeptCode());
            } else {
                // 不能将部门移动到其自身或其子部门下
                if (isDescendant(cmd.getId(), cmd.getParentId())) {
                    throw new IllegalArgumentException("不能将部门移动到其子部门下");
                }
                
                Department parent = departmentGateway.getDepartmentById(cmd.getParentId());
                if (parent == null) {
                    throw new IllegalArgumentException("父部门不存在: " + cmd.getParentId());
                }
                department.setDeptLevel(parent.getDeptLevel() + 1);
                department.setDeptPath(parent.getDeptPath() + "/" + cmd.getDeptCode());
            }
        } else {
            // 保持原有层级和路径
            department.setDeptLevel(existingDept.getDeptLevel());
            department.setDeptPath(existingDept.getDeptPath());
        }
        
        departmentGateway.updateDepartment(department);
        
        log.info("部门更新成功: deptId={}", cmd.getId());
    }
    
    /**
     * 删除部门
     *
     * @param deptId 部门ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long deptId) {
        log.info("接收删除部门请求: deptId={}", deptId);
        
        // 检查部门是否存在
        Department department = departmentGateway.getDepartmentById(deptId);
        if (department == null) {
            throw new IllegalArgumentException("部门不存在: " + deptId);
        }
        
        // 检查是否有子部门
        Long childrenCount = departmentGateway.countChildrenDepartments(deptId);
        if (childrenCount > 0) {
            throw new IllegalArgumentException("存在子部门，无法删除");
        }
        
        // 检查是否有用户关联
        long userCount = userGateway.countUsersByDeptId(deptId);
        if (userCount > 0) {
            throw new IllegalArgumentException("该部门有" + userCount + "个用户，无法删除。请先将用户移到其他部门。");
        }
        
        departmentGateway.deleteDepartment(deptId);
        
        log.info("部门删除成功: deptId={}", deptId);
    }
    
    /**
     * 查询部门详情
     *
     * @param deptId 部门ID
     * @return 部门实体
     */
    public Department getDepartmentById(Long deptId) {
        log.info("接收查询部门详情请求: deptId={}", deptId);
        return departmentGateway.getDepartmentById(deptId);
    }
    
    /**
     * 查询所有部门（平铺列表）
     *
     * @return 部门列表
     */
    public List<Department> listAllDepartments() {
        log.info("接收查询所有部门请求");
        return departmentGateway.listAllDepartments();
    }
    
    /**
     * 查询部门树
     *
     * @return 部门树
     */
    public List<Department> getDepartmentTree() {
        log.info("接收查询部门树请求");
        return departmentGateway.getDepartmentTree();
    }
    
    /**
     * 查询子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    public List<Department> listChildrenDepartments(Long parentId) {
        log.info("接收查询子部门请求: parentId={}", parentId);
        return departmentGateway.listChildrenDepartments(parentId);
    }
    
    /**
     * 判断targetId是否为ancestorId的后代
     */
    private boolean isDescendant(Long ancestorId, Long targetId) {
        if (ancestorId.equals(targetId)) {
            return true;
        }
        
        Department target = departmentGateway.getDepartmentById(targetId);
        if (target == null || target.getParentId() == null || target.getParentId() == 0) {
            return false;
        }
        
        return isDescendant(ancestorId, target.getParentId());
    }
}

