package com.xf_mingsu.mapper.pojo;

import java.util.Date;

public class users {
    private Integer id;

    private String userUnionid;

    private String userOpenid;

    private String userNickname;

    private String userPhone;

    private String userAvatarurl;

    private Integer userGender;

    private Byte flagDel;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserUnionid() {
        return userUnionid;
    }

    public void setUserUnionid(String userUnionid) {
        this.userUnionid = userUnionid;
    }

    public String getUserOpenid() {
        return userOpenid;
    }

    public void setUserOpenid(String userOpenid) {
        this.userOpenid = userOpenid;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAvatarurl() {
        return userAvatarurl;
    }

    public void setUserAvatarurl(String userAvatarurl) {
        this.userAvatarurl = userAvatarurl;
    }

    public Integer getUserGender() {
        return userGender;
    }

    public void setUserGender(Integer userGender) {
        this.userGender = userGender;
    }

    public Byte getFlagDel() {
        return flagDel;
    }

    public void setFlagDel(Byte flagDel) {
        this.flagDel = flagDel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}