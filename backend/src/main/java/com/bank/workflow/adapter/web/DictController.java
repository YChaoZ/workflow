package com.bank.workflow.adapter.web;

import com.bank.workflow.app.config.DictAppService;
import com.bank.workflow.domain.config.entity.DictData;
import com.bank.workflow.domain.config.entity.DictType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典控制器
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/config/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictAppService dictAppService;

    // ==================== 字典类型 ====================

    /**
     * 查询所有字典类型
     */
    @GetMapping("/types")
    public Map<String, Object> listTypes() {
        log.info("查询所有字典类型");

        List<DictType> types = dictAppService.listAllDictTypes();
        return success("查询成功", types);
    }

    /**
     * 根据ID查询字典类型
     */
    @GetMapping("/types/{id}")
    public Map<String, Object> getTypeById(@PathVariable Long id) {
        log.info("查询字典类型，ID: {}", id);

        DictType type = dictAppService.getDictTypeById(id);
        if (type == null) {
            return error("字典类型不存在");
        }

        return success("查询成功", type);
    }

    /**
     * 创建字典类型
     */
    @PostMapping("/types")
    public Map<String, Object> createType(@Valid @RequestBody DictType dictType) {
        log.info("创建字典类型，dictCode: {}", dictType.getDictCode());

        try {
            boolean success = dictAppService.createDictType(dictType);
            return success ? success("创建成功") : error("创建失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/types/{id}")
    public Map<String, Object> updateType(@PathVariable Long id, @Valid @RequestBody DictType dictType) {
        log.info("更新字典类型，ID: {}", id);

        dictType.setId(id);
        try {
            boolean success = dictAppService.updateDictType(dictType);
            return success ? success("更新成功") : error("更新失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/types/{id}")
    public Map<String, Object> deleteType(@PathVariable Long id) {
        log.info("删除字典类型，ID: {}", id);

        try {
            boolean success = dictAppService.deleteDictType(id);
            return success ? success("删除成功") : error("删除失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    // ==================== 字典数据 ====================

    /**
     * 根据字典类型编码查询字典数据
     */
    @GetMapping("/data/type/{typeCode}")
    public Map<String, Object> listDataByTypeCode(@PathVariable String typeCode) {
        log.info("根据字典类型编码查询字典数据，typeCode: {}", typeCode);

        List<DictData> dataList = dictAppService.listDictDataByTypeCode(typeCode);
        return success("查询成功", dataList);
    }

    /**
     * 根据ID查询字典数据
     */
    @GetMapping("/data/{id}")
    public Map<String, Object> getDataById(@PathVariable Long id) {
        log.info("查询字典数据，ID: {}", id);

        DictData data = dictAppService.getDictDataById(id);
        if (data == null) {
            return error("字典数据不存在");
        }

        return success("查询成功", data);
    }

    /**
     * 创建字典数据
     */
    @PostMapping("/data")
    public Map<String, Object> createData(@Valid @RequestBody DictData dictData) {
        log.info("创建字典数据，dictCode: {}, dictLabel: {}", dictData.getDictCode(), dictData.getDictLabel());

        try {
            boolean success = dictAppService.createDictData(dictData);
            return success ? success("创建成功") : error("创建失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 更新字典数据
     */
    @PutMapping("/data/{id}")
    public Map<String, Object> updateData(@PathVariable Long id, @Valid @RequestBody DictData dictData) {
        log.info("更新字典数据，ID: {}", id);

        dictData.setId(id);
        try {
            boolean success = dictAppService.updateDictData(dictData);
            return success ? success("更新成功") : error("更新失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{id}")
    public Map<String, Object> deleteData(@PathVariable Long id) {
        log.info("删除字典数据，ID: {}", id);

        try {
            boolean success = dictAppService.deleteDictData(id);
            return success ? success("删除成功") : error("删除失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /**
     * 刷新字典缓存
     */
    @PostMapping("/refresh-cache")
    public Map<String, Object> refreshCache() {
        log.info("刷新数据字典缓存");

        dictAppService.refreshCache();
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

