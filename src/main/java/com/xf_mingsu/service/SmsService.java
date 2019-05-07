package com.xf_mingsu.service;

import com.xf_mingsu.vo.Result;

import javax.servlet.http.HttpServletRequest;

public interface SmsService {
    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    Result sendValidCode(String phone, HttpServletRequest request);
}
