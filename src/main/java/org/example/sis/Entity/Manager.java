package org.example.sis.Entity;

public class Manager extends  UserEntity{
   private Long mid;
    public Manager(Long mid,String userName, String password) {
        super(3, userName, password);
        this.mid = mid;
    }
    public void setMid(Long mid) {
        this.mid = mid;
    }
    public Manager(Long mid){
        this.mid = mid;
    }
    public  Long getMid(){
        return mid;
    }
}
