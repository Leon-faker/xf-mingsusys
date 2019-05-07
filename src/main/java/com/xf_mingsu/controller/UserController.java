package com.xf_mingsu.controller;

import com.alibaba.fastjson.JSON;
import com.xf_mingsu.common.CommUtils;
import com.xf_mingsu.common.WeChatConfig;
import com.xf_mingsu.common.captcha.Captcha;
import com.xf_mingsu.common.captcha.SpecCaptcha;
import com.xf_mingsu.service.SmsService;
import com.xf_mingsu.service.UserService;
import com.xf_mingsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @PostMapping(value = "/login")
    @ResponseBody
    public Result userLoginByPhoneCaptcha(@RequestBody Map<String,Object> map, HttpServletRequest request){
        log.info("请求参数:"+ JSON.toJSONString(map));
        if(CommUtils.isNull(map)){
            return Result.fail("请求参数不能为空");
        }
        return userService.userLoginByPhoneCaptcha(map.get("phone").toString(),map.get("captchaCode").toString(),map.get("smsCode").toString(),request);
    }

    @PostMapping(value = "/sms")
    @ResponseBody
    public Result sendSms(@RequestBody Map<String,Object> map, HttpServletRequest request){
        log.info("请求参数:"+ JSON.toJSONString(map));
        if(CommUtils.isNull(map)){
            return Result.fail("请求参数不能为空");
        }
        return smsService.sendValidCode(map.get("phone").toString(),request);
    }

    @GetMapping(value = "/getCaptcha")
    @ResponseBody
    public void getCaptcha(@RequestParam(value = "tokenId") String tokenId
            ,HttpServletRequest request,HttpServletResponse response){

        log.info("SessionId:"+request.getSession().getId());
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        log.info("获取验证码,请求参数:"+tokenId);
        Captcha captcha = new SpecCaptcha(150,40,5);
        try {
            Map<String,Object> map = captcha.out(response.getOutputStream(),request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
