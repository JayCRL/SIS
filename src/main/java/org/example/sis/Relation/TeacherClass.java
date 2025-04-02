package org.example.sis.Relation;

import org.example.sis.Entity.Teacher;

public class TeacherClass {
    private Long fid;
    private Long tid;
    private Long cid;
    public TeacherClass(Long fid, Long tid, Long cid){
        this.fid = fid;
        this.tid = tid;
        this.cid = cid;
    }
    public TeacherClass(){}
    public Long getFid(){
        return fid;
    }
    public Long getTid(){
        return tid;
    }
    public Long getCid(){
        return cid;
    }
    @Override
    public String toString(){
        return "TeacherClass{" +
                "fid=" + fid +
                ", tid=" + tid +
                ", cid=" + cid +
                '}';
    }
    public static  TeacherClass ParseTeacher(String str){
        TeacherClass t = new TeacherClass();
        str=str.substring(13, str.length()-2);
        String[] arr = str.split(", ");
        for(String msg:arr){
            String[] msgArr = msg.split("=",2);
           String key=msgArr[0];
           String value=msgArr[1];
          switch (key){
              case "fid":
                  t.fid = Long.parseLong(value);
                  break;
              case "tid":
                  t.tid = Long.parseLong(value);
                  break;
              case "cid":
                  t.cid = Long.parseLong(value);
                  break;
          }
        }
        return t;
    }
}
