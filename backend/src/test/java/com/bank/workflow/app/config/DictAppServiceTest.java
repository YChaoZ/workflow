package com.bank.workflow.app.config;

import com.bank.workflow.domain.config.entity.DictData;
import com.bank.workflow.domain.config.entity.DictType;
import com.bank.workflow.domain.config.gateway.DictGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DictAppService单元测试
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DictAppServiceTest {

    @Mock
    private DictGateway dictGateway;

    @InjectMocks
    private DictAppService dictAppService;

    private DictType testDictType;
    private DictData testDictData;

    @BeforeEach
    void setUp() {
        // 初始化字典类型
        testDictType = new DictType();
        testDictType.setId(1L);
        testDictType.setDictCode("test_type");
        testDictType.setDictName("测试类型");
        testDictType.setIsSystem(false);
        testDictType.setStatus(true);
        testDictType.setCreatedTime(LocalDateTime.now());

        // 初始化字典数据
        testDictData = new DictData();
        testDictData.setId(1L);
        testDictData.setDictTypeId(1L);
        testDictData.setDictCode("test_type");
        testDictData.setDictLabel("测试标签");
        testDictData.setDictValue("TEST_VALUE");
        testDictData.setTagType("primary");
        testDictData.setStatus(true);
        testDictData.setCreatedTime(LocalDateTime.now());
    }

    // ==================== 字典类型测试 ====================

    @Test
    void testGetDictTypeById_Success() {
        // Given
        when(dictGateway.getDictTypeById(1L)).thenReturn(testDictType);

        // When
        DictType result = dictAppService.getDictTypeById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDictCode()).isEqualTo("test_type");
        verify(dictGateway, times(1)).getDictTypeById(1L);
    }

    @Test
    void testGetDictTypeByCode_Success() {
        // Given
        when(dictGateway.getDictTypeByCode("test_type")).thenReturn(testDictType);

        // When
        DictType result = dictAppService.getDictTypeByCode("test_type");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDictCode()).isEqualTo("test_type");
        verify(dictGateway, times(1)).getDictTypeByCode("test_type");
    }

    @Test
    void testListAllDictTypes_Success() {
        // Given
        List<DictType> types = Arrays.asList(testDictType);
        when(dictGateway.listAllDictTypes()).thenReturn(types);

        // When
        List<DictType> result = dictAppService.listAllDictTypes();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(dictGateway, times(1)).listAllDictTypes();
    }

    @Test
    void testCreateDictType_Success() {
        // Given
        when(dictGateway.getDictTypeByCode("test_type")).thenReturn(null);
        when(dictGateway.saveDictType(any(DictType.class))).thenReturn(true);

        // When
        boolean result = dictAppService.createDictType(testDictType);

        // Then
        assertThat(result).isTrue();
        assertThat(testDictType.getCreatedTime()).isNotNull();
        verify(dictGateway, times(1)).getDictTypeByCode("test_type");
        verify(dictGateway, times(1)).saveDictType(any(DictType.class));
    }

    @Test
    void testCreateDictType_DuplicateCode_ThrowsException() {
        // Given
        when(dictGateway.getDictTypeByCode("test_type")).thenReturn(testDictType);

        // When & Then
        assertThatThrownBy(() -> dictAppService.createDictType(testDictType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("字典编码已存在");
        
        verify(dictGateway, times(1)).getDictTypeByCode("test_type");
        verify(dictGateway, never()).saveDictType(any(DictType.class));
    }

    @Test
    void testUpdateDictType_Success() {
        // Given
        when(dictGateway.getDictTypeById(1L)).thenReturn(testDictType);
        when(dictGateway.updateDictType(any(DictType.class))).thenReturn(true);

        // When
        boolean result = dictAppService.updateDictType(testDictType);

        // Then
        assertThat(result).isTrue();
        assertThat(testDictType.getUpdatedTime()).isNotNull();
        verify(dictGateway, times(1)).getDictTypeById(1L);
        verify(dictGateway, times(1)).updateDictType(any(DictType.class));
    }

    @Test
    void testDeleteDictType_Success() {
        // Given
        testDictType.setIsSystem(false);
        when(dictGateway.getDictTypeById(1L)).thenReturn(testDictType);
        when(dictGateway.deleteDictTypeById(1L)).thenReturn(true);

        // When
        boolean result = dictAppService.deleteDictType(1L);

        // Then
        assertThat(result).isTrue();
        verify(dictGateway, times(1)).getDictTypeById(1L);
        verify(dictGateway, times(1)).deleteDictTypeById(1L);
    }

    @Test
    void testDeleteDictType_SystemDict_ThrowsException() {
        // Given
        testDictType.setIsSystem(true);
        when(dictGateway.getDictTypeById(1L)).thenReturn(testDictType);

        // When & Then
        assertThatThrownBy(() -> dictAppService.deleteDictType(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("系统字典不可删除");
        
        verify(dictGateway, times(1)).getDictTypeById(1L);
        verify(dictGateway, never()).deleteDictTypeById(1L);
    }

    // ==================== 字典数据测试 ====================

    @Test
    void testGetDictDataById_Success() {
        // Given
        when(dictGateway.getDictDataById(1L)).thenReturn(testDictData);

        // When
        DictData result = dictAppService.getDictDataById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDictLabel()).isEqualTo("测试标签");
        verify(dictGateway, times(1)).getDictDataById(1L);
    }

    @Test
    void testListDictDataByTypeCode_Success() {
        // Given
        List<DictData> dataList = Arrays.asList(testDictData);
        when(dictGateway.listDictDataByTypeCode("test_type")).thenReturn(dataList);

        // When
        List<DictData> result = dictAppService.listDictDataByTypeCode("test_type");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDictCode()).isEqualTo("test_type");
        verify(dictGateway, times(1)).listDictDataByTypeCode("test_type");
    }

    @Test
    void testListDictDataByTypeId_Success() {
        // Given
        List<DictData> dataList = Arrays.asList(testDictData);
        when(dictGateway.listDictDataByTypeId(1L)).thenReturn(dataList);

        // When
        List<DictData> result = dictAppService.listDictDataByTypeId(1L);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(dictGateway, times(1)).listDictDataByTypeId(1L);
    }

    @Test
    void testCreateDictData_Success() {
        // Given
        when(dictGateway.saveDictData(any(DictData.class))).thenReturn(true);

        // When
        boolean result = dictAppService.createDictData(testDictData);

        // Then
        assertThat(result).isTrue();
        assertThat(testDictData.getCreatedTime()).isNotNull();
        verify(dictGateway, times(1)).saveDictData(any(DictData.class));
    }

    @Test
    void testUpdateDictData_Success() {
        // Given
        when(dictGateway.getDictDataById(1L)).thenReturn(testDictData);
        when(dictGateway.updateDictData(any(DictData.class))).thenReturn(true);

        // When
        boolean result = dictAppService.updateDictData(testDictData);

        // Then
        assertThat(result).isTrue();
        assertThat(testDictData.getUpdatedTime()).isNotNull();
        verify(dictGateway, times(1)).getDictDataById(1L);
        verify(dictGateway, times(1)).updateDictData(any(DictData.class));
    }

    @Test
    void testDeleteDictData_Success() {
        // Given
        when(dictGateway.getDictDataById(1L)).thenReturn(testDictData);
        when(dictGateway.deleteDictDataById(1L)).thenReturn(true);

        // When
        boolean result = dictAppService.deleteDictData(1L);

        // Then
        assertThat(result).isTrue();
        verify(dictGateway, times(1)).getDictDataById(1L);
        verify(dictGateway, times(1)).deleteDictDataById(1L);
    }

    @Test
    void testDeleteDictData_NotFound_ThrowsException() {
        // Given
        when(dictGateway.getDictDataById(1L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> dictAppService.deleteDictData(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("字典数据不存在");
        
        verify(dictGateway, times(1)).getDictDataById(1L);
        verify(dictGateway, never()).deleteDictDataById(1L);
    }

    @Test
    void testRefreshCache_Success() {
        // When
        dictAppService.refreshCache();

        // Then - 只验证方法执行不抛异常
        // 缓存刷新通过@CacheEvict注解实现，此处只测试方法调用
    }
}

