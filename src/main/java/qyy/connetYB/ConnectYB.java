package qyy.connetYB;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectYB {
    CloseableHttpClient httpClient = null;
    private String cookies = null;
//                      https://oauth.yiban.cn/code/html?client_id=bc20d1eef7f79107&redirect_uri=https://www.secretzhong.cn:8080/YiBanDemo/content&error=e003
    private String ref="https://oauth.yiban.cn/code/html?client_id=bc20d1eef7f79107&redirect_uri=https://www.secretzhong.cn:8080/YiBanDemo/content&error=e003";
    private String BASE_URL="https://oauth.yiban.cn/code/usersure";
    private String redirect_Url = "https://www.secretzhong.cn:8080/YiBanDemo/content";

    //首次连接获取publicKey
    public void connectIndex() {
        String url = ref;
        httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            cookies = response.getFirstHeader("Set-Cookie").getValue();
            String html = sendGetRequest(url, null);    //发送访问请求并获得响应页面
            String publicKey = getPublicKey(html);
            TestRSA.setPublicKey(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //登录
    public boolean login(String userName, String password) {
        String html = null;
        boolean suc = false;
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        password = TestRSA.getEncryKey(password);
        password = password.replaceAll("\r|\n","");
        params.add(new BasicNameValuePair("oauth_uname", userName));//学号
        params.add(new BasicNameValuePair("oauth_upwd", password));//密码
        params.add(new BasicNameValuePair("client_id", "bc20d1eef7f79107"));
        params.add(new BasicNameValuePair("redirect_uri", redirect_Url));
        params.add(new BasicNameValuePair("state", ""));
        params.add(new BasicNameValuePair("scope", "1,2,3,"));
        params.add(new BasicNameValuePair("display", "html"));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
            html = sendPostRequest(BASE_URL, "", entity);
            System.out.println("html"+html);
            if(html.contains("code=")){
                suc = true;
                System.out.println("登录成功"+suc);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return suc;
    }

    public String sendGetRequest(String url, String ref) {
        // TODO Auto-generated method stub
        HttpGet httpGet = new HttpGet(url);
        String strEntity = null;
        httpGet.setHeader("Cookie", cookies);//设置cookie
        if (ref != null && !ref.equals("")) {
            httpGet.setHeader("Referer", ref);//如果有地址引用则设置
        }
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);//提交请求获得响应
            HttpEntity httpEntity = response.getEntity();
            strEntity = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strEntity;
    }

    public String sendPostRequest(String url, String ref, HttpEntity entity) {
        // TODO Auto-generated method stub
        String strEntity = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Cookie", cookies);    //设置cookie
        if (ref != null && !ref.equals("")) {
            httpPost.setHeader("Referer", ref);//如果有地址引用则设置
        }
        httpPost.setEntity(entity);//设置请求参数
        CloseableHttpResponse response = null;//提交请求
        try {
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            strEntity = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strEntity;
    }

    public String getPublicKey(String html) {
        String div = null;
        String regex = "BEGIN PUBLIC KEY([\\S\\s]+?)END PUBLIC KEY";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(html);
        while (match.find()) {
            div = match.group();
            int start = div.indexOf("KEY-----");
            int end = div.lastIndexOf("-----END PUBLIC KEY");
            div = div.substring(start + 8 , end);
        }
        return div;
    }



}
