package com.terry.account.bean;

/**
 * 支付选项的数据结构
 *
 * @author sjy
 */
public class PayBean {

    //主键
    private String id;
    //支付名称
    private String name;

    private String userName;//所属的用户

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
