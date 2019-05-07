package com.xf_mingsu.controller;

import com.alibaba.fastjson.JSON;
import com.xf_mingsu.common.CommUtils;
import com.xf_mingsu.common.SystemCash;
import com.xf_mingsu.common.WeChatConfig;
import com.xf_mingsu.common.WechatUtil;
import com.xf_mingsu.service.UserService;
import com.xf_mingsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/wechat")
public class WxLoginController {


    @Autowired
    public UserService userService;

    @PostMapping(value = "/wxAuthLogin")
    @ResponseBody
    public Result wxAuthLogin(@RequestBody Map<String,Object> map, HttpServletRequest request){
        log.info("请求参数:"+ JSON.toJSONString(map));
        String js_code = map.get("jsCode").toString();
        WeChatConfig.js_Code = js_code;
        String result  = WeChatConfig.requestWxLoginAuth(request);
        return result == null ? Result.fail("微信授权登陆失败").setData(result) : Result.success("登陆成功").setData(result);
    }

    @PostMapping(value = "/resolveEncryptedData")
    @ResponseBody
    public Result resolveEncryptedData(@RequestBody Map<String,Object> map,HttpServletRequest request){
        log.info("请求参数:"+ JSON.toJSONString(map));
        if(CommUtils.isNull(map)){
            return Result.fail("参数异常");
        }

        String ecncryptedData = String.valueOf(map.get("ecncryptedData"));
        String sessionKeyB64 = String.valueOf(request.getSession().getAttribute("session_key"));
        String ivb64 = String.valueOf(map.get("ivb64"));
        log.info("Session_Key："+sessionKeyB64);
        String reuslt = WechatUtil.decryptData(ecncryptedData,sessionKeyB64,ivb64);
        return userService.saveUserInfo(reuslt);
    }

}
