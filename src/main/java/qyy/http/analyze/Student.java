package qyy.http.analyze;

public class Student {
    private String userName;
    private String password;

    public Student() {
    }

    public Student(String userName, String password, String checkCode) {
        this.userName = userName;
        this.password = password;
        this.checkCode = checkCode;
    }

    @Override
    public String toString() {
        return "Student{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", checkCode='" + checkCode + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    private String checkCode;


}
