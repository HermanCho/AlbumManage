package qyy.http.analyze;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import qyy.http.analyze.constant.Constant;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectJWGL {
    private String userName;
    private String stuName;
    private String __VIEWSTATE = "";
    private String origin__VIEWSTATE = "";
    CloseableHttpClient httpClient = null;
    private String mErrorMessege;

    public String get__VIEWSTATE() {
        return __VIEWSTATE;
    }

    public Connection getConnection() {
        return connection;
    }

    private String cookies = null;
    private Connection connection;
    private Document document;

    public ConnectJWGL() {
        init();
    }

    public void init(){
        connectIndex();
        downloadCheckcode();
    }

    //首次连接获取cookies和__VIEWSTATE
    public void connectIndex() {
        String url = Constant.BASE_URL;
        httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            cookies = response.getFirstHeader("Set-Cookie").getValue();
            String html = sendGetRequest(url, null);    //发送访问请求并获得响应页面
            __VIEWSTATE = findViewState(html);
            origin__VIEWSTATE = __VIEWSTATE;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String findViewState(String html) {
        String res = "";
        String pattern = "<input type=\"hidden\" name=\"__VIEWSTATE\" value=\"(.*?)\" />";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(html);
        if (m.find()) {
            res = m.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
        }
        return res;
    }

    public static String findStuName(String html) {
        String res = "";
        String pattern = "<span id=\"xhxm\">(.*?)同学</span>";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(html);
        if (m.find()) {
            res = m.group();
            res = res.substring(res.indexOf("xhxm") + 6, res.lastIndexOf("<") - 2);
        }
        return res;
    }

    public boolean downloadCheckcode() {
        try {
            String captcha_url = "http://202.116.160.170/CheckCode.aspx";
            HttpGet httpGet = new HttpGet(captcha_url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            // 临时目录
            String tempPath =System.getProperty("java.io.tmpdir") + "images";
            File file = new File(tempPath);
            if (file.exists()) {
                file.delete();
            }
            else {
                file.mkdir();
            }
            file = new File(tempPath + "/pic.png");
            file.createNewFile();
            OutputStream output = new FileOutputStream(file);
            BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(bytes);
            bufferedOutput.close();
            output.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //登录
    public String login(String userName, String password, String checkCode) {
        __VIEWSTATE = origin__VIEWSTATE;  //保证viewstate正常
        this.userName = userName;
        stuName = "";
        String html = null;
        String loginHtml = null;
        String errorMessage = null;
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));//__VIEWSTATE，不可缺少这个参数
        params.add(new BasicNameValuePair("txtUserName", userName));//学号
        params.add(new BasicNameValuePair("TextBox2", password));//密码
        params.add(new BasicNameValuePair("txtSecretCode", checkCode));//验证码
        params.add(new BasicNameValuePair("RadioButtonList1", Constant.RADIO_BUTTON_LIST));//登陆用户类型
        params.add(new BasicNameValuePair("Button1", ""));
        params.add(new BasicNameValuePair("lbLanguage", ""));
        params.add(new BasicNameValuePair("hidPdrs", ""));
        params.add(new BasicNameValuePair("hidsc", ""));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Constant.ENCODING);
            html = sendPostRequest(Constant.LOGIN_URL, null, entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (html.contains("alert")) {
            errorMessage = getErrorMessege(html);
        }else{
            loginHtml = sendGetRequest(Constant.STUDENT_URL + userName, null);
            if(loginHtml.contains("欢迎您")){
                stuName = findStuName(loginHtml);
            }else {
                errorMessage = "登录失败,请再次尝试";
            }
            __VIEWSTATE = origin__VIEWSTATE;
        }
        return errorMessage;
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

    public String getErrorMessege(String html) {
        String div = null;
        String regex = "alert.*?;";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(html);
        while (match.find()) {
            div = match.group();
            int start = div.indexOf("'");
            int end = div.lastIndexOf("'");
            div = div.substring(start + 1, end);
        }
        return div;
    }

    public ArrayList<String[]> getSchoolTimetable() {
        ArrayList<String[]> courseList = null;
        try {
            String url = "http://202.116.160.170/xskbcx.aspx?xh=" + userName + "&xm=" + URLEncoder.encode(stuName, "GB2312") + "&gnmkdm=N121603";
            String referer = Constant.STUDENT_URL + userName;//引用地址
            String resHtml = sendGetRequest(url, referer);
            __VIEWSTATE = findViewState(resHtml);//记录__VIEWSTATE
            String courseHtml = findCourseTableHtml(resHtml);
            if (courseHtml != null && !courseHtml.equals("")) {
                courseList = getCourseList(courseHtml);

            } else {
                System.out.println("courseHtml是空的" + courseHtml);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return courseList;
    }

    private static String findCourseTableHtml(String html) {
        String res = "";
        String tar = "<table id=\"Table1\" class=\"blacktab\" bordercolor=\"Black\" border=\"0\" width=\"100%\">";
        String pattern = "<table id=\"Table1\" class=\"blacktab\" bordercolor=\"Black\" border=\"0\" width=\"100%\">([\\S\\s]+?)</table>";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(html);
        if (m.find()) {
            res = m.group(0);
            res = res.substring(res.indexOf(tar) + tar.length(), res.lastIndexOf("</table>")).trim();
        }
        return res;
    }

    public ArrayList<String[]> getCourseList(String courseTableHtml) {
        //*、+会尽可能多的匹配文字，在它们的后面加上 一个?就可以实现最小匹配。
        ArrayList<String[]> course = new ArrayList<>();//按星期一到星期五分，每个里面又是一个string数组，2节一划分，6节课
        String regex0 = "<tr>[\\s\\S]*?</tr>";
        String regex1 = "<td[\\s\\S]*?</td>";
        String regex2 = "[\\u4e00-\\u9fa5]+[^a-z<>]*[\\u4e00-\\u9fa5]*[0-9}]?";
        //        String regex2 = "[\\u4e00-\\u9fa5]+[^a-z<>]*[\\u4e00-\\u9fa5]*[周}]?";
        Pattern pattern0 = Pattern.compile(regex0);
        Matcher match = pattern0.matcher(courseTableHtml);
        while (match.find()) {
            String div = match.group();
            boolean start = false;
            String[] lesson = new String[8];
            int i = 0;
            Pattern pattern1 = Pattern.compile(regex1);
            Matcher matcherBig = pattern1.matcher(div);
            while (matcherBig.find()) {
                String big = matcherBig.group();
                String whole = "";
                int count = 0;
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcherSmall = pattern2.matcher(big);
                while (matcherSmall.find()) {
                    String tmp = matcherSmall.group();
                    if (start) {
                        count++;
                    }
                    if (Pattern.matches("第[13579]1?节", tmp)) {
                        start = true;
                    }
                    if (count == 1 || count == 2) {
                        whole += tmp;
                        if (count == 1) {
                            whole += "\n";
                        }
                    }
                }
                if (start) {
                    lesson[i++] = whole;
                }
            }
            if (start) {
                course.add(lesson);
            }
        }
        return course;
    }
}
