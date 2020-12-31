package com.terry.account.bean;


import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

/**
 * 一年支出的数据结构
 */

@SmartTable(name="年度支出报表",count = true)
public class OutTableBean {
    //主键
    private String id;
    @SmartColumn(id =1,name = "支出类型",autoCount = true)
    private String type;
    @SmartColumn(id =2,name = "账户类别")
    private String fromType = "";
    @SmartColumn(id =3,name = "金额",autoCount = true)
    private double money;
    @SmartColumn(id =4,name = "时间")
    private String time;
    @SmartColumn(id =5,name = "备注")
    private String mark;//备注
    private String user;//所属的用户

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
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

    public static OutTableBean getInTableBean(AccountBean accountBean){
        OutTableBean inTableBean = new OutTableBean();
        inTableBean.setId(accountBean.getId());
        inTableBean.setFromType(accountBean.getFromType());
        inTableBean.setMark(accountBean.getMark());
        inTableBean.setTime(accountBean.getTime());
        inTableBean.setMoney(Double.parseDouble(accountBean.getMoney()));
        inTableBean.setType(accountBean.getType());
        inTableBean.setUserName(accountBean.getUserName());
        return inTableBean;
    }

}
