package qyy.controller;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import qyy.connetYB.ConnectYB;
import qyy.http.analyze.RunIT;
import qyy.http.analyze.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




@RestController
public class WholeController {

    RunIT runIT = null;

    public WholeController() {
        runIT = new RunIT();
    }

    @RequestMapping(value = "/hello")
    public String hello() {
        return "aaaaaaa";
    }

    @RequestMapping(value = "/yblogin")
    private Map<String, Object> getLoginInfo(String user , String password) {
        System.out.println("user:"+user);
        System.out.println("password:"+password);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ConnectYB connectYB = new ConnectYB();
        connectYB.connectIndex();
        boolean suc = connectYB.login(user,password);
        modelMap.put("success", suc);
        return modelMap;
    }


    @RequestMapping("/reload")
    private void reload() {
        runIT = new RunIT();
        runIT.loadCheckcode();
        System.out.println("reload了");
    }

    @RequestMapping("/refresh")
    private Map<String, Object> refreshCheckcode() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("success", runIT.loadCheckcode());
        return modelMap;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    private Map<String, Object> getLoginInfo(@RequestBody Student student) {
        System.out.println(student.toString());
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("success", runIT.run(student));
        modelMap.put("failMessage", runIT.getErrorReason());
        System.out.println("failMessage" + runIT.getErrorReason());
        return modelMap;
    }

    @RequestMapping(value = "/login/getcourse", method = RequestMethod.POST)
    private Map<String, Object> getCourse() {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        ArrayList<String[]> courseList = runIT.getCourse();
        boolean suc = true;
        if (courseList == null) {
            suc = false;
        } else {
            String[] lesson = {"1-2节", "3-4节", "5-6节", "7-8节", "9-10节", "11-13节"};
            for (int i = 0; i < courseList.size(); i++) {
                String[] tmp = courseList.get(i);
                tmp[0] = lesson[i];
            }
        }
        modelMap.put("success", suc);
        modelMap.put("course", courseList);
        return modelMap;
    }

    /**
     *  fa8f64dba7c2efd79c5d429634f0e8072cd1dab0
     *  b9e0c75d9030f6f245107a0c57d45e28957170c4
     */
    String getAut = null;

    @RequestMapping("/YiBanDemo/content")
    public String yb(String code,String state){
        String message = "请通过授权页面打开本页面，直接打开授权无效";
        //tempCode == null ,即直接打开
        //但tempCode！=null也不一定就是，因为可以通过？code来伪造登录状态，不过这种方法获得的后续也无法调用易班窗口
        if(code != null){
            message = "已通过授权，授权有效时间为300s，请尽快回到小程序更新状态";
            getAut = code;
        }
        return message;
    }


    @RequestMapping("/orc2/{url}")
    public String test(@PathVariable("url") String url) throws JSONException {
        String getCorrectURL = url;
        getCorrectURL = getCorrectURL.replaceAll("&","/");

        String path = getCorrectURL;
        //设置APPID/AK/SK
        String APP_ID = "17026928";
        String API_KEY = "xZ0NurcUKxGjHdDy0pSyMv13";
        String SECRET_KEY = "AYgk8ZkYSxGh4FpouBZGGn5CDg5pOZxp";
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        // 通用文字识别, 图片参数为远程url图片
        JSONObject res = client.basicGeneralUrl(path, options);
        //System.out.println(res.toString(2));
        JSONArray myJson = res.getJSONArray("words_result");
        String outputWords = "";
//        Iterator<Object> iterator = myJson.iterator();
        Iterator<Object> iterator = myJson.iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            JSONObject obj = new JSONObject(value.toString());
//            System.out.println(obj.get("words"));
            outputWords = outputWords+obj.get("words")+"\n";
        }
        System.out.println(outputWords);
        return outputWords;

    }


}
