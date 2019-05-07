package com.xf_mingsu.service.impl;

import com.xf_mingsu.common.CommUtils;
import com.xf_mingsu.service.SmsService;
import com.xf_mingsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${user.sms.validcode.count}")
    private int SEND_VALIDCODE_COUNT;//每天发送验证码次数
    @Value("${user.sms.reg.validcode.expire}")
    private int SMS_REG_VALIDCODE_EXPIRE;//注册短信验证码失效时间
    @Value("${user.sms.login.validcode.expire}")
    private int SMS_LOGIN_VALIDCODE_EXPIRE;//忘记密码短信验证码失效时间

    @Value("${user.sms.update.pwd.validcode.expire}")
    private int SMS_UPDATE_PWD_VALIDCODE_EXPIRE;//修改密码短信验证码失效时间

    @Value("${user.sms.each_time.expire}")
    private int SMS_EACH_TIME_EXPIRE;//多久才能发送一次

    @Override
    public Result sendValidCode(String phone, HttpServletRequest request) {
        try {
            log.info("【发送短信Service】");
            if(CommUtils.isNull(phone)){
                return Result.fail("手机号不能为空");
            }

            //key
            String key = "xf:sms:captcha";

            if(!CommUtils.isNull(request.getSession().getAttribute(key))){
                return Result.fail("请一分钟之后再试");
            }

            //Sms code
            String smsCode = CommUtils.getRandomNumber(6);

            log.info("【key：】"+key+"【验证码:】"+smsCode);

            //存入session
            HttpSession session = request.getSession();
            session.setAttribute(key,smsCode);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    log.info("删除验证码");
                    // 删除session中存的验证码
                    session.removeAttribute(key);
                    timer.cancel();
                }
            }, 0b1 * 0b111100 * 0b1111101000);

            System.out.println(session.getAttribute(key));

            //TODO 发送短信
            return Result.success("请求成功");
        }catch (Exception e){

        }

        return null;
    }
}
