package com.bank.workflow.app.config;

import com.bank.workflow.domain.config.entity.SystemParam;
import com.bank.workflow.domain.config.gateway.SystemParamGateway;
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
 * SystemParamAppService单元测试
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SystemParamAppServiceTest {

    @Mock
    private SystemParamGateway systemParamGateway;

    @InjectMocks
    private SystemParamAppService systemParamAppService;

    private SystemParam testParam;

    @BeforeEach
    void setUp() {
        testParam = new SystemParam();
        testParam.setId(1L);
        testParam.setParamKey("test.param.key");
        testParam.setParamValue("test-value");
        testParam.setParamName("测试参数");
        testParam.setParamType(SystemParam.ParamType.STRING);
        testParam.setParamGroup("test");
        testParam.setIsSystem(false);
        testParam.setCreatedTime(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        // Given
        when(systemParamGateway.getById(1L)).thenReturn(testParam);

        // When
        SystemParam result = systemParamAppService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getParamKey()).isEqualTo("test.param.key");
        verify(systemParamGateway, times(1)).getById(1L);
    }

    @Test
    void testGetByKey_Success() {
        // Given
        when(systemParamGateway.getByKey("test.param.key")).thenReturn(testParam);

        // When
        SystemParam result = systemParamAppService.getByKey("test.param.key");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getParamKey()).isEqualTo("test.param.key");
        verify(systemParamGateway, times(1)).getByKey("test.param.key");
    }

    @Test
    void testListAll_Success() {
        // Given
        List<SystemParam> params = Arrays.asList(testParam);
        when(systemParamGateway.listAll()).thenReturn(params);

        // When
        List<SystemParam> result = systemParamAppService.listAll();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(systemParamGateway, times(1)).listAll();
    }

    @Test
    void testListByGroup_Success() {
        // Given
        List<SystemParam> params = Arrays.asList(testParam);
        when(systemParamGateway.listByGroup("test")).thenReturn(params);

        // When
        List<SystemParam> result = systemParamAppService.listByGroup("test");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParamGroup()).isEqualTo("test");
        verify(systemParamGateway, times(1)).listByGroup("test");
    }

    @Test
    void testCreate_Success() {
        // Given
        when(systemParamGateway.getByKey("test.param.key")).thenReturn(null);
        when(systemParamGateway.save(any(SystemParam.class))).thenReturn(true);

        // When
        boolean result = systemParamAppService.create(testParam);

        // Then
        assertThat(result).isTrue();
        assertThat(testParam.getCreatedTime()).isNotNull();
        verify(systemParamGateway, times(1)).getByKey("test.param.key");
        verify(systemParamGateway, times(1)).save(any(SystemParam.class));
    }

    @Test
    void testCreate_DuplicateKey_ThrowsException() {
        // Given
        when(systemParamGateway.getByKey("test.param.key")).thenReturn(testParam);

        // When & Then
        assertThatThrownBy(() -> systemParamAppService.create(testParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("参数键已存在");
        
        verify(systemParamGateway, times(1)).getByKey("test.param.key");
        verify(systemParamGateway, never()).save(any(SystemParam.class));
    }

    @Test
    void testUpdate_Success() {
        // Given
        when(systemParamGateway.getById(1L)).thenReturn(testParam);
        when(systemParamGateway.update(any(SystemParam.class))).thenReturn(true);

        // When
        boolean result = systemParamAppService.update(testParam);

        // Then
        assertThat(result).isTrue();
        assertThat(testParam.getUpdatedTime()).isNotNull();
        verify(systemParamGateway, times(1)).getById(1L);
        verify(systemParamGateway, times(1)).update(any(SystemParam.class));
    }

    @Test
    void testUpdate_NotFound_ThrowsException() {
        // Given
        when(systemParamGateway.getById(1L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> systemParamAppService.update(testParam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("参数不存在");
        
        verify(systemParamGateway, times(1)).getById(1L);
        verify(systemParamGateway, never()).update(any(SystemParam.class));
    }

    @Test
    void testDelete_Success() {
        // Given
        testParam.setIsSystem(false);
        when(systemParamGateway.getById(1L)).thenReturn(testParam);
        when(systemParamGateway.deleteById(1L)).thenReturn(true);

        // When
        boolean result = systemParamAppService.delete(1L);

        // Then
        assertThat(result).isTrue();
        verify(systemParamGateway, times(1)).getById(1L);
        verify(systemParamGateway, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_SystemParam_ThrowsException() {
        // Given
        testParam.setIsSystem(true);
        when(systemParamGateway.getById(1L)).thenReturn(testParam);

        // When & Then
        assertThatThrownBy(() -> systemParamAppService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("系统参数不可删除");
        
        verify(systemParamGateway, times(1)).getById(1L);
        verify(systemParamGateway, never()).deleteById(1L);
    }

    @Test
    void testDelete_NotFound_ThrowsException() {
        // Given
        when(systemParamGateway.getById(1L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> systemParamAppService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("参数不存在");
        
        verify(systemParamGateway, times(1)).getById(1L);
        verify(systemParamGateway, never()).deleteById(1L);
    }

    @Test
    void testRefreshCache_Success() {
        // When
        systemParamAppService.refreshCache();

        // Then - 只验证方法执行不抛异常
        // 缓存刷新通过@CacheEvict注解实现，此处只测试方法调用
    }
}

