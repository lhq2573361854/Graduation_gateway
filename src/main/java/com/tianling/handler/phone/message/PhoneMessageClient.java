package com.tianling.handler.phone.message;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Tianling
 * @email 859073143@qq.com
 * @since 2021/3/11 9:28
 */
@Data
@Component("phoneMessageClient")
@ConfigurationProperties("phone.message")
public class PhoneMessageClient implements SendMessage {

    private String appId;
    private long timestamp;
    private String secretKey;
    private String version;
    private String signType;
    private String bizContent;
    private String method;
    private String template_id;
    private Integer type;
    private String sign;
    PhoneMessageClient() {
        this.timestamp = System.currentTimeMillis();
    }

    public static String md5(String s) {
        char str[] = new char[32];
        char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] b = md.digest(s.getBytes());
            int k = 0;
            for (int i = 0; i < b.length; i++) {
                str[k++] = hex[b[i] >>> 4 & 0xf];
                str[k++] = hex[b[i] & 0xf];
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(str);
    }

    public String createSignature(HashMap<String, Object> data, String secretKey) {
        Object[] array = data.keySet().toArray();
        Arrays.sort(array);
        ArrayList<String> list = new ArrayList<>();
        for (Object key : array) {
            list.add(key + "=" + data.get(key));
        }
        list.add("key=" + secretKey);

        StringBuilder sb = new StringBuilder();
        for (String v : list) {
            sb.append(v);
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return md5(sb.toString()).toUpperCase();
    }

    public String execute() {

        StringBuilder sb = new StringBuilder();

        HashMap<String, Object> post = new HashMap<>();
        post.put("app_id", this.appId);
        post.put("timestamp", this.timestamp);
        post.put("sign_type", this.signType);
        post.put("version", this.version);
        post.put("method", this.method);
        post.put("biz_content", this.bizContent);
        post.put("sign", createSignature(post, this.secretKey));

        ArrayList<String> list = new ArrayList<>();
        for (String key : post.keySet()) {
            list.add(key + "=" + post.get(key));
        }

        StringBuilder data = new StringBuilder();
        for (String v : list) {
            data.append(v);
            data.append("&");
        }
        data.deleteCharAt(data.length() - 1);

        try {
            URL url = new URL("http://api.shansuma.com/gateway.do");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setUseCaches(false);
            http.setConnectTimeout(15 * 1000);
            http.setReadTimeout(60 * 1000);
            http.setRequestMethod("POST");
            http.setRequestProperty("User-Agent", "Mozilla 5.0 Java-SMS-SDK v1.0.0 (Haowei Tech)");
            http.setRequestProperty("Connection", "close");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("Content-Length", "" + data.length());
            http.setDoOutput(true);
            http.setDoInput(true);

            OutputStream ops = http.getOutputStream();
            ops.write(data.toString().getBytes());
            ops.flush();
            ops.close();

            String next;
            InputStreamReader reader = new InputStreamReader(http.getInputStream());
            BufferedReader buffered = new BufferedReader(reader);
            while ((next = buffered.readLine()) != null) {
                sb.append(next);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String sendMessage(String phone,String code){
        this.setBizContent("{\"mobile\":[\""+ phone +"\"],\"template_id\":\""+this.template_id+"\",\"type\":"+this.type+",\"params\":{\"code\":" + code + " },\"sign\":\""+this.sign+"\"}");
        return  execute();
    }

}