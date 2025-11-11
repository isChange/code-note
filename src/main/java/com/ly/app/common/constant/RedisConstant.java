package com.ly.app.common.constant;

/**
 * Description: reids 常量
 * <p>
 * Redis命名规范:
 * 1、和表结构相关     表名+":"+字段名+":"+字段值      例如: sys_user:age:123456789
 * 2、和业务相关      业务名+":"+业务字段+":"+业务值   例如: sys:id:1234
 * 3、接口相关       接口名+":"+接口字段+":"+字段值   例如: api_login:id:123456789
 *
 * @author: liuyi
 */
public class RedisConstant {


    public static final Integer DEFAULT_EXPIRE_TIME = 60 * 60 * 12;

    /**
     * 标签列表数据
     */
    public final static String ALL_TAG = "tag:all";


}
