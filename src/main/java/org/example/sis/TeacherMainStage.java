package org.example.sis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.sis.Entity.Course;
import org.example.sis.Entity.Student;
import org.example.sis.Entity.Teacher;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class TeacherMainStage extends Stage {
    Socket socket;
    Teacher teacher;
    HashMap<Long, Course> courseHashMap;
    private BorderPane mainLayout = new BorderPane();
    private StackPane contentArea = new StackPane();
    private VBox navigationBar = new VBox(10);

    // 模拟数据
    private ObservableList<String> courses = FXCollections.observableArrayList(
            "Java编程基础", "数据结构与算法", "软件工程"
    );

    private ObservableList<Pair<String, String>> feedback = FXCollections.observableArrayList(
            new Pair<>("Java编程基础", "学生掌握情况良好"),
            new Pair<>("数据结构与算法", "需要加强实践练习")
    );

    private ObservableList<Schedule> schedules = FXCollections.observableArrayList(
            new Schedule("Java编程基础", "周一 8:00-10:00", "逸夫楼301"),
            new Schedule("数据结构与算法", "周三 14:00-16:00", "计算机中心205")
    );

    public ArrayList<Course> GetTeacherCourse() throws IOException {
        //通过老师号获取课程代码
        ArrayList<Course> courses = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("getTeacherCourse");
        //tid
        printWriter.println(teacher.getTid());
        //获取数据
        String message=reader.readLine();
        System.out.println(message);

        String[] cours= message.substring(1,message.length()-1).split("}, ");
        for(int i=0;i< cours.length;i++){
         if(i!=cours.length-1){
             cours[i]+="}";
         }
            courses.add(Course.parseCourse(cours[i]));
        }
        //解析数据
        System.out.println("获取数据成功");
        return courses;
    }
    public TeacherMainStage(Socket socket,Teacher teacher) throws IOException {
        this.socket = socket;
        this.teacher = teacher;
        this.setTitle("教师主界面");
        initializeUI();
        this.setScene(new Scene(mainLayout, 800, 600));
    }
    private void initializeUI() throws IOException {
        setupNavigationBar();
        setupContentArea();
        mainLayout.setLeft(navigationBar);
        mainLayout.setCenter(contentArea);
    }

    private void setupNavigationBar() {
        navigationBar.setPadding(new Insets(15));
        navigationBar.setStyle("-fx-background-color: #f0f0f0;");
        Button scoreEntryBtn = new Button("成绩录入");
        Button feedbackBtn = new Button("查看反馈");
        Button scheduleBtn = new Button("我的课表");
        Button exitBtn = new Button("退出");
        // 按钮样式
        String buttonStyle = "-fx-min-width: 120px; -fx-pref-height: 35px;";
        scoreEntryBtn.setStyle(buttonStyle);
        feedbackBtn.setStyle(buttonStyle);
        scheduleBtn.setStyle(buttonStyle);
        exitBtn.setStyle(buttonStyle + "-fx-background-color: #ff4444; -fx-text-fill: white;");
        // 事件绑定
        scoreEntryBtn.setOnAction(e -> showScoreEntry());
        feedbackBtn.setOnAction(e -> showFeedback());
        scheduleBtn.setOnAction(e -> showSchedule());
        exitBtn.setOnAction(e -> this.close());
        navigationBar.getChildren().addAll(scoreEntryBtn, feedbackBtn, scheduleBtn, exitBtn);
        VBox.setVgrow(exitBtn, Priority.ALWAYS);
    }

    private void setupContentArea() throws IOException {
        contentArea.getChildren().addAll(
                createScoreEntryPane(),
                createFeedbackPane(),
                createSchedulePane()
        );
        showScoreEntry(); // 默认显示第一个界面
    }
    ArrayList<Student> GetStudents(Long cid) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        ArrayList<Student> students = new ArrayList<Student>();
        printWriter.println("getStudents");
        printWriter.println(cid);
        String message=reader.readLine();
        if(message.equals("Wrong Number of Students")){
            System.out.println("Wrong number of students");
            return students;
        }
        message=message.substring(1,message.length()-1);
        String[] individuals=message.split("},");
        for(String individual : individuals) {
            try {
                students.add(Student.fromString(individual));
            }catch (Exception e){
                System.out.println("Parse Student Error: " + e.getMessage());
            }
        }
        return students;
    }
    // region 成绩录入界面
    private VBox createScoreEntryPane() throws IOException {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setId("scoreEntry");
        // 获取教师课程列表
        ArrayList<Course> courseListData = GetTeacherCourse();
        ObservableList<Course> courses = FXCollections.observableArrayList(courseListData);
        // 创建课程列表视图
        ListView<Course> courseList = new ListView<>(courses);
        courseList.setPrefHeight(200);

        // 设置单元格显示课程名称
        courseList.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getCourseName());
            }
        });

        VBox studentScores = new VBox(10);

        // 监听课程选择
        courseList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            studentScores.getChildren().clear();
            if (newVal != null) {
                Course selectedCourse = newVal;
                Student studen=new Student(1L,"李华","123",1,111L);
                // 获取该课程的学生列表
                ArrayList<Student> students = new ArrayList<>();
                students.add(studen);
                    try {
                        students = GetStudents(selectedCourse.getCourseId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                for (Student student : students) {
                    HBox row = new HBox(10);
                    Label nameLabel = new Label(student.getUserName());
                    TextField scoreField = new TextField();
                    Button saveBtn = new Button("保存");
                    // 保存时携带课程信息
                    saveBtn.setOnAction(e -> {
                        try {
                            double score = Double.parseDouble(scoreField.getText());
                            showAlert("Success","成绩保存成功");
                        } catch (NumberFormatException ex) {
                            showAlert("Wrong","请输入有效的数字成绩");
                        }
                    });

                    row.getChildren().addAll(nameLabel, scoreField, saveBtn);
                    studentScores.getChildren().add(row);
                }
            }
        });

        container.getChildren().addAll(
                new Label("选择课程："),
                courseList,
                new Separator(),
                new Label("学生成绩录入："),
                studentScores
        );
        return container;
    }

    private void saveGradeToDatabase(Long courseId, Student student, double score) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer.println("saveGrade");
        writer.println(courseId);
        writer.println(student.toString());
        writer.println(score);
        String message = bufferedReader.readLine();
        if (!message.equals("success")) {
            throw new RuntimeException("Failed to save grade: " + message);
        }
    }
    // endregion

    ArrayList<Course> GetMyCourse() throws IOException {
        ArrayList<Course> courses = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GetMyCourseByTeacherID");
        printWriter.println(teacher.getTid());
        String result=reader.readLine();
        if(result.equals("Wrong Number of Course")){
            System.out.println("Wrong number of courses");
            return new ArrayList<>();
        }else{
           result=result.substring(0,result.length()-1);
           String[] corse=result.split("节}, ");
           corse[0]=corse[0].substring(1);
           for(int i=0;i<corse.length-2;i++){
               if(i!=corse.length-2){
                   corse[i]=corse[i]+"节}";
               }
               //不是第一个处理掉,{

                courses.add(Course.parseCourse(corse[i]));
           }
        }
        return courses;
    }
    ArrayList<String> GetCourseCommentsById(Long cid) throws IOException {
        ArrayList<String> comments = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GetCourseCommentsById");
        printWriter.println(cid);
        String message=reader.readLine();
        if(message.equals("Wrong Number of Comments")){
            System.out.println("Wrong number of comments");
            return new ArrayList<>();
        }else{
           String result=message.substring(0,message.length()-1);
           String[] cnts=result.split(",");
            for(String c: cnts){
                comments.add(c);
            }
        }
        return  comments;
    }
    // region 查看反馈界面
    private VBox createFeedbackPane() throws IOException {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setId("feedback");

// 课程列表数据（假设Course类包含getId()和getCourseName()方法）
        ObservableList<Course> courses = FXCollections.observableArrayList(GetMyCourse());

// 课程列表视图
        ListView<Course> courseList = new ListView<>(courses);
        courseList.setId("courseList");
        courseList.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    // 实时获取评价数量
                    int commentCount = 0;
                    try {
                        commentCount = GetCourseCommentsById(course.getId()).size();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    setText(course.getCourseName() + " (" + commentCount + "条评价)");
                }
            }
        });

