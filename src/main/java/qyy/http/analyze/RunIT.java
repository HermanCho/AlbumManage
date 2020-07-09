package qyy.http.analyze;

import java.util.ArrayList;

public class RunIT {
    public static ConnectJWGL connectJWGL = null;

    String errorMessage = null;

    public RunIT() {
        connectJWGL = new ConnectJWGL();  //已包含初始化
    }

//    public static void update(){
//        connectJWGL.init();
//        System.out.println(connectJWGL);
//    }

    public boolean loadCheckcode() {
        if (connectJWGL.downloadCheckcode()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean run(Student student) {
        errorMessage = connectJWGL.login(student.getUserName(), student.getPassword(), student.getCheckCode());
        if (errorMessage == null) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String[]> getCourse() {
        return (connectJWGL.getSchoolTimetable());
    }

    public String getErrorReason() {
        return errorMessage;
    }
}
