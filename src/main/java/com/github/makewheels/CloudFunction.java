package com.github.makewheels;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SimpleTimeZone;

public class CloudFunction implements StreamRequestHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        String url = System.getenv("url");
        String name = System.getenv("name");
        try {
            HttpUtil.createGet(url).timeout(10 * 1000).execute();
        } catch (Exception e) {
            e.printStackTrace();
            //如果发生错误，推送
            JSONObject body = new JSONObject();
            body.put("toAddress", "finalbird@foxmail.com");
            body.put("fromAlias", "health-check");
            body.put("subject", name);
            body.put("htmlBody",
                    "url:<br>" + url + "<br><br>"
                            + "name = " + name + "<br><br>"
                            + "time = " + DateUtil.formatDateTime(new Date()) + "<br><br>"
                            + "detailMessage:<br>" + e.getMessage()
            );
            String response = HttpUtil.post("http://82.157.172.71:5025/push/sendEmail",
                    body.toJSONString());
            System.out.println(response);
        }
    }

}
