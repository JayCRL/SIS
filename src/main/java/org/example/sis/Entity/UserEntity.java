package org.example.sis.Entity;

public class UserEntity {
    //身份
    private int Identity;
    //用户名
    private String userName;
    //密码
    private String password;

    public UserEntity() {

    }

    public void  setUsername(String username){
        this.userName = username;
    }

    UserEntity(int Identity,String userName, String password){
        this.Identity = Identity;
        this.userName = userName;
        this.password = password;
    }
    public  String getUserName(){
        return  userName;
    }
    public String getPassword(){
        return password;
    }
    @Override
    public String toString(){
        return "UserEntity{" +
                "Identity=" + Identity +
                ", userName='" + userName + '\'' +
                '}';
    }
}
