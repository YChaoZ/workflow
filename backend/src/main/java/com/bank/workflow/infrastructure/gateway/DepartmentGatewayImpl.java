package com.bank.workflow.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bank.workflow.domain.department.entity.Department;
import com.bank.workflow.domain.department.gateway.DepartmentGateway;
import com.bank.workflow.infrastructure.persistence.mapper.DepartmentMapper;
import com.bank.workflow.infrastructure.persistence.po.DepartmentDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门网关实现
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentGatewayImpl implements DepartmentGateway {
    
    private final DepartmentMapper departmentMapper;
    
    @Override
    public Long createDepartment(Department department) {
        log.info("创建部门: department={}", department);
        
        DepartmentDO departmentDO = new DepartmentDO();
        BeanUtils.copyProperties(department, departmentDO);
        departmentDO.setCreateTime(new Date());
        departmentDO.setUpdateTime(new Date());
        
        departmentMapper.insert(departmentDO);
        
        log.info("部门创建成功: deptId={}", departmentDO.getId());
        return departmentDO.getId();
    }
    
    @Override
    public void updateDepartment(Department department) {
        log.info("更新部门: department={}", department);
        
        DepartmentDO departmentDO = new DepartmentDO();
        BeanUtils.copyProperties(department, departmentDO);
        departmentDO.setUpdateTime(new Date());
        
        departmentMapper.updateById(departmentDO);
        
        log.info("部门更新成功: deptId={}", department.getId());
    }
    
    @Override
    public void deleteDepartment(Long deptId) {
        log.info("删除部门: deptId={}", deptId);
        
        departmentMapper.deleteById(deptId);
        
        log.info("部门删除成功: deptId={}", deptId);
    }
    
    @Override
    public Department getDepartmentById(Long deptId) {
        log.info("查询部门详情: deptId={}", deptId);
        
        DepartmentDO departmentDO = departmentMapper.selectById(deptId);
        if (departmentDO == null) {
            log.warn("部门不存在: deptId={}", deptId);
            return null;
        }
        
        return convertToEntity(departmentDO);
    }
    
    @Override
    public Department getDepartmentByCode(String deptCode) {
        log.info("根据编码查询部门: deptCode={}", deptCode);
        
        LambdaQueryWrapper<DepartmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentDO::getDeptCode, deptCode);
        
        DepartmentDO departmentDO = departmentMapper.selectOne(wrapper);
        if (departmentDO == null) {
            log.warn("部门不存在: deptCode={}", deptCode);
            return null;
        }
        
        return convertToEntity(departmentDO);
    }
    
    @Override
    public List<Department> listAllDepartments() {
        log.info("查询所有部门");
        
        LambdaQueryWrapper<DepartmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DepartmentDO::getSortOrder, DepartmentDO::getId);
        
        List<DepartmentDO> departmentDOList = departmentMapper.selectList(wrapper);
        
        List<Department> result = new ArrayList<>();
        for (DepartmentDO departmentDO : departmentDOList) {
            result.add(convertToEntity(departmentDO));
        }
        
        log.info("查询到{}个部门", result.size());
        return result;
    }
    
    @Override
    public List<Department> listChildrenDepartments(Long parentId) {
        log.info("查询子部门: parentId={}", parentId);
        
        LambdaQueryWrapper<DepartmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentDO::getParentId, parentId);
        wrapper.orderByAsc(DepartmentDO::getSortOrder, DepartmentDO::getId);
        
        List<DepartmentDO> departmentDOList = departmentMapper.selectList(wrapper);
        
        List<Department> result = new ArrayList<>();
        for (DepartmentDO departmentDO : departmentDOList) {
            result.add(convertToEntity(departmentDO));
        }
        
        log.info("查询到{}个子部门", result.size());
        return result;
    }
    
    @Override
    public Long countChildrenDepartments(Long parentId) {
        log.info("统计子部门数量: parentId={}", parentId);
        
        LambdaQueryWrapper<DepartmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentDO::getParentId, parentId);
        
        Long count = departmentMapper.selectCount(wrapper);
        
        log.info("子部门数量: {}", count);
        return count;
    }
    
    @Override
    public List<Department> getDepartmentTree() {
        log.info("查询部门树");
        
        // 查询所有部门
        List<Department> allDepartments = listAllDepartments();
        
        // 构建部门映射
        Map<Long, Department> deptMap = allDepartments.stream()
                .collect(Collectors.toMap(Department::getId, dept -> dept));
        
        // 构建树形结构
        List<Department> roots = new ArrayList<>();
        for (Department dept : allDepartments) {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                // 根部门
                roots.add(dept);
            } else {
                // 子部门，添加到父部门的children列表
                Department parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                } else {
                    // 父部门不存在，作为根部门处理
                    roots.add(dept);
                }
            }
        }
        
        log.info("部门树构建完成，根部门数量: {}", roots.size());
        return roots;
    }
    
    @Override
    public List<Department> listDepartmentsByManagerId(Long managerId) {
        log.info("根据负责人ID查询部门: managerId={}", managerId);
        
        LambdaQueryWrapper<DepartmentDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentDO::getManagerId, managerId);
        wrapper.orderByAsc(DepartmentDO::getSortOrder, DepartmentDO::getId);
        
        List<DepartmentDO> departmentDOList = departmentMapper.selectList(wrapper);
        
        List<Department> result = new ArrayList<>();
        for (DepartmentDO departmentDO : departmentDOList) {
            result.add(convertToEntity(departmentDO));
        }
        
        log.info("查询到{}个部门", result.size());
        return result;
    }
    
    /**
     * 转换DO为领域实体
     */
    private Department convertToEntity(DepartmentDO departmentDO) {
        Department department = new Department();
        BeanUtils.copyProperties(departmentDO, department);
        return department;
    }
}

