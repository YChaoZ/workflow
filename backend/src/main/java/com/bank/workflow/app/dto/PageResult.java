package com.bank.workflow.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /** 数据列表 */
    private List<T> list;
    
    /** 总记录数 */
    private Long total;
    
    /** 当前页码 */
    private Integer pageNum;
    
    /** 每页大小 */
    private Integer pageSize;
    
    /** 总页数 */
    private Integer totalPages;
    
    /**
     * 计算总页数
     */
    public void calculateTotalPages() {
        if (this.total != null && this.pageSize != null && this.pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) this.total / this.pageSize);
        }
    }
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.calculateTotalPages();
        return result;
    }
    
    /**
     * 简化的构造器（只需要total和list）
     */
    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}

