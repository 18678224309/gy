package com.jtfu.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;

import java.awt.*;

public class Hutool {
    public static void main(String[] args) {
             //定义图形验证码的长和宽
             LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

            //图形验证码写出，可以写出到文件，也可以写出到流
            // lineCaptcha.write("d:/line.png");

             //输出code
             Console.log(lineCaptcha.getCode());
             //验证图形验证码的有效性，返回boolean值
             boolean verify = lineCaptcha.verify("1234");
             System.out.println(verify);

    }
}
