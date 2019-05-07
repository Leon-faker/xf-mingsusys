package com.xf_mingsu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping(value = "/sys")
public class SystemController {

    @PostMapping(value = "/getSessionId")
    @ResponseBody
    public void userLoginByPhoneCaptcha(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Set-Cookie",request.getSession().getId());
    }

}
