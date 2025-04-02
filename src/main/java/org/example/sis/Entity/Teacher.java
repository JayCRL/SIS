package org.example.sis.Entity;


import java.util.ArrayList;

public class Teacher extends UserEntity{
    private Long tid;
    private int sex;
    //电话号码
    private Long phonenumber;
    public Long getTid(){
        return tid;
    }
    public int getSex() {
        return sex;
    }
    public Long getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(Long phonenumber) {
        this.phonenumber = phonenumber;
    }
    public Teacher(Long tid,  String userName, String password, int sex,Long phonenumber) {
        super(2, userName, password);
        this.tid =tid;
        this.sex = sex;
        this.phonenumber = phonenumber;
    }
    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Teacher() {
        super();
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "tid=" + tid +
                ", username="+super.getUserName()
                +", sex=" + sex +
                ", phonenumber=" + phonenumber +
                '}';
    }
    //字符解析教师信息只提供工号和名字
    public static Teacher Parse(String txt) {
        txt=txt.substring(8,txt.length()-1);
        String[] arr=txt.split(", ");
        Teacher teacher=new Teacher();
        for(String message: arr){
            String[] result=message.split("=",2);
            String key=result[0];
            String value=result[1];
            switch (key) {
                case "tid":
                    teacher.setTid(Long.parseLong(value));
                    break;
                case "username":
                    teacher.setUsername(value);
                    break;
                case "phonenumber":
                    teacher.setPhonenumber(Long.parseLong(value));
                    break;
            }
        }
        return teacher;
    }

    public ArrayList<Teacher> ParseTeachers(String message) {
        message.substring(7, message.length() -2);
        String[] messages=message.split(", ");
        ArrayList<Teacher> teachers=new ArrayList<>();
        for(String txt: messages){
            teachers.add(Parse(txt));
        }
        return teachers;
    }
}
