package org.example.sis.Relation;

import org.example.sis.Entity.Course;

public class CourseClassroom {
    private Long fid;
    private Long Cid;
    private Long classroomid;
    public CourseClassroom(Long fid, Long cid, Long classroomid){
        this.Cid=cid;
        this.classroomid=classroomid;
        this.fid=fid;
    }
    public  CourseClassroom(){

    }
    Long getFid(){
        return  fid;
    }
    public Long getCid(){
        return Cid;
    }
  public   Long getClassroomid(){
        return classroomid;
    }

}
