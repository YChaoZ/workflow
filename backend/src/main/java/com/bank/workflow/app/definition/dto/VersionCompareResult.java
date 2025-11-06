package com.bank.workflow.app.definition.dto;

import lombok.Data;

/**
 * 版本对比结果
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class VersionCompareResult {
    
    /**
     * 源版本号
     */
    private Integer sourceVersion;
    
    /**
     * 目标版本号
     */
    private Integer targetVersion;
    
    /**
     * 源版本XML
     */
    private String sourceXml;
    
    /**
     * 目标版本XML
     */
    private String targetXml;
    
    /**
     * 是否有差异
     */
    private Boolean hasDifference;
    
    /**
     * 差异说明
     */
    private String differenceDescription;
}

