package org.example.sis.Entity;

import java.util.ArrayList;

public class Comment {
    private  Long fid;
    private  Long cid;
    private  Long sid;
    private Long tid;
    private int score;
    private String text;
    private int logicDelete;
    public Comment(Long fid,Long cid,Long sid,Long tid,int score,String text,int logicDelete){
        this.fid=fid;
        this.cid=cid;
        this.sid=sid;
        this.tid=tid;
        this.score=score;
        this.text=text;
        this.logicDelete=logicDelete;
    }
    public Long getFid(){
        return fid;
    }
    public Comment() {
    }
    public void setText(String text){
        this.text=text;
    }
    public void setLogicDelete(int logicDelete){
        this.logicDelete=logicDelete;
    }
    public void setScore(int score){
        this.score=score;
    }

    public Long getTid(){
        return tid;
    }

    @Override
    public String toString() {
        return "Comment{"+"fid="+fid+",cid="+cid+",sid="+sid+",tid="+tid+",score="+score+",text="+text+",logicDelete="+logicDelete+"}";
    }
    public static Comment parseComment(String comment){
        Comment c = new Comment();
        //解析数据
        comment=comment.substring(8,comment.length()-1);
        String[] parts = comment.split(",");
        for(String keyValue:parts){
            String[] result=keyValue.split("=",2);
            String key=result[0];
            String value=result[1];
            switch (key){
                case "fid":
                    c.fid=Long.parseLong(value);
                    break;
                case "cid":
                    c.cid=Long.parseLong(value);
                    break;
                case "sid":
                    c.sid=Long.parseLong(value);
                    break;
                case "tid":
                    c.tid=Long.parseLong(value);
                    break;
                case "score":
                    c.score=Integer.parseInt(value);
                    break;
                case "text":
                    c.text=value;
                    break;
                case "logicDelete":
                    c.logicDelete=Integer.parseInt(value);
                    break;
            }
        }
        return c;
    }
    //解析评论
    public static ArrayList<Comment> parseComments(String comments){
        comments=comments.substring(1, comments.length()-1);
        String[] text=comments.split(", ");
        ArrayList<Comment> co=new ArrayList<Comment>();
        for(String t : text){
            co.add(parseComment(t));
        }
        return co;
    }
    public String getText() {
        return text;
    }

    public int getScore() {
        return score;
    }

    public Long getSid() {
        return sid;
    }

    public Long getCid() {
        return cid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public int getLogicDelete() {
        return logicDelete;
    }
}
