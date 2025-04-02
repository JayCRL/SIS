package org.example.sis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.sis.Entity.Comment;
import org.example.sis.Entity.Course;
import org.example.sis.Entity.Student;
import org.example.sis.Entity.Teacher;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class StudentMainStage extends Stage {
    private VBox navBar;
    private StackPane contentPane;

    private Student student;
    private Socket socket;

    public StudentMainStage(Socket socket,String student)  {
        this.socket=socket;
        this.student=Student.fromString(student);//解析字符串数据成Student对象
        setupUI();
        setupStyle();
    }
    private void setupUI() {
        HBox root = new HBox();
        root.setPrefSize(1200, 800);
        // 左侧导航栏
        navBar = createNavBar();
        // 右侧内容区
        contentPane = new StackPane();
        contentPane.getChildren().add(createHomePage());
        root.getChildren().addAll(navBar, contentPane);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        setScene(scene);
        setTitle("学生信息管理系统");
    }

    private VBox createNavBar() {
        //设置导航栏
        VBox nav = new VBox(20);
        nav.setPrefWidth(200);
        nav.setStyle("-fx-background-color: #2c3e50;");
        //边距
        nav.setPadding(new Insets(30, 20, 30, 20));

        String[] tabs = {"个人主页", "课表查询", "教学评价"};
        for (String tab : tabs) {
            Button btn = new Button(tab);
            btn.getStyleClass().add("nav-btn");
            //预宽度
            btn.setPrefWidth(160);
            //设置单机函数
            btn.setOnAction(e -> {
                try {
                    switchTab(tab);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            //全部加进去
            nav.getChildren().add(btn);
        }
        // 退出按钮
        Button logoutBtn = new Button("退出登录");
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setOnAction(e -> this.close());
        nav.getChildren().add(logoutBtn);
        return nav;
    }

    private void switchTab(String tab) throws IOException {
        contentPane.getChildren().clear();
        switch (tab) {
            case "个人主页":
                contentPane.getChildren().add(createHomePage());
                break;
            case "课表查询":
                contentPane.getChildren().add(createSchedulePage());
                break;
            case "教学评价":
                contentPane.getChildren().add(createEvaluationPage());
                break;
        }
    }
    // 以下是各页面实现
    private VBox createHomePage()  {
        VBox home = new VBox(30);
        home.setPadding(new Insets(40));
        home.setAlignment(Pos.TOP_RIGHT);
        // 标题（强制顶部居中）
        Label welcome = new Label("             欢迎回来，同学！");
        welcome.setMaxWidth(Double.MAX_VALUE);
        welcome.setAlignment(Pos.CENTER);
        welcome.getStyleClass().add("welcome-label");
        // 内容容器（确保绝对居中）
        StackPane contentPane = new StackPane();
        contentPane.setAlignment(Pos.CENTER_RIGHT);
        GridPane infoGrid = new GridPane();
        infoGrid.setAlignment(Pos.CENTER_RIGHT);
        infoGrid.setVgap(25);
        infoGrid.setHgap(30);
        // 列约束（设置百分比宽度）
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setPercentWidth(30);
        labelCol.setHalignment(HPos.CENTER);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setPercentWidth(70);
        fieldCol.setHalignment(HPos.CENTER);
        infoGrid.getColumnConstraints().addAll(labelCol, fieldCol);
        TextField idField = createEditableField(student.getSid().toString(),false);
        TextField nameField = createEditableField(student.getUserName().toString(),false);
        TextField sexField=createEditableField(student.getSex()==1?"男":"女",false);
        TextField phoneField = createEditableField(student.getPhoneNumber().toString(),true);
        infoGrid.addRow(0, createInfoLabel("学号："),idField);
        infoGrid.addRow(1, createInfoLabel("姓名："), nameField);
        infoGrid.addRow(2, createInfoLabel("性别："),sexField);
        infoGrid.addRow(3, createInfoLabel("电话号码："),phoneField);
        infoGrid.setMaxWidth(600);
        contentPane.getChildren().add(infoGrid);
        // 底部按钮容器
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));
        Button saveBtn = new Button("保存修改");
        saveBtn.getStyleClass().add("save-btn");
        saveBtn.setOnAction(e -> {
            try {
                handleSave(phoneField);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonContainer.getChildren().add(saveBtn);
        home.getChildren().addAll(welcome, contentPane, buttonContainer);
        return home;
    }
    // 保存处理方法
    private void handleSave(TextField phoneField) throws IOException {
        student.setInformation(Long.parseLong(phoneField.getText().trim()));
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        printWriter.println("UpdataSlefInformation");
        printWriter.println(student.toString());
        // 这里添加实际的保存逻辑
        System.out.println("保存个人信息");
        showAlert("保存成功", "个人信息已更新");
    }
    //通过学生id获取课程数据集
    private String getCourses(Long sid) throws IOException {
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter.println("GetCourses");
        printWriter.println(sid);
        String course=reader.readLine();
        return course;
    }
    //通过课程号获取课程
    public Course GetCourse(Long cid) throws IOException {
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter.println("GetCourse");
        printWriter.println(cid);
        return Course.parseCourse(reader.readLine());
    }
    //通过课程号获取评论集
    private String GetComments(Long sid) throws IOException {
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter.println("GetComments");
        printWriter.println(sid);
        String comment=reader.readLine();
        return comment;
    }
    //通过教师id获取教师信息
    private Teacher getTeaccher(Long tid)throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        printWriter.println("GetTeacher");
        printWriter.println(tid);
        return Teacher.Parse(bufferedReader.readLine());
    }
    //创建自定义标签
    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("info-label");
        label.setAlignment(Pos.CENTER_RIGHT);
        //标签尺寸
        label.setPrefSize(180, 45); // 放大标签尺寸
        return label;
    }
    //创建自定义输入框
    private TextField createEditableField(String value,boolean ENABLE) {
        TextField field = new TextField(value);
        field.setEditable(ENABLE);
        field.getStyleClass().add("info-field");
        //标签尺寸
        field.setPrefSize(420, 45); // 放大输入框尺寸
        return field;
    }
    //创建课表视图
    private VBox createSchedulePage() throws IOException {
        String message= getCourses(student.getSid());
        System.out.println("course:"+message);
        message=message.substring(1,message.length()-2);
        String[] cour = message.split("},");
        ArrayList<Course> coursess = new ArrayList<>();
        for(String c : cour){
            coursess.add(Course.parseCourse(c));
        }
        VBox schedule = new VBox(30);
        schedule.setPadding(new Insets(30));
        schedule.setAlignment(Pos.TOP_CENTER);
        // 表格标题
        Label title = new Label("我的课表");
        title.getStyleClass().add("schedule-title");
        // 课表表格
        TableView<Course> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // 自动填充宽度
        // 列定义（设置最小宽度）
        TableColumn<Course, String> timeCol = new TableColumn<>("课程号");
        timeCol.setMinWidth(50);
        timeCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));

        TableColumn<Course, String> nameCol = new TableColumn<>("课程名称");
        nameCol.setMinWidth(200);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Course, String> roomCol = new TableColumn<>("课时");
        roomCol.setMinWidth(100);
        roomCol.setCellValueFactory(new PropertyValueFactory<>("courseHour"));

        TableColumn<Course, String> CPointCol = new TableColumn<>("学分");
        CPointCol.setMinWidth(100);
        CPointCol.setCellValueFactory(new PropertyValueFactory<>("cousePoint"));

        TableColumn<Course, String> CPosition = new TableColumn<>("上课时间");
        CPosition.setMinWidth(100);
        CPosition.setCellValueFactory(new PropertyValueFactory<>("courseTime"));


        tableView.getColumns().addAll(timeCol, nameCol, roomCol,CPointCol,CPosition);
        // 设置表格尺寸约束
        tableView.setPrefWidth(1000); // 预设宽度
        tableView.setMinHeight(400);

        // 添加示例数据
        ObservableList<Course> courses = FXCollections.observableArrayList(coursess);
        tableView.setItems(courses);
        // 导出按钮容器（右对齐）
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button exportBtn = new Button("导出课表");
        exportBtn.getStyleClass().add("export-btn");
        exportBtn.setOnAction(e -> exportSchedule(courses));

        buttonBox.getChildren().add(exportBtn);

        schedule.getChildren().addAll(title, tableView, buttonBox);
        return schedule;
    }
    //导出课表
    //保存课表
    private void exportSchedule( ObservableList<Course> courses) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存课表");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        File file = fileChooser.showSaveDialog(this);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("---课程名称------时间-----学时-------学分");
                for(Course course:courses){
                    String name=course.getCourseName()+course.getCourseTime()+course.getCourseHour()+" "+course.getCousePoint();
                    writer.println(name);
                }
                // 添加实际数据
                showAlert("导出成功", "课表已保存至：" + file.getPath());
            } catch (IOException ex) {
                showAlert("导出失败", "文件保存出错：" + ex.getMessage());
            }
        }
    }
    //获取评论课表
    private VBox createEvaluationPage() throws IOException {
        //获取评论
        ArrayList<Comment> comments = Comment.parseComments(GetComments(student.getSid()));
        Accordion accordion = new Accordion();

        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().add(accordion);

        for(Comment comment : comments){
            //遍历每条评论
            //获取课程信息
            //获取老师名称
            Teacher teacher=getTeaccher(comment.getTid());
            Course course=GetCourse(comment.getCid());
            accordion.getPanes().addAll(
                    createEvaluationItem(course.getCourseName(), teacher.getUserName(),comment)
            );
        }
        return mainLayout;
    }
    //更新评论
    void UpDataComment(Comment comment) throws IOException {
        PrintWriter writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer.println("updateComment");
        writer.println(comment.toString());
    }
    //设置评论页面
    private TitledPane createEvaluationItem(String course, String teacher,Comment commen) {
        // 评价内容部分（默认隐藏）
        VBox content = new VBox(10);
        content.getStyleClass().add("eval-content"); // 添加CSS样式类

        TextArea comment = new TextArea();
        comment.setPromptText("请输入您的评价...");
        comment.getStyleClass().add("eval-comment");  // 文本框样式类

        // 评分滑块配置为整数选择
        Slider scoreSlider = new Slider(0, 10, 8);
        scoreSlider.setMajorTickUnit(1);
        scoreSlider.setMinorTickCount(0);
        scoreSlider.setSnapToTicks(true);
        scoreSlider.setShowTickLabels(true);
        scoreSlider.setShowTickMarks(true);
        scoreSlider.getStyleClass().add("eval-slider"); // 滑块样式类

        Button submitBtn = new Button("提交评价");
        submitBtn.getStyleClass().add("eval-submit-btn"); // 按钮样式类
        //提交函数
        submitBtn.setOnAction(e -> {
            int score = (int) scoreSlider.getValue();
            String text=comment.getText();
            commen.setText(text);
            commen.setScore(score);
            try {
                UpDataComment(commen);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            showAlert("提交成功", "已提交对" + course + "的评价");
        });
        // 将按钮放入右对齐的HBox
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER_RIGHT); // 右对齐
        buttonContainer.getChildren().add(submitBtn);
        content.getChildren().addAll(
                new Label("评分（0-10分）："), scoreSlider,
                new Label("文字评价："), comment, buttonContainer // 替换为容器
        );
        content.setPadding(new Insets(10));
        // 标题区域：课程信息 + 评价按钮
        HBox titleBox = new HBox(10);
        titleBox.getStyleClass().add("eval-title-box"); // 标题栏样式类
        Label courseLabel = new Label(course);
        courseLabel.getStyleClass().add("eval-course");
        Label teacherLabel = new Label("教师：" + teacher);
        teacherLabel.getStyleClass().add("eval-teacher");
        Button evalBtn = new Button("评价");
        evalBtn.getStyleClass().add("eval-trigger-btn");
        TitledPane pane = new TitledPane();
        pane.getStyleClass().add("eval-titled-pane"); // 核心样式类
        pane.setGraphic(titleBox);
        pane.setContent(content);
        pane.setExpanded(false);
        // 点击按钮展开评价界面
        evalBtn.setOnAction(e -> pane.setExpanded(true));
        titleBox.getChildren().addAll(courseLabel, teacherLabel, evalBtn);
        return pane;
    }
   //设置logo
    private void setupStyle() {
        getIcons().add(new Image("/logo.png"));
    }
    //显示提示框
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
// 课程数据模型（需添加到Client类外部）
