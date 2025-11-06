package com.bank.workflow.app.user.query;

import lombok.Data;

/**
 * 用户分页查询
 *
 * @author Workflow Team
 * @since 1.0.0
 */
@Data
public class UserPageQuery {

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}

