package org.example.sis;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sis.Entity.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Service {
    public  static  void  main(String[]args) throws IOException {
        //导入配置 初始化数据库 开启连接
        DBMS dbms = new DBMS();
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("src/main/resources/package.json");
        PackageConfig packageConfig = objectMapper.readValue(jsonFile, PackageConfig.class);
        int port = packageConfig.getPort();
        System.out.println("Port: " + port);
        ServerSocket serversocket=new ServerSocket(port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("Waiting for client connection...");
                        Socket socket=serversocket.accept();
                        System.out.println("Client connection established");
                        //读入读出流
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(), true);
                        String messageLine;
                        try {
                            //若没退出一直连接
                            while ((messageLine = reader.readLine()) != null) {
                                System.out.println(messageLine);
                                if(messageLine.equals("log in")){
                                    String message=reader.readLine();//读取下一行数据
                                    //数据解析
                                    int mode= Integer.parseInt((message.substring(message.indexOf("mode:")+"mode:".length())));
                                    message=reader.readLine();//读取下一行数据
                                    String username=message.substring(message.indexOf("username:")+"username:".length());
                                    message=reader.readLine();//读取下一行数据
                                    String password=message.substring(message.indexOf("password:")+"password:".length());
                                   String result=dbms.CheckPassWord(mode,username, password);
                                   //如果登录成功
                                   if(result.equals("Login Successfully")){
                                       /*
                                       * 1:Teacher
                                       * 2.Student
                                       * 3.Manager
                                       * */
                                       switch (mode) {
                                           case 2:
                                               printWriter.println("success");
                                               printWriter.println("Get Teacher Successfully");
                                               Teacher teacher=dbms.getTeacher(username);
                                               printWriter.println(teacher.toString());
                                               printWriter.println("ok");
                                               break;
                                           case 1:
                                               printWriter.println("success");
                                               printWriter.println("Get Student Successfully");
                                               Student student=dbms.getStudent(username);
                                               printWriter.println(student.toString());
                                               printWriter.println("ok");
                                               break;
                                           case 3:
                                               printWriter.println("success");
                                               printWriter.println("Get Manager Successfully");
                                               Manager manager=dbms.getManager(username);
                                               printWriter.println(manager.getMid());
                                               printWriter.println("ok");
                                               break;
                                       }
                                   }else{
                                       //发送报错信息
                                       printWriter.println(result);
                                   }
                                }
                                //客户端发信息退出
                                if(messageLine.equals("UpdataSlefInformation")){
                                    String message=reader.readLine();//读取下一行数据
                                    dbms.UpDataStudent(Student.fromString(message));
                                }
                                //断开连接
                                if (messageLine.equals("exit")) {
                                    break;
                                }
                                if(messageLine.equals("GetClassRoomByCid")){
                                    Long courseId=Long.parseLong(reader.readLine());
                                    printWriter.println( dbms.getClassRoomByCId(courseId).getClassName());
                                }
                                if(messageLine.equals("GetCourses")){
                                    String message=reader.readLine();//读取下一行数据
                                    printWriter.println(dbms.getCourseList(Long.parseLong(message)).toString());
                                }
                                if(messageLine.equals("GET COURSES")){
                                    Long mid=Long.parseLong(reader.readLine());
                                    printWriter.println(dbms.getCourseByMid(mid).toString());
                                }
                                if(messageLine.equals("GET CLASSROOM")){
                                         Long mid=Long.parseLong(reader.readLine());
                                         printWriter.println(dbms.getClassroomByMid(mid).toString());
                                }
                                if(messageLine.equals("GET Teachers")){
                                    Long mid=Long.parseLong(reader.readLine());
                                    printWriter.println(dbms.getTeacherByMid(mid).toString());

                                }
                                if(messageLine.equals("GET Students")){
                                    Long mid=Long.parseLong(reader.readLine());
                                    printWriter.println(dbms.getStudentByMid(mid).toString());

                                }
                                //断开连接
                                if (messageLine.equals("exit")) {
                                    break;
                                }
                                if(messageLine.equals("GetMyCourseByTeacherID")){
                                    Long TeacgerId=Long.parseLong(reader.readLine());//读取下一行数据
                                    printWriter.println(dbms.getCourseByTid(TeacgerId).toString());
                                }
                                if(messageLine.equals("GetCourseCommentsById")){
                                    String message=reader.readLine();
                                    printWriter.println( dbms.getCommentsNameseByCid(Long.parseLong(message)).toString());

                                }
                                if(messageLine.equals("getTeacherCourse")){
                                    String message=reader.readLine();//读取下一行数据
                                  ArrayList<Course> arrayList= dbms.GetCourseByTid(Long.parseLong(message));
                                    printWriter.println(arrayList.toString());
                                }
                                //请求获取老师数据
                                if(messageLine.equals("GetTeacher")){
                                    String message=reader.readLine();
                                        printWriter.println( dbms.getTeacherById(Long.parseLong(message)).toString());
                                }
                                if(messageLine.equals("getStudents")) {
                                    String message = reader.readLine();
                                    try {
                                        printWriter.println(dbms.getStudentByCId(Long.parseLong(message)).toString());
                                    }catch (NumberFormatException e){
                                        System.out.println("获取数据失败");
                                        printWriter.println("Wrong number of students");
                                    }
                                }
                                if(messageLine.equals("GetComment")){
                                    String message=reader.readLine();
                                    printWriter.println( dbms.getCommentById( Long.parseLong(message)).toString());
                                }
                                if(messageLine.equals("GetCourse")){
                                    String message=reader.readLine();
                                    printWriter.println( dbms.getCourseById(Long.parseLong(message)).toString());

                                }
                                //更新评论
                                if(messageLine.equals("updateComment")){
                                    String message=reader.readLine();
                                    Comment comment=Comment.parseComment(message);
                                    dbms.UpdataComment(comment);
                                }
                                if(messageLine.equals("updateStudent")){
                                    String message=reader.readLine();
                                    System.out.println(message);
                                    Student student=Student.fromString(message);
                                    dbms.UpDataStudent(student);
                                }
                                if(messageLine.equals("updateTeacher")){
                                    String message=reader.readLine();
                                    System.out.println(message);
                                    Teacher teacher=Teacher.Parse(message);
                                    dbms.UpdataTeacher(teacher);
                                }
                                if(messageLine.equals("updateClassroom")){
                                    ClassRoom room=ClassRoom.PARSE(reader.readLine());
                                    dbms.UpdataClassRoom(room);
                                }
                                if(messageLine.equals("updateCourse")){
                                    String message=reader.readLine();
                                    System.out.println(message);
                                    Course course=Course.parseCourse(message);
                                    dbms.UpdataCourse(course);
                                }

                                if(messageLine.equals("GetComments")){
                                    String message=reader.readLine();
                                    System.out.println(message);
                                    Long sid=Long.parseLong(message);
                                    //通过学生号课程号获取信息
                                    //通过评论信息展示
                                    ArrayList<Comment> commentArrayList =dbms.getCommentsBySId(sid);
                                    printWriter.println(commentArrayList.toString());
                                }
                            }
                        }catch (SocketException e){
                            System.out.println("客户端断开连接");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("Service is running");
    }
}
