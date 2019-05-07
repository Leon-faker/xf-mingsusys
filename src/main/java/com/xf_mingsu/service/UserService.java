package com.xf_mingsu.service;

import com.xf_mingsu.vo.Result;

import javax.servlet.http.HttpServletRequest;

/**
 *  user service interface
 */
public interface UserService {

    /**
     *  用户登录(手机验证码登陆)
     * @param phone 手机号
     * @param captchaCode
     * @param smsCode
     * @return
     */
    Result userLoginByPhoneCaptcha(String phone, String captchaCode, String smsCode, HttpServletRequest request);

    /**
     * 登陆保存用户信息
     * @param jsonUserInfo
     * @return
     */
    Result saveUserInfo(String jsonUserInfo);
}
