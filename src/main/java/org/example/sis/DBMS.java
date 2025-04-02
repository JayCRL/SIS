package org.example.sis;
import org.example.sis.Entity.*;
import org.example.sis.Relation.CourseClassroom;
import org.example.sis.Relation.CourseStudent;
import org.example.sis.Relation.TeacherClass;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
public class DBMS {
    private static final long MOD_VALUE = 202313201000L; // 2e11
    private HashMap<Integer,Student> students;
    private HashMap<Long,Teacher> teachers;
    private HashMap<Integer, Manager> managers;
    private HashMap<Long,Course> couses;
    private HashMap<Long,Comment> commentHashMap;
    private HashMap<Long, CourseClassroom> courseClassroomHashMap;
    //选课
    private HashMap<Long, CourseStudent> chooseClassHashMap;
    private HashMap<Long, TeacherClass> teacherClassHashMap;
    private HashMap<Integer,ClassRoom> classRooms;
    private String filePaths="src/main/resources/";
    void CleanFilePath(){
        filePaths="src/main/resources/";
    }
    public ArrayList<Course> GetCourseByTid(Long tid){
        ArrayList<Course> courses=new ArrayList<>();
        for (TeacherClass teacherClass: teacherClassHashMap.values()){
            if(teacherClass.getTid().equals(tid)){
                courses.add(couses.get(teacherClass.getCid()));
            }
        }
        return courses;
    }
    DBMS() throws IOException {
        init();
    }
    void init() throws IOException {
        InitStudent("User_Students.txt");
        InitTeacher("User_Teachers.txt");
        InitCourse("Courses");
        InitClassRoom("ClassRooms");
        InitManager("Managers");
        InitChooseClass("CourseStudent");
        InitComments("Comments");
//        InitCourseStudent("CourseStudent");
        InitTeacherClass("TeachClass");
        InitCourseClassroom("CourseClassRoom");
    }
    //核验信息
    String CheckPassWord(int identity,String username,String password){
        Integer integer=(int)(Long.parseLong(username)%MOD_VALUE);
        switch(identity){
            //老师
            case 1:
                Student student=students.get(integer);
                if(student!=null && student.getPassword().equals(password)){
                    System.out.println(200);
                    return "Login Successfully";
                } else{
                    System.out.println(300);
                  return "Password Error";
                }
            case 2:
                Teacher teacher=teachers.get(Long.parseLong(username));
                if(teacher!=null && teacher.getPassword().equals(password)){
                    System.out.println(200);
                    return  "Login Successfully";
                }else{
                    System.out.println(300);
                    return "Password Error";
                }
            //管理员
            case 3:
                Manager manager=managers.get(integer);
                if(manager!=null && manager.getPassword().equals(password)){
                    System.out.println(200);
                    return "Login Successfully";
                } else{
                    System.out.println(300);
                    return "Password Error";
                }
            default:
                System.out.println(400);
                return "Unknown Identity";
        }
    }
    void InitCourseClassroom(String form) throws IOException {
        courseClassroomHashMap=new HashMap<>();
        filePaths+=form;
        BufferedReader reader=new BufferedReader(new FileReader(filePaths));
        String line;
        while((line=reader.readLine()) != null){
            String[] data = line.split(",");
            Long fid=Long.parseLong(data[0].trim());
            Long cid = Long.parseLong(data[1].trim());
            Long classroom = Long.parseLong(data[2].trim());
            CourseClassroom courseClassroom=new CourseClassroom(fid,cid,classroom);
            courseClassroomHashMap.put(fid,courseClassroom);
        }
        System.out.println("CourseClassroomHashMap Successfully Inited The number of courseClassroomHashMap:"+courseClassroomHashMap.size());
        CleanFilePath();
    }
    void InitTeacherClass(String form) throws IOException {
        teacherClassHashMap = new HashMap<>();
        filePaths+=form;
        BufferedReader reader = new BufferedReader(new FileReader(filePaths));
        String line;
        while((line=reader.readLine()) != null){
            String[] data = line.split(",");
            Long fid=Long.parseLong(data[0].trim());
            Long tid = Long.parseLong(data[1].trim());
            Long cid = Long.parseLong(data[2].trim());
            TeacherClass td=new TeacherClass(fid,cid,tid);
            teacherClassHashMap.put(fid,td);
        }
        System.out.println("TeacherClass Successfully Inited The number of teacherClass:"+teacherClassHashMap.size());
        CleanFilePath();
    }

