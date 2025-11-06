package com.bank.workflow.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 权限持久化对象
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
@TableName("sys_permission")
public class PermissionDO {
    
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 权限编码
     */
    private String permissionCode;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限类型：MENU-菜单，BUTTON-按钮，API-接口
     */
    private String permissionType;
    
    /**
     * 父权限ID（0表示根权限）
     */
    private Long parentId;
    
    /**
     * 资源路径（菜单路由或API路径）
     */
    private String resourcePath;
    
    /**
     * 请求方法（GET/POST/PUT/DELETE）
     */
    private String resourceMethod;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}