// 界面元素
        Label currentTitle = new Label("所有课程：");
        Button backButton = new Button("返回课程列表");
        backButton.setVisible(false);

// 评价列表视图
        ListView<String> commentList = new ListView<>();
        commentList.setId("commentList");

// 课程选择事件处理
        courseList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedCourse) -> {
            if (selectedCourse != null) {
                // 异步加载评价数据
                Task<ObservableList<String>> loadCommentsTask = new Task<>() {
                    @Override
                    protected ObservableList<String> call() throws IOException {
                        return FXCollections.observableArrayList(
                                GetCourseCommentsById(selectedCourse.getId())
                        );
                    }
                };

                loadCommentsTask.setOnSucceeded(e -> {
                    commentList.setItems(loadCommentsTask.getValue());
                    currentTitle.setText(selectedCourse.getCourseName() + "的评价：");
                    backButton.setVisible(true);
                    container.getChildren().setAll(currentTitle, commentList, backButton);
                });

                new Thread(loadCommentsTask).start();
            }
        });

// 返回按钮处理
        backButton.setOnAction(e -> {
            container.getChildren().setAll(currentTitle, courseList);
            backButton.setVisible(false);
        });

// 初始状态
        container.getChildren().addAll(currentTitle, courseList);
        return container;
    }
    // endregion
    String GetClassRoomByCid(Long cid) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GetClassRoomByCid");
        printWriter.println(cid);
        String message=reader.readLine();
        return message;

    }
    // region 我的课表界面
    private VBox createSchedulePane() throws IOException {
        // 获取当前用户的课程列表
        ArrayList<Course> myCourses = GetMyCourse();
        // 创建表格和数据容器
        TableView<Schedule> table = new TableView<>();

        ObservableList<Schedule> schedules = FXCollections.observableArrayList();

        // 创建列（保持不变）
        TableColumn<Schedule, String> nameCol = new TableColumn<>("课程名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Schedule, String> timeCol = new TableColumn<>("上课时间");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Schedule, String> roomCol = new TableColumn<>("上课教室");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        // 遍历课程列表填充数据
        for (Course course : myCourses) {
            try {
                // 获取课程基本信息
                String courseName = course.getCourseName();
                Long cid = course.getId();

                // 获取教室信息（假设已实现getClassRoomByCid方法）
                String classroom = GetClassRoomByCid(cid);

                // 获取上课时间（假设Course类有getClassTime方法）
                String classTime = course.getCourseTime();
                // 创建日程对象并添加到列表
                schedules.add(new Schedule(courseName, classTime, classroom));
            } catch (Exception e) {
                // 处理单个课程获取失败的情况
                System.err.println("加载课程失败: " + course.toString());
                e.printStackTrace();
            }
        }

        // 设置表格数据
        table.getColumns().addAll(nameCol, timeCol, roomCol);
        table.setItems(schedules);
        // 导出按钮（保持不变）
        Button exportBtn = new Button("导出课表");
        exportBtn.setOnAction(e -> exportScheduleToCSV());
        // 将按钮加入布局（需要根据实际布局调整）
        VBox vbox = new VBox(10); // 添加10像素间距
        vbox.getChildren().addAll(table, exportBtn);
        vbox.setId("schedule");
        // 关键修改：设置表格动态高度
        table.setPrefHeight(Region.USE_COMPUTED_SIZE); // 自动计算高度
        VBox.setVgrow(table, Priority.ALWAYS);         // 允许表格扩展

        return vbox; // 返回包含表格和按钮的容器
    }
    private void exportScheduleToCSV() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        File file = fileChooser.showSaveDialog(this);
        try (FileWriter writer = new FileWriter(file)) {
            // 写入CSV表头
            writer.write("课程名称,上课时间,上课教室\n");
            // 写入数据行
            for (Schedule item : schedules) {
                writer.write(String.format("%s,%s,%s\n",
                        item.getName(),
                        item.getTime(),
                        item.getRoom()
                ));
            }
            writer.flush();
            new Alert(Alert.AlertType.INFORMATION, "课表已导出").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "导出失败: " + e.getMessage()).show();
        }
    }
    // endregion

    private void showScoreEntry() {
        switchContent("scoreEntry");
    }

    private void showFeedback() {
        switchContent("feedback");
    }

    private void showSchedule() {
        switchContent("schedule");
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void switchContent(String id) {
        contentArea.getChildren().forEach(node -> {
            node.setVisible(node.getId() != null && node.getId().equals(id));
        });
    }

    // 课表数据模型
    // Schedule数据模型类
    public static class Schedule {
        private final String name;
        private final String time;
        private final String room;

        public Schedule(String name, String time, String room) {
            this.name = name;
            this.time = time;
            this.room = room;
        }

        public String getName() { return name; }
        public String getTime() { return time; }
        public String getRoom() { return room; }
    }
}
