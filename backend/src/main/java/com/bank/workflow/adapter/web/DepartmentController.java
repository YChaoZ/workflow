package com.bank.workflow.adapter.web;

import com.bank.workflow.app.department.DepartmentAppService;
import com.bank.workflow.app.department.command.CreateDepartmentCmd;
import com.bank.workflow.app.department.command.UpdateDepartmentCmd;
import com.bank.workflow.domain.department.entity.Department;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentAppService departmentAppService;
    
    /**
     * 创建部门
     */
    @PostMapping
    public Map<String, Object> createDepartment(@Valid @RequestBody CreateDepartmentCmd cmd) {
        log.info("接收创建部门请求: cmd={}", cmd);
        
        Long deptId = departmentAppService.createDepartment(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", Map.of("deptId", deptId));
        return result;
    }
    
    /**
     * 更新部门
     */
    @PutMapping("/{deptId}")
    public Map<String, Object> updateDepartment(
            @PathVariable Long deptId,
            @Valid @RequestBody UpdateDepartmentCmd cmd) {
        log.info("接收更新部门请求: deptId={}, cmd={}", deptId, cmd);
        
        cmd.setId(deptId);
        departmentAppService.updateDepartment(cmd);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新成功");
        return result;
    }
    
    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public Map<String, Object> deleteDepartment(@PathVariable Long deptId) {
        log.info("接收删除部门请求: deptId={}", deptId);
        
        departmentAppService.deleteDepartment(deptId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }
    
    /**
     * 查询部门详情
     */
    @GetMapping("/{deptId}")
    public Map<String, Object> getDepartmentById(@PathVariable Long deptId) {
        log.info("接收查询部门详情请求: deptId={}", deptId);
        
        Department department = departmentAppService.getDepartmentById(deptId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", department);
        return result;
    }
    
    /**
     * 查询所有部门（平铺列表）
     */
    @GetMapping("/list")
    public Map<String, Object> listAllDepartments() {
        log.info("接收查询所有部门请求");
        
        List<Department> departments = departmentAppService.listAllDepartments();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", departments);
        return result;
    }
    
    /**
     * 查询部门树（树形结构）
     */
    @GetMapping("/tree")
    public Map<String, Object> getDepartmentTree() {
        log.info("接收查询部门树请求");
        
        List<Department> departmentTree = departmentAppService.getDepartmentTree();
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", departmentTree);
        return result;
    }
    
    /**
     * 查询指定父部门下的子部门
     */
    @GetMapping("/children/{parentId}")
    public Map<String, Object> listChildrenDepartments(@PathVariable Long parentId) {
        log.info("接收查询子部门请求: parentId={}", parentId);
        
        List<Department> children = departmentAppService.listChildrenDepartments(parentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", children);
        return result;
    }
}

