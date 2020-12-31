package com.terry.account.bean;


/**
 * 收入支出的数据结构
 *
 * @author sjy
 */
public class AccountBean{


    //主键
    private String id;
    //金额
    private String money;
    //时间
    private String time;

    //支出 outcome还是收入 income
    private String accountType = "";

    //收入支出具体资产类型
    private String type;

    //账户（微信、支付宝、银行卡）
    private String fromType = "";

    private String user;//所属的用户

    private String mark;//备注

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return user;
    }

    public void setUserName(String userName) {
        this.user = userName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
