package org.example.sis.Entity;

import java.util.ArrayList;

public class ClassRoom {
    //教室名
    Long Cid;
    //教室名
    String ClassName;
    //容量
    int volume;
    public ClassRoom(Long Cid, String ClassName, int volume){
     this.Cid=Cid;
     this.ClassName=ClassName;
     this.volume=volume;
    }
    public  ClassRoom(){

    }
    public  int getVolume(){
        return volume;
    }
    public Long getCid() {
        return Cid;
    }

    public void setCid(Long cid) {
        Cid = cid;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getClassName() {
        return ClassName;
    }
    @Override
    public  String toString() {
        return "ClassRoom{ ClassID="+getCid()+",ClassName="+getClassName()+",Volume="+volume+"}";
    }
    public static ClassRoom PARSE(String str){
      ClassRoom classRoom=  new ClassRoom();
        str=str.substring(11,str.length()-1);
        String[] results=str.split(",");
        for(String result:results){
            String[] keyValue = result.split("=");
            switch (keyValue[0]){
                case "ClassID":
                   classRoom.setCid(Long.parseLong(keyValue[1]));
                   break;
                case "ClassName":
                    classRoom.setClassName(keyValue[1]);
                    break;
                case "Volume":
                    classRoom.setVolume(Integer.parseInt(keyValue[1]));
                    break;
            }
        }
        return classRoom;
    }
   public static ArrayList<ClassRoom> getClassRoom(String str){
        ArrayList<ClassRoom> list=new ArrayList<>();
        str=str.substring(0,str.length()-1);
        String[] classRooms=str.split("}, ");
        for(String string:classRooms){
            String[] results=string.substring(11).split(",");
            ClassRoom classRoom=new ClassRoom();
            for(String keyValue: results){
                String[] res=keyValue.split("=");
                String key=res[0];
                String value=res[1];
                switch (key){
                    case " ClassID":
                        classRoom.setCid(Long.parseLong(value));
                        break;
                    case "ClassID":
                        classRoom.setCid(Long.parseLong(value));
                        break;
                    case "ClassName":
                        classRoom.setClassName(value);
                        break;
                    case "Volume":
                        classRoom.setVolume(Integer.parseInt(value));
                        break;
                }
            }
            list.add(classRoom);
        }
return list;
    }
}
