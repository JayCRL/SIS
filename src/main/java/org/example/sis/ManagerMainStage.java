package org.example.sis;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.sis.Entity.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static org.example.sis.Entity.ClassRoom.getClassRoom;

public class ManagerMainStage extends Stage {
    Socket socket;
    Manager manager;

    private BorderPane mainPane = new BorderPane();
    private StackPane contentPane = new StackPane();
    ManagerMainStage(Socket socket,Long managerId) throws IOException {
        this.socket=socket;
        this.manager=new Manager(managerId);
        loadAllData();
        setupUI();
    }
    ArrayList<Student> students=new ArrayList<Student>();
    ArrayList<ClassRoom> classrooms=new ArrayList<ClassRoom>();
    ArrayList<Teacher> teachers=new ArrayList<Teacher>();
    ArrayList<Course> courses=new ArrayList<Course>();

    public void UpDataStudent(Student student) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        printWriter.println("updateStudent");
        printWriter.println(student.toString());
    }
    public void UpDataClassRoom(ClassRoom classroom) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        printWriter.println("updateClassroom");
        printWriter.println(classroom.toString());
    }
    public void UpDataTeacher(Teacher teacher) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        printWriter.println("updateTeacher");
        printWriter.println(teacher.toString());
    }
    public void UpDataCourse(Course course) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        printWriter.println("updateCourse");
        printWriter.println(course.toString());

    }
    ArrayList<Course> GetCourseList() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        printWriter.println("GET COURSES");
        printWriter.println(manager.getMid());
        String result = reader.readLine();
        String[] courseText = result.substring(1, result.length() - 1).split(", Course");
        for (String ct : courseText) {
            courses.add(Course.parseCourse(ct));
        }
        return  courses;
    }
    ArrayList<ClassRoom> GetClassRoomList() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GET CLASSROOM");
        printWriter.println(manager.getMid());
        String result=reader.readLine();
        String[] courseText = result.substring(1, result.length() - 1).split(", Classroom");
        for (String ct : courseText) {
         classrooms=getClassRoom(ct);
        }
        return  classrooms;
    }
    //获取老师的列表
    ArrayList<Teacher> GetTeacherList() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GET Teachers");
        printWriter.println(manager.getMid());
        String result=reader.readLine();
        String[] courseText = result.substring(1, result.length() - 2).split("}, ");
        for (String ct : courseText) {
            teachers.add(Teacher.Parse(ct));
        }
        return  teachers;
    }
    //获取学生的列表
    ArrayList<Student> GetStudentList() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        printWriter.println("GET Students");
        printWriter.println(manager.getMid());
        String result=reader.readLine();
        String[] courseText = result.substring(1, result.length() - 2).split("}, ");
        for (String ct : courseText) {
            students.add(Student.fromString(ct+"}"));
        }
        return  students;
    }
    //初始化界面
    private void setupUI() {
        // 左侧导航栏
        VBox navigation = new VBox(10);
        navigation.setPadding(new Insets(15));
        navigation.setStyle("-fx-background-color: #f0f0f0;");
        Button btnInfo = createNavButton("信息修改", this::showInfoManagement);
        Button btnCourse = createNavButton("课程管理", this::showCourseManagement);
        Button btnCheck = createNavButton("检验课表", this::showConflictCheck);
        navigation.getChildren().addAll(btnInfo, btnCourse, btnCheck);
        mainPane.setLeft(navigation);
        mainPane.setCenter(contentPane);
        setScene(new Scene(mainPane, 1000, 600));
        setTitle("教务管理系统-管理员");
        show();
    }

    // 信息管理模块
    // 修改后的信息管理模块
    // 新增成员变量跟踪当前数据类型
    private String currentDataType = "STUDENT";

    // 修改后的信息管理模块
    private void showInfoManagement(ActionEvent event) {
        VBox container = new VBox(15);
        //设置间隔
        container.setPadding(new Insets(20));
        //设置竖着的布局
        HBox typeSelector = new HBox(10);
        Button[] btns = {
                createDataButton("学生", "STUDENT"),
                createDataButton("教师", "TEACHER"),
                createDataButton("课程", "COURSE"),
                createDataButton("教室", "CLASSROOM")
        };
        typeSelector.getChildren().addAll(btns);

        // 数据表格 默认学生数据
        TableView<Map<String, String>> table = buildEditableTable(convertStudentsToData());
        // 操作按钮栏
        HBox actionBar = new HBox(10);
        Button btnAdd = new Button("新增");
        btnAdd.setOnAction(e -> showAddDialog());
        Button btnDelete = new Button("删除");
        btnDelete.setOnAction(e -> deleteSelectedItem(table));
        actionBar.getChildren().addAll(btnAdd, btnDelete);
        container.getChildren().addAll(typeSelector, table, actionBar);
        contentPane.getChildren().setAll(container);
    }

    // 新增对话框
    private void showAddDialog() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("新增" + currentDataType);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        //输入框
        Map<String, TextField> fields = new HashMap<>();
        switch(currentDataType) {
            case "STUDENT":
                grid.addRow(0, new Label("学号:"), createTextField(fields, "学号"));
                grid.addRow(1, new Label("姓名:"), createTextField(fields, "姓名"));
                grid.addRow(0, new Label("性别:"), createTextField(fields, "性别"));
                grid.addRow(2, new Label("电话:"), createTextField(fields, "电话"));
                break;
            case "TEACHER":
                grid.addRow(0, new Label("工号:"), createTextField(fields, "工号"));
                grid.addRow(1, new Label("姓名:"), createTextField(fields, "姓名"));
                grid.addRow(1, new Label("密码:"), createTextField(fields, "密码"));
                grid.addRow(1, new Label("性别:"), createTextField(fields, "性别"));
                grid.addRow(1, new Label("电话号码:"), createTextField(fields, "电话号码"));
                break;
            case "COURSE":
                grid.addRow(0, new Label("课程号:"), createTextField(fields, "课程号"));
                grid.addRow(1, new Label("名称:"), createTextField(fields, "名称"));
                grid.addRow(2, new Label("课时:"), createTextField(fields, "课时"));
                grid.addRow(3, new Label("学分:"), createTextField(fields, "学分"));
                grid.addRow(4, new Label("时间",createTextField(fields, "时间")));

                break;
            case "CLASSROOM":
                grid.addRow(0, new Label("教室号:"), createTextField(fields, "教室号"));
                grid.addRow(1, new Label("教室名:"), createTextField(fields, "教室名"));
                grid.addRow(2, new Label("容量:"), createTextField(fields, "容量"));
                break;
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        //若是OK
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, String> result = new HashMap<>();
                //遍历输入框获取数值
                fields.forEach((k,v) -> result.put(k, v.getText()));
                return result;
            }
            return null;
        });
        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(this::handleAddNewItem);
    }

    private TextField createTextField(Map<String, TextField> fields, String key) {
        TextField tf = new TextField();
        fields.put(key, tf);
        return tf;
    }

    // 处理新增数据
    private void handleAddNewItem(Map<String, String> data) {
        try {
            switch(currentDataType) {
                case "STUDENT":
                    Student student = new Student(
                            Long.parseLong(data.get("学号")),
                            data.get("姓名"),
                            null,
                            Integer.parseInt(data.get("性别")),
                            Long.parseLong(data.get("电话"))
                    );
                    students.add(student);
                    UpDataStudent(student);
                    break;
                case "TEACHER":
                    Teacher teacher = new Teacher(
                      Long.parseLong(data.get("工号")),
                            data.get("姓名"),
                            data.get("密码"),
                            Integer.parseInt(data.get("性别")),
                            Long.parseLong(data.get("电话号码"))
                    );
                    teachers.add(teacher);
                    UpDataTeacher(teacher);
                    break;
                case "COURSE":
                    Course course = new Course(Long.parseLong(data.get("课程号")),data.get("名称"),
                            Integer.parseInt(data.get("课时")),
                            Integer.parseInt(data.get("学分")),
                            data.get("时间"));
                    courses.add(course);
                    UpDataCourse(course);
                    break;
                case "CLASSROOM":
                    ClassRoom classroom = new ClassRoom(
                            Long.parseLong(data.get("教室号")),data.get("教室名"),
                            Integer.parseInt(data.get("容量"))
                    );
                    classrooms.add(classroom);
                    UpDataClassRoom(classroom);
                    break;
            }
            refreshTable();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "数据格式错误: " + e.getMessage()).show();
        }
    }

    // 删除选中项
    private void deleteSelectedItem(TableView<Map<String, String>> table) {
        Map<String, String> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "请先选择要删除的项").show();
            return;
        }

        try {
            switch(currentDataType) {
                case "STUDENT":
                    students.removeIf(s -> s.getSid().toString().equals(selected.get("学号")));
                    break;
                case "TEACHER":
                    teachers.removeIf(t -> t.getTid().toString().equals(selected.get("工号")));
                    break;
                case "COURSE":
                    courses.removeIf(c -> c.getCourseId().toString().equals(selected.get("课程号")));
                    break;
                case "CLASSROOM":
                    classrooms.removeIf(c -> c.getCid().toString().equals(selected.get("教室号")));
                    break;
            }
            refreshTable();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "删除失败: " + e.getMessage()).show();
        }
    }

    // 刷新表格
    private void refreshTable() {
        VBox container = (VBox) contentPane.getChildren().get(0);
        TableView<Map<String, String>> newTable = buildEditableTable(getCurrentData());
        container.getChildren().set(1, newTable);
    }
    //根据选择的数据刷新链表
    private ObservableList<Map<String, String>> getCurrentData() {
        switch(currentDataType) {
            case "STUDENT": return convertStudentsToData();
            case "TEACHER": return convertTeachersToData();
            case "COURSE": return convertCoursesToData();
            case "CLASSROOM": return convertClassroomsToData();
            default: return FXCollections.observableArrayList();
        }
    }
    // 学生数据转化为双String哈希表
    private ObservableList<Map<String, String>> convertStudentsToData() {
        ObservableList<Map<String, String>> studentData = FXCollections.observableArrayList();
        for (Student student : students) {
            Map<String, String> item = new HashMap<>();
            item.put("学号", student.getSid().toString());
            item.put("姓名", student.getUserName());
            item.put("电话号码", student.getPhoneNumber().toString());
            studentData.add(item);
        }
        return studentData;
    }

    // 课程管理模块
    // 修改后的课程管理模块
    private void showCourseManagement(ActionEvent event) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        ObservableList<Map<String, String>> courseData = FXCollections.observableArrayList();
        for (Course course : courses) {
            Map<String, String> item = new HashMap<>();
            item.put("课程", course.getCourseName());
            item.put("时间", course.getCourseTime());
            item.put("学时", course.getCourseTime());
            item.put("学分", course.getCousePoint()+"");
            courseData.add(item);
        }

        TableView<Map<String, String>> table = buildEditableTable(courseData);
        Button btnSave = new Button("保存修改");
        btnSave.setOnAction(e -> saveScheduleChanges(table.getItems()));
        container.getChildren().addAll(table, btnSave);
        contentPane.getChildren().setAll(container);
    }
    private void saveScheduleChanges(ObservableList<Map<String, String>> changedData) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("UPDATE_SCHEDULE");
            writer.println(manager.getMid());

            for (Map<String, String> item : changedData) {
                String courseId = item.get("课程号");
                String newTime = item.get("时间");
                String newClassroom = item.get("教室");
                // 发送更新命令到服务器
                writer.println(courseId + "|" + newTime + "|" + newClassroom);
            }

            new Alert(Alert.AlertType.INFORMATION, "课表更新成功").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "保存失败：" + e.getMessage()).show();
        }
    }


    private ObservableList<Map<String, String>> convertTeachersToData() {
        ObservableList<Map<String, String>> teacherData = FXCollections.observableArrayList();
        for (Teacher teacher : teachers) {
            Map<String, String> item = new HashMap<>();
            item.put("工号", teacher.getTid().toString());
            item.put("姓名", teacher.getUserName());
            teacherData.add(item);
        }
        return teacherData;
    }

    private ObservableList<Map<String, String>> convertCoursesToData() {
        ObservableList<Map<String, String>> courseData = FXCollections.observableArrayList();
        for (Course course : courses) {
            Map<String, String> item = new HashMap<>();
            item.put("课程号", course.getCourseId().toString());
            item.put("名称", course.getCourseName());
            item.put("学分", String.valueOf(course.getCousePoint()));
            courseData.add(item);
        }
        return courseData;
    }

    private ObservableList<Map<String, String>> convertClassroomsToData() {
        ObservableList<Map<String, String>> classroomData = FXCollections.observableArrayList();
        for (ClassRoom room : classrooms) {
            Map<String, String> item = new HashMap<>();
            item.put("教室号", room.getCid().toString());
            item.put("容量", String.valueOf(room.getVolume()));
            item.put("类型", room.getClassName().toString());
            classroomData.add(item);
        }
        return classroomData;
    }
    // 冲突检测模块
    private void showConflictCheck(ActionEvent event) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        // 冲突模拟数据
        String[][] conflicts = {
                {"教师冲突", "王老师在周一 8:00 同时有Java编程和算法设计", "调整Java编程到周三, 调整算法设计到周四"},
                {"教室冲突", "A201在周一 8:00 被两门课程占用", "将数据库课程调整到B305"},
                {"学生冲突", "学生1001在同一时间选修两门课程", "建议调整数据库课程时间"}
        };

        for (String[] conflict : conflicts) {
            HBox item = new HBox(10);
            Label lbl = new Label(conflict[0] + "：" + conflict[1]);
            ComboBox<String> solutions = new ComboBox<>();
            solutions.getItems().addAll(conflict[2].split(", "));
            Button btnApply = new Button("应用方案");
            btnApply.setOnAction(e -> showApplyAlert(solutions.getValue()));

            item.getChildren().addAll(lbl, solutions, btnApply);
            container.getChildren().add(item);
        }

        contentPane.getChildren().setAll(new ScrollPane(container));
    }


    // 构建可编辑表格
    private TableView<Map<String, String>> buildEditableTable(ObservableList<Map<String, String>> data) {
        TableView<Map<String, String>> table = new TableView<>();

        if (!data.isEmpty()) {
            for (String key : data.get(0).keySet()) {
                TableColumn<Map<String, String>, String> col = new TableColumn<>(key);
                col.setCellValueFactory(param ->
                        new SimpleStringProperty(param.getValue().get(key)));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                table.getColumns().add(col);
            }
        }

        table.setItems(data);
        table.setEditable(true);
        return table;
    }

    private Button createNavButton(String text, EventHandler<ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setPrefWidth(120);
        btn.setOnAction(handler);
        return btn;
    }

    // 数据按钮创建方法
    private Button createDataButton(String text, final String dataType) {
        Button btn = new Button(text);
        btn.setOnAction(e -> {
            currentDataType = dataType; // 更新当前数据类型
            refreshTable();
        });
        return btn;
    }
    private void applySolution(String conflictType, String solution) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("APPLY_SOLUTION");
            writer.println(manager.getMid());
            writer.println(conflictType + "|" + solution);

            // 重新加载数据
            loadAllData();
            new Alert(Alert.AlertType.INFORMATION, "方案已应用并刷新数据").show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "应用失败：" + e.getMessage()).show();
        }
    }

    // 数据加载方法
    private void loadAllData() throws IOException {
        students = GetStudentList();
        teachers = GetTeacherList();
        courses = GetCourseList();
        classrooms = GetClassRoomList();
    }
    private void showSaveAlert() {
        new Alert(Alert.AlertType.INFORMATION, "模拟保存成功").show();
    }

    private void showApplyAlert(String solution) {
        new Alert(Alert.AlertType.INFORMATION, "已应用方案：" + solution).show();
    }

}
