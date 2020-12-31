package com.terry.account.bean;

/**
 * 用户信息
 */
public class UserBean {

    private String name;//账户名

    private String pwd;//密码

    private Float dayMax;//日限额

    private Float monthMax;//月限额

    private Float yearMax;//年限额

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Float getDayMax() {
        return Float.valueOf(dayMax);
    }

    public void setDayMax(float dayMax) {
        this.dayMax = Float.valueOf(dayMax);
    }

    public Float getMonthMax() {
        return Float.valueOf(monthMax);
    }

    public void setMonthMax(float monthMax) {
        this.monthMax = Float.valueOf(monthMax);
    }

    public Float getYearMax() {
        return Float.valueOf(yearMax);
    }

    public void setYearMax(float yearMax) {
        this.yearMax = Float.valueOf(yearMax);
    }
}
