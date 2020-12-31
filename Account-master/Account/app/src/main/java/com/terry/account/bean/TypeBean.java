package com.terry.account.bean;

/**
 * 收入和消费类型的数据结构
 *
 * @author sjy
 */
public class TypeBean{

    //主键
    private String id;
    //资产名称
    private String name;
    //资产类型  支出 outcome 还是收入 income
    private String type;

    private String userName;//所属的用户

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
