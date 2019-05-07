package com.xf_mingsu.common.captcha;

import com.alibaba.fastjson.JSON;
import com.xf_mingsu.common.Base64ImageConverter;
import com.xf_mingsu.common.CommUtils;
import com.xf_mingsu.common.StringUtil;
import com.xf_mingsu.common.SystemCash;
import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.xf_mingsu.common.captcha.Randoms.num;

@Slf4j
public class SpecCaptcha extends Captcha {
    public SpecCaptcha()
    {
    }
    public SpecCaptcha(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    public SpecCaptcha(int width, int height, int len){
        this(width,height);
        this.len = len;
    }
    public SpecCaptcha(int width, int height, int len, Font font){
        this(width,height,len);
        this.font = font;
    }
    /**
     * 生成验证码
     * @throws java.io.IOException IO异常
     */
    @Override
    public  Map<String,Object> out(OutputStream out){
        return graphicsImage(alphas(), out,null);
    }

    @Override
    public Map<String, Object> out(OutputStream out, HttpServletRequest request) {
        return graphicsImage(alphas(), out,request);
    }

    /**
     * 画随机码图
     * @param strs 文本
     * @param out 输出流
     */
    private Map<String,Object> graphicsImage(char[] strs, OutputStream out,HttpServletRequest request
    ){
        String captchaTmp = String.valueOf(strs);
        Map<String,Object> resultMap = new HashMap<>();
        boolean ok = false;
        try
        {
            BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D)bi.getGraphics();
            AlphaComposite ac3;
            Color color ;
            int len = strs.length;
            g.setColor(Color.WHITE);
            g.fillRect(0,0,width,height);
            // 随机画干扰的蛋蛋
            for(int i=0;i<15;i++){
                color = color(150, 250);
                g.setColor(color);
                g.drawOval(num(width), num(height), 5+num(10), 5+num(10));// 画蛋蛋，有蛋的生活才精彩
                color = null;
            }
            g.setFont(font);
            int h  = height - ((height - font.getSize()) >>1),
                    w = width/len,
                    size = w-font.getSize()+1;
            /* 画字符串 */
            for(int i=0;i<len;i++) {
                ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);// 指定透明度
                g.setComposite(ac3);
                color = new Color(20 + num(110), 20 + num(110), 20 + num(110));// 对每个字符都用随机颜色
                g.setColor(color);
                g.drawString(strs[i] + "", (width - (len - i) * w) + size, h - 4);
                color = null;
                ac3 = null;
            }

            log.info("将图形验证码存入Session,value为:"+JSON.toJSONString(captchaTmp));
            SystemCash.setKey("xf:image:capatcha",captchaTmp);
            log.info("存储成功;value 为:" + JSON.toJSONString(SystemCash.getKey("xf:image:capatcha")));
//            HttpSession session =  request.getSession();
//            //存储到session中
//            if(!CommUtils.isNull(request)) {
//                log.info("将图形验证码存入Session,value为:"+JSON.toJSONString(captchaTmp));
//                session.setAttribute("xf:image:capatcha", captchaTmp);
//                log.info("存储成功;value 为:" + JSON.toJSONString(session.getAttribute("xf:image:capatcha")));
//            }

//            ByteArrayOutputStream os = new ByteArrayOutputStream();
              ImageIO.write(bi, "png",out);
//            InputStream is = new ByteArrayInputStream(os.toByteArray());
//            String imgCode = Base64ImageConverter.imageInputStreamToBase64(is);
//            System.out.println(imgCode);
//            ok = true;
            resultMap.put("imageCode",captchaTmp);
            return resultMap;
        }catch (Exception e){
            ok = false;
        }finally
        {
            Streams.close(out);
        }
        return resultMap;
    }
}
