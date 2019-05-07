package com.xf_mingsu.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class WeChatConfig {
    private static final String APPID = "wx8ef4346d7856e350";

    private static final String SECRET = "1b668f9e10c23c88bf7615dd1550cb4c";

    public static String js_Code = "";

    public static String requestWxLoginAuth(HttpServletRequest request){
        try {
            if(CommUtils.isChinaPhoneLegal(js_Code)){
                return new RuntimeException("js_Code can't is null").toString();
            }

            String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+APPID+"&secret="+SECRET+"&js_code="+js_Code+"&grant_type=authorization_code";
            String result = HttpClientUtil.doGet(url);
            log.info("微信返回openid 、 session_key:"+JSON.toJSONString(result));
            JSONObject jsonObject = JSONObject.parseObject(result);
            request.getSession().setAttribute("session_key",jsonObject.getString("session_key"));
            log.info("存储之后:"+request.getSession().getAttribute("session_key"));
            return result;
        }catch (Exception e){
            log.error("请求微信code异常:"+e.getMessage());
            return null;
        }
    }
}
