package com.bank.workflow.adapter.web;

import com.bank.workflow.app.config.SystemParamAppService;
import com.bank.workflow.domain.config.entity.SystemParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/config/params")
@RequiredArgsConstructor
public class SystemParamController {

    private final SystemParamAppService systemParamAppService;

    /**
     * 查询参数列表
     */
    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String paramGroup) {
        log.info("查询系统参数列表，paramGroup: {}", paramGroup);

        List<SystemParam> params = paramGroup != null && !paramGroup.isEmpty()
                ? systemParamAppService.listByGroup(paramGroup)
                : systemParamAppService.listAll();

        return success("查询成功", params);
    }

    /**
     * 根据ID查询参数
     */
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        log.info("查询系统参数，ID: {}", id);

        SystemParam param = systemParamAppService.getById(id);
        if (param == null) {
            return error("参数不存在");
        }

        return success("查询成功", param);
    }

    /**
     * 根据参数键查询参数
     */
    @GetMapping("/key/{key}")
    public Map<String, Object> getByKey(@PathVariable String key) {
        log.info("根据参数键查询，key: {}", key);

        SystemParam param = systemParamAppService.getByKey(key);
        if (param == null) {
            return error("参数不存在");
        }

        return success("查询成功", param);
    }

    /**
     * 创建参数
     */
    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody SystemParam param) {
        log.info("创建系统参数，paramKey: {}", param.getParamKey());

        try {
            boolean success = systemParamAppService.create(param);
            return success ? success("创建成功") : error("创建失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 更新参数
     */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody SystemParam param) {
        log.info("更新系统参数，ID: {}", id);

        param.setId(id);
        try {
            boolean success = systemParamAppService.update(param);
            return success ? success("更新成功") : error("更新失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 删除参数
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        log.info("删除系统参数，ID: {}", id);

        try {
            boolean success = systemParamAppService.delete(id);
            return success ? success("删除成功") : error("删除失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 刷新参数缓存
     */
    @PostMapping("/refresh-cache")
    public Map<String, Object> refreshCache() {
        log.info("刷新系统参数缓存");

        systemParamAppService.refreshCache();
        return success("缓存刷新成功");
    }

    /**
     * 成功响应
     */
    private Map<String, Object> success(String message) {
        return success(message, null);
    }

    /**
     * 成功响应（带数据）
     */
    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", message);
        if (data != null) {
            result.put("data", data);
        }
        return result;
    }

    /**
     * 错误响应
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        return result;
    }
}

