package com.zty.xiaomi.server.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

// 自动发送请求
public class LogUploader {
    public static int randomNumberGenerator(int min,int max){
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static void sendHttpRequest(String spec, String ip, Map<String,Object> args) throws InterruptedException {
        try{
            URL url = new URL(spec);
//                URL url = new URL("http://10.16.22.76:8081/nginxController/loggen");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为post
            conn.setRequestMethod("GET");
            //时间头用来供server进行时钟校对的
            conn.setRequestProperty("clientTime", System.currentTimeMillis() + "");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 设置伪装的ip
            conn.setRequestProperty("X-Forwarded-For",ip);
            //允许上传数据
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置请求的头信息,设置内容类型为JSON
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream outputStream = conn.getOutputStream();

            String data = "";//username=zmh&password=15023119606
            // 解析参数,并将参数拼接起来
            Iterator<Map.Entry<String, Object>> iterator = args.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> entry = iterator.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                String tempArg = key + "=" + value;
                data += tempArg;
                if (iterator.hasNext()) {
                    data += "&";
                }
            }

            outputStream.write(data.getBytes()); //上传参数
            System.out.println("upload success");
            //输出流
            OutputStream out = conn.getOutputStream();
            out.flush();
            out.close();
            int code = conn.getResponseCode();
            System.out.println(code);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        while (true){
            // 首先生成随机的代理ip
            String proxyHost = "";
            for (int i = 0; i < 4; i++) {
                proxyHost += randomNumberGenerator(1,254) + "";
                // 随后个数字末尾不需要加 "."
                if (i != 3){
                    proxyHost += ".";
                }
            }
            String[] urls = {
                    "http://localhost:8080/login/UserLogin",//username、password
                    "http://localhost:8080/product/getinfo",//id
                    "http://localhost:8080/index/category",
                    "http://localhost:8080/carts/push",//POST：productId、select、token、username
                    "http://localhost:8080/orders/pay"//orderNo=846530854
            };
            Map<String,Object> arg = new HashMap<>();
            arg.put("username","zmh");
            arg.put("password","15023119606");
            sendHttpRequest(urls[0],proxyHost,arg);

            Thread.sleep(7000);
        }
    }
}
