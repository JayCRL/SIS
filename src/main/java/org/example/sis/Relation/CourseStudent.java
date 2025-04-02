package org.example.sis.Relation;

public class CourseStudent {
    //表单号
    private  Long fid;
    //课程号
    private  Long cid;
    //学生号
    private  Long sid;
    private  int logicDelete;
   public CourseStudent(Long fid,Long cid,Long sid,int logic){
        this.cid=cid;
        this.sid=sid;
        this.fid=fid;
        this.logicDelete=logic;
   }
//get方法
    public Long getCid() {
        return cid;
    }

    public int getLogicDelete() {
        return logicDelete;
    }

    public Long getFid() {
        return fid;
    }


    public Long getSid() {
        return sid;
    }
}
