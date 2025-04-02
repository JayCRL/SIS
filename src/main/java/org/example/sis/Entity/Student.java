package org.example.sis.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Student extends  UserEntity{
    //学号
    private Long sid;
    //性别
    private int sex;
    //电话号码
    private Long phonenumber;
    public Student(Long sid, String userName, String password,int sex, Long phonenumber) {
        super(1, userName, password);
        this.sid=sid;
        this.sex=sex;
        this.phonenumber=phonenumber;
    }
    public void setInformation(Long phonenumber){
        this.phonenumber=phonenumber;
    }
    public Long getSid(){
        return sid;
    }
    public  int getSex(){
        return sex;
    }
    public Long getPhoneNumber(){
        return  phonenumber;
    }

    @Override
    public String toString() {
        return "Student{" +
                "sid=" + sid +
                ", sex=" + sex +
                ", phonenumber=" + phonenumber +
                "} " + super.toString();
    }
    public static Student fromString(String str) {

        Pattern pattern = Pattern.compile(
                "Student\\{sid=([0-9]+), sex=([0-9]+), phonenumber=([0-9]+)\\} " +
                        "UserEntity\\{Identity=([0-9]+), userName='([^']*)'\\}"
        );
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid string format");
        }

        try {
            // 提取 Student 部分的字段
            long sid = Long.parseLong(matcher.group(1));
            int sex = Integer.parseInt(matcher.group(2));
            long phonenumber = Long.parseLong(matcher.group(3));

            // 提取 UserEntity 部分的字段并验证 id 必须为 1
            int userId = Integer.parseInt(matcher.group(4));
            if (userId != 1) {
                throw new IllegalArgumentException("UserEntity id must be 1");
            }
            String userName = matcher.group(5);

            // 调用构造函数创建 Student 对象
            return new Student(sid, userName, null, sex, phonenumber);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse numeric values", e);
        }
    }

}