    void InitStudent(String form) throws IOException {
     students = new HashMap<>();
     filePaths +=form;
     BufferedReader reader = new BufferedReader(new FileReader(filePaths));
     String line;
     while((line=reader.readLine()) != null){
         String[] data = line.split(",");
         /*
    public Student(Long Sid, String userName, String password,int sex, Long phonenumber) {
         * */
         Long sid = Long.parseLong(data[0].trim());
         String userName = data[1].trim();
         String password = data[2].trim();
         int sex = Integer.parseInt(data[3].trim()); // 正确转换String到int
         Long phoneNumber = Long.parseLong(data[4].trim());
         // 使用Long作为键避免哈希冲突
         students.put((int)(sid%MOD_VALUE), new Student(sid, userName, password, sex, phoneNumber));
     }
     System.out.println("Students Successfully Inited The number of students:"+students.size());
     CleanFilePath();
    }
    void InitManager(String form) throws IOException {
        managers=new HashMap<>();
        filePaths+=form;
        BufferedReader reader = new BufferedReader(new FileReader(filePaths));
        String line;
        while((line=reader.readLine()) != null){
            String[] data = line.split(",");
            Long mid = Long.parseLong(data[0].trim());
            /*
               public Manager(Long mid,String userName, String password) {
             * */
            managers.put((int)(mid%MOD_VALUE), new Manager(mid,data[1], data[2]));
        }
        System.out.println("Manager Successfully Inited The number of students:" + managers.size());
        CleanFilePath();
    }
    ArrayList<Course> getCourseByTid(Long tid) throws IOException {
        ArrayList<Course> courses=new ArrayList<>();
        for (TeacherClass teacherClass: teacherClassHashMap.values()){
            if(teacherClass.getTid().equals(tid)){
                courses.add(couses.get(teacherClass.getCid()));
            }
        }
        return courses;
    }
    ArrayList<String> getCommentsNameseByCid(Long cid) throws IOException {
        ArrayList<String> names=new ArrayList<>();
        for (Comment comment: commentHashMap.values()){
            if(comment.getCid().equals(cid)){
                names.add(comment.getText());
            }
        }
        return names;
    }
    void UpDataStudent(Student student) throws IOException {
        if(students.get((int)(student.getSid()%MOD_VALUE))==null){
            students.put((int)(student.getSid()%MOD_VALUE), student);
        }else {
            students.putIfAbsent((int) (student.getSid() % MOD_VALUE), student);
            Student stu = getStudentById((int) (student.getSid() % MOD_VALUE));
            stu.setInformation(student.getPhoneNumber());
            students.replace((int) (Long.parseLong(student.getSid() + "".trim()) % MOD_VALUE), stu);
        }
        LoadStudent();
    }
    void LoadStudent() throws IOException {
        String targetFilePath = filePaths + "User_Students.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
        for (Integer key :students.keySet()) {
            Student student1=students.get(key);
            if (student1 != null) { // 仅保存存在的学生数据（无空行）
                // 2. 生成与InitStudent解析格式完全一致的CSV行
                String line = String.format("%d,%s,%s,%d,%d",
                        student1.getSid(),
                        student1.getUserName(),
                        student1.getPassword(),
                        student1.getSex(),
                        student1.getPhoneNumber());
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();
        System.out.println("Students Saved. Total: " + students.size());
        CleanFilePath(); // 清理路径（与InitStudent逻辑一致）
    }
    void UpdataComment(Comment comment) throws IOException {
        Comment com=getCommentById(comment.getFid());
        com.setScore(comment.getScore());
        com.setText(comment.getText());
        commentHashMap.replace(comment.getFid(),com);
        String targetFilePath = filePaths + "Comments";
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
        for (Long key : commentHashMap.keySet()) {
            Comment comment1=commentHashMap.get(key);
                // 2. 生成与InitComment解析格式完全一致的CSV行
                String line = String.format("%d,%d,%d,%d,%d,%s,%d",
                        comment1.getFid(),
                        comment1.getSid(),
                        comment1.getCid(),
                        comment1.getTid(),
                        comment1.getScore(),
                        comment1.getText(),
                        comment1.getLogicDelete());
                writer.write(line);
                writer.newLine();
        }
        writer.close();
        System.out.println("Comments Saved. Total: " + students.size());
        CleanFilePath();
    }

    void InitTeacher(String form) throws IOException {
    teachers = new HashMap<>();
    filePaths+=form;
    BufferedReader reader = new BufferedReader(new FileReader(filePaths));
    String line;
    while((line=reader.readLine()) != null){
        String[] data = line.split(",");
        Long tid = Long.parseLong(data[0]);
        int sex = Integer.parseInt(data[3]);
        teachers.put(tid, new Teacher(tid, data[1], data[2], sex, Long.parseLong(data[4])));
    }
    System.out.println("Teachers Successfully Inited The number of teachers:"+teachers.size());
    CleanFilePath();
}
    void InitChooseClass(String form) throws IOException {
        chooseClassHashMap=new HashMap<>();
        filePaths+=form;
        BufferedReader reader=new BufferedReader(new FileReader(filePaths));
        String line;
        while((line=reader.readLine())!=null){
            String[] data=line.split(",");
            Long fid=Long.parseLong(data[0]);
            Long cid=Long.parseLong(data[1]);
            Long sid=Long.parseLong(data[2]);
            int logicDelete=Integer.parseInt(data[3]);
            chooseClassHashMap.put(fid,new CourseStudent(fid,cid,sid,logicDelete));
        }
        System.out.println("Successfully InitChooseClass The number of is :"+chooseClassHashMap.size());
        CleanFilePath();
    }

    void InitComments(String form) throws IOException {
        commentHashMap=new HashMap<>();
        filePaths+=form;
        BufferedReader reader=new BufferedReader(new FileReader(filePaths));
        String line;
        while ((line=reader.readLine())!=null){
            String[] data=line.split(",");
            Long fid=Long.parseLong(data[0]);
            Long sid=Long.parseLong(data[1]);
            Long cid=Long.parseLong(data[2]);
            Long tid=Long.parseLong(data[3]);
            int score=Integer.parseInt(data[4]);
            String text=data[5].toString().trim();
            int logic=Integer.parseInt(data[6]);
            commentHashMap.put(fid,new Comment(fid,cid,sid,tid,score,text,logic));
        }
        System.out.println("Success load Comments The number of is :"+commentHashMap.size());
        CleanFilePath();
    }
    void InitCourse(String form) throws IOException {
    couses = new HashMap<>();
    filePaths += form;
    BufferedReader reader = new BufferedReader(new FileReader(filePaths));
    String line;
    while ((line = reader.readLine()) != null) {
        String[] data = line.split(",",5);
        Long l = Long.parseLong(data[0]);
        int capacity = Integer.parseInt(data[2]);
        int credits = Integer.parseInt(data[3]);
        String couseTime=data[4].trim();
        couses.put(l, new Course(l, data[1], capacity, credits,couseTime));
    }
    System.out.println("Courses Successfully Inited The number of courses:" + couses.size());
    CleanFilePath();
}
    void InitClassRoom(String form) throws IOException {
        classRooms = new HashMap<>();
        filePaths+=form;
        BufferedReader reader = new BufferedReader(new FileReader(filePaths));
        String line;
        while((line=reader.readLine()) != null){
            String[] data = line.split(",");

            Long l=Long.parseLong((String)data[0]);
            int Volume=Integer.parseInt(data[2]);
            classRooms.put(l.intValue(),new ClassRoom(l,data[1], Volume));
        }
        System.out.println("Classrooms Successfully Inited The number of classrooms:"+classRooms.size());
        CleanFilePath();
    }
    //按照String形式的ID
    public Student getStudent(String sid){
        return students.get((int)(Long.parseLong(sid)%MOD_VALUE));
    }
    public Student getStudentById(Long sid){
        return students.get((int)(sid%MOD_VALUE));
    }
    public Teacher getTeacher(String tid){
        return teachers.get(Long.parseLong(tid));
    }
    public Manager getManager(String mid){
        return managers.get((int)(Long.parseLong(mid)%MOD_VALUE));
    }
    public Course getCourse(String cid){
        return couses.get((int)(Long.parseLong(cid)%MOD_VALUE));
    }
    public ClassRoom getClassRoom(String cid){
        return classRooms.get((int)(Long.parseLong(cid)%MOD_VALUE));
    }
    
    //按照ID查询
    public Student getStudentById(Integer id) {
        return students.get(id);
    }
    public Comment getCommentById(Long id) {
        return commentHashMap.get(id);
    }
    public ArrayList<Comment> getCommentsBySId(Long id) {
        ArrayList<Comment> comments=new ArrayList<>();
        for(Long key : commentHashMap.keySet()){
            if(commentHashMap.get(key).getSid().equals(id)){
                comments.add(commentHashMap.get(key));
            }
        }
        return comments;
    }

    public Teacher getTeacherById(Long id) {
        return teachers.get(id);
    }

    public ArrayList<Course> getCourseList(Long sid) {
        ArrayList<Long> keys=new ArrayList<>();
        for(Long id :chooseClassHashMap.keySet()){
            if(chooseClassHashMap.get(id).getSid().equals(sid)){
                keys.add(chooseClassHashMap.get(id).getCid());
            }
        }
        ArrayList<Course> courses=new ArrayList<>();
        for(Long id: keys){
            courses.add(couses.get(id));
        }
        return  courses;
    }
    public Course getCourseById(Long id) {
        return couses.get(id);
    }
    public ClassRoom getClassRoomById(Integer id) {
        return classRooms.get(id);
    }
    public Manager getManagerById(Integer id) {
        return managers.get(id);
    }

    public ArrayList<Student> getStudentByCId(long l) {
        ArrayList<Student> students=new ArrayList<>();
        for(Long key : chooseClassHashMap.keySet()){
            if(chooseClassHashMap.get(key).getCid().equals(l)){
                students.add(getStudentById(chooseClassHashMap.get(key).getSid()));
            }
        }
        return students;
    }

    public ClassRoom getClassRoomByCId(Long cid) {
        for(Long key : courseClassroomHashMap.keySet()) {
            CourseClassroom courseClassroom = courseClassroomHashMap.get(key);
            if (courseClassroom.getCid().equals(cid)) {
                Long classroomId = courseClassroom.getClassroomid();
                Integer id=classroomId.intValue();
                ClassRoom room = classRooms.get(id);
                return  room;
            }
        }
        return null;
    }

    public ArrayList<ClassRoom> getClassroomByMid(Long mid) {
        ArrayList<ClassRoom> classRooms1=new ArrayList<>();
        int flage=0;
        for(Integer key : managers.keySet()){
            if(managers.get(key).getMid().equals(mid)){
                flage=1;
            }
        }
        if(flage==0){
            return null;
        }else{
            for(Integer key : classRooms.keySet()){
                classRooms1.add(classRooms.get(key));
            }
            return  classRooms1;
        }
    }

    public ArrayList<Teacher> getTeacherByMid(Long mid) {
        ArrayList<Teacher> teachers1=new ArrayList<>();
        int flage=0;
        for(Integer key : managers.keySet()){
           if(managers.get(key).getMid().equals(mid)){
               flage=1;
           }
        }
        if(flage==0){
            return null;
        }else{
            for(Long key : teachers.keySet()){
               teachers1.add(teachers.get(key));
            }
            return  teachers1;
        }
    }

    public ArrayList<Student> getStudentByMid(Long mid) {
        ArrayList<Student> students1=new ArrayList<>();
        int flage=0;
        for(Integer key : managers.keySet()){
            if(managers.get(key).getMid().equals(mid)){
                flage=1;
            }
        }
        if(flage==0){
            return null;
        }else{
            for(Integer key :students.keySet()){
                students1.add(students.get(key));
            }
            return  students1;
        }
    }

    public ArrayList<Course> getCourseByMid(Long mid) {
        ArrayList<Course> course=new ArrayList<>();
        int flage=0;
        for(Integer key : managers.keySet()){
            if(managers.get(key).getMid().equals(mid)){
                flage=1;
            }
        }
        if(flage==0){
            return null;
        }else{
            for(Long key : couses.keySet()){
                course.add(couses.get(key));
            }
            return  course;
        }
    }


    public void UpdataTeacher(Teacher teacher) throws IOException {
       if(teachers.get(teacher.getTid())==null){
           teachers.put(teacher.getTid(),teacher);
           loadTeachers(1);
       }else{
           loadTeachers(2);
           teachers.replace(teacher.getTid(),teacher);
       }

    }
    void loadTeachers(int flage) throws IOException {
        String targetFilePath = filePaths + "User_Teachers.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
        for (Long key :teachers.keySet()) {
            Teacher teacherr=teachers.get(key);
            if (teacherr != null) { // 仅保存存在的学生数据（无空行）
                Long Tid= teacherr.getTid();
                String name=teacherr.getUserName();
                Long password;
                if(flage==1) {
                 password= 1L;
                }else{
                    password= Long.valueOf(teacherr.getPassword());
                }
                int sex=teacherr.getSex();
                Long phonenumber= teacherr.getPhonenumber();
                // 2. 生成与InitStudent解析格式完全一致的CSV行
                String line = String.format("%d,%s,%d,%d,%d", Tid,name,password,sex,phonenumber);
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();
        System.out.println("Teacher Saved. Total: " + teachers.size());
        CleanFilePath(); // 清理路径（与InitStudent逻辑一致）
    }
    String DetectConfilics(){
        StringBuilder sb = new StringBuilder();
        int[] a=new int[10000000];
        for(int i=0;i<a.length;i++){
            a[i]=0;
        }
        for(Long key: teacherClassHashMap.keySet()){
            TeacherClass teacherClass=new TeacherClass();
            if(a[teacherClass.getCid().intValue()]!=0){
                sb.append("工号为："+teacherClass.getTid()+"的老师"+"同时教了两门课");
            }
            a[teacherClass.getCid().intValue()]=1;
        }
        return "课程冲突";
    }



    public void UpdataClassRoom(ClassRoom room) throws IOException {
        if(classRooms.get(room.getCid())==null){
            classRooms.put(room.getCid().intValue(),room);
        }else{
            classRooms.replace(room.getCid().intValue(),room);
        }
        loadClassrooms();
    }
    void loadClassrooms() throws IOException {
        String targetFilePath = filePaths + "ClassRooms";
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
        for (Integer key : classRooms.keySet()) {
            ClassRoom classRoom=classRooms.get(key);
            if (classRoom != null) { // 仅保存存在的课程数据（无空行）
                // 2. 生成与InitStudent解析格式完全一致的CSV行
                String line = String.format("%d,%s,%d",classRoom.getCid(),classRoom.getClassName(),classRoom.getVolume());
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();
        System.out.println("Classroom Saved. Total: " + classRooms.size());
        CleanFilePath();
    }
    void Test(){
        PlanCourse();
    }
    public void UpdataCourse(Course course) throws IOException {
if(couses.get(course.getCourseId())==null){
    couses.put(course.getCourseId(),course);
}else{
    couses.replace(course.getCourseId(),course);
}
LoadCourse();
    }
    void LoadCourse() throws IOException {
        String targetFilePath = filePaths + "Courses";
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
        for (Long key : couses.keySet()) {
            Course courser=couses.get(key);
            if (courser!= null) { // 仅保存存在的课程数据（无空行）
                // 2. 生成与InitStudent解析格式完全一致的CSV行
                String line = String.format("%d,%s,%d,%d,%s",
                        courser.getId(),courser.getCourseName(),
                        courser.getCousePoint(),courser.getCourseHour(),courser.getCourseTime());
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();
        System.out.println("Course Saved. Total: " + couses.size());
    }
    void PlanCourse() {
        // 1. 为每个课程安排时间和教室
        for (Course course : couses.values()) {
            // 假设我们有一个时间表和教室分配系统
            String availableTime = findAvailableTimeForCourse(course);
            ClassRoom availableClassroom = findAvailableClassroom(course);
            if (availableTime != null && availableClassroom != null) {
            } else {
                System.out.println("无法为课程 " + course.getCourseName() + " 安排时间或教室！");
            }
        }
        // 2. 为学生安排课程
        for (Student student : students.values()) {
            ArrayList<Course> studentCourses = getCourseList(student.getSid());
            for (Course course : studentCourses) {
                if (detectTimeConflictForStudent(student, course)) {
                    System.out.println("学生 " + student.getUserName() + " 的课程 " + course.getCourseName() + " 有时间冲突！");
                }
            }
        }
        // 3. 为教师安排课程
        for (Teacher teacher : teachers.values()) {
            ArrayList<Course> teacherCourses = GetCourseByTid(teacher.getTid());
            for (Course course : teacherCourses) {
                if (detectTimeConflictForTeacher(teacher, course)) {
                    System.out.println("教师 " + teacher.getUserName() + " 的课程 " + course.getCourseName() + " 有时间冲突！");
                }
            }
        }
    }

    private ClassRoom findAvailableClassroom(Course course) {
        return null;
    }

    private String findAvailableTimeForCourse(Course course) {
        return null;
    }

    private boolean detectTimeConflictForTeacher(Teacher teacher, Course course) {
        return false;
    }

    private boolean detectTimeConflictForStudent(Student student, Course course) {
        return false;
    }

//    String DetectConflicts(){
//
//    }
//    void PlanCourse(){
//
//    }
}
