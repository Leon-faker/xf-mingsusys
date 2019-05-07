package com.xf_mingsu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xf_mingsu.common.CommUtils;
import com.xf_mingsu.common.SystemCash;
import com.xf_mingsu.mapper.pojo.users;
import com.xf_mingsu.mapper.usersMapper;
import com.xf_mingsu.service.UserService;
import com.xf_mingsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    public usersMapper usersMapperDto;

    @Override
    public Result userLoginByPhoneCaptcha(String phone, String captchaCode, String smsCode, HttpServletRequest request) {
        log.info("用户手机验证码登录【Service】");
        HttpSession session = request.getSession();
        if(CommUtils.isNull(phone)){
            return Result.fail("手机号不能为空");
        }

        if(phone.length() != 11){
            return  Result.fail("手机号输入有误");
        }

        if(CommUtils.isNull(captchaCode)) {
            return Result.fail("图形验证码不能为空");
        }

        log.info("登陆验证，图形验证码为:"+ JSON.toJSONString(SystemCash.getKey("xf:image:capatcha")));
        if(!(captchaCode.equalsIgnoreCase(String.valueOf(SystemCash.getKey("xf:image:capatcha"))))){
            return Result.fail("图形验证码不正确");
        }

        if(CommUtils.isNull(smsCode)){
            return Result.fail("手机验证码不能为空");
        }

        if(CommUtils.isNull(session.getAttribute("xf:sms:captcha"))){
            return Result.fail("短信验证码已过期");
        }

        log.info("登陆验证，短信验证码为:"+ JSON.toJSONString(session.getAttribute("xf:sms:captcha")));
        if(!(smsCode.equals(session.getAttribute("xf:sms:captcha")))){
            return Result.fail("短信验证码不正确");
        }



        return Result.success("请求成功");
    }

    @Override
    public Result saveUserInfo(String jsonUserInfo) {

        if(CommUtils.isNull(jsonUserInfo)){
            return Result.fail("获取用户信息失败");
        }

        users user = new users();
        JSONObject jsonObject = JSONObject.parseObject(jsonUserInfo);
        if(!CommUtils.isNull(jsonObject.getString("openId"))){
            user.setUserOpenid(jsonObject.getString("openId"));
        }
        //TODO unionId
        //if()

        if(!CommUtils.isNull(jsonObject.getString("nickName"))){
            user.setUserNickname(jsonObject.getString("nickName"));
        }
        //TODO phone
        //if()

        if(!CommUtils.isNull(jsonObject.getString("avatarUrl"))){
            user.setUserAvatarurl(jsonObject.getString("avatarUrl"));
        }

        if(CommUtils.isNull(jsonObject.getString("gender"))){
            user.setUserGender(Integer.parseInt(jsonObject.getString("gender")));
        }

        user.setCreateTime(new Date());
        try {
            int sqlResult = usersMapperDto.insertSelective(user);
            return sqlResult >= 1 ? Result.success("登陆成功") : Result.fail("登陆失败");
        }catch (Exception e){
            log.error("登陆保存用户信息异常:"+e.getMessage());
            return Result.fail("登陆异常");
        }

    }

}
