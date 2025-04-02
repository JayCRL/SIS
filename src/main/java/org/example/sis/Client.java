package org.example.sis;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.example.sis.Entity.*;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Client extends Application {
    private Button selectedButton = null;
    //输入框
    private TextField usernameField = new TextField();
    //密码输入框
    private PasswordField passwordField = new PasswordField();
    private CheckBox rememberPass = new CheckBox("记住密码");
    private CheckBox autoLogin = new CheckBox("自动登录");

    int mode;
    boolean IfRememberPassWord;
    boolean IfAutoLogin;

    @Override
    public void start(Stage primaryStage) throws IOException {
        HBox root = new HBox();
        root.setPrefSize(1000, 600);

        VBox leftPane = createLeftPane();
        VBox rightPane = createRightPane(primaryStage);
        root.getChildren().addAll(leftPane, rightPane);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.getIcons().add(new Image("/logo.png"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        enableWindowDrag(root, primaryStage);
    }
    //左侧面板
    private VBox createLeftPane() {
        VBox leftPane = new VBox();
        leftPane.setPrefWidth(600);

        // 创建StackPane用于层叠布局
        StackPane stackPane = new StackPane();

        // 背景图
        ImageView bgImageView = new ImageView(new Image(getClass().getResourceAsStream("/background.png")));
        bgImageView.setFitWidth(600);
        bgImageView.setFitHeight(600);

        // Logo图标
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
        logoView.setFitWidth(80);  // 根据实际logo尺寸调整
        logoView.setFitHeight(80);
        logoView.setPreserveRatio(true);

        // 将Logo定位在左上角
        StackPane.setAlignment(logoView, Pos.TOP_LEFT);
        StackPane.setMargin(logoView, new Insets(20, 0, 0, 20)); // 上20 左20边距
        stackPane.getChildren().addAll(bgImageView, logoView);
        leftPane.getChildren().add(stackPane);
// 在创建logoView后添加
        logoView.getStyleClass().add("logo");

// 如果需要点击事件
        logoView.setOnMouseClicked(e -> {
            System.out.println("Logo被点击");
            // 可以添加返回首页等逻辑
        });
        return leftPane;
    }

    //右侧面板
    private VBox createRightPane(Stage stage) {
        VBox rightPane = new VBox(30);
        rightPane.setPrefWidth(400);
        rightPane.setStyle("-fx-background-color: #ffffff;");

        // Logo图标
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/close.png")));
        logoView.setFitWidth(40);  // 根据实际logo尺寸调整
        logoView.setFitHeight(40);
        HBox ActionLogo=new HBox(5);
        ActionLogo.setAlignment(Pos.TOP_RIGHT);
        ActionLogo.getChildren().add(logoView);
        logoView.setOnMouseClicked(e->{
            stage.close();
        });


        HBox roleButtons = createRoleButtons();
        GridPane form = createLoginForm();
        HBox actionButtons = createActionButtons();
        HBox bottomArea = createBottomArea();


        logoView.setPreserveRatio(true);
        rightPane.getChildren().addAll(ActionLogo);
        rightPane.setPadding(new Insets(10, 10, 40, 0));
        rightPane.getChildren().addAll(roleButtons, form, actionButtons, bottomArea);
        return rightPane;
    }
    //创建复选按钮组
    private HBox createRoleButtons() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        Button studentBtn = createRoleButton("学生", "student.png");
        Button teacherBtn = createRoleButton("教师", "teacher.png");
        Button adminBtn = createRoleButton("管理员", "manager.png");
        studentBtn.setOnMouseClicked(e->{
            mode = 1;
            handleButtonSelection(studentBtn);
            System.out.println("Student Selected mode:"+mode);
        });
        teacherBtn.setOnMouseClicked(e->{
            mode = 2;
            handleButtonSelection(teacherBtn);
            System.out.println("Teacher Selected mode:"+mode);
        });
        adminBtn.setOnMouseClicked(e->{
            mode = 3;
            handleButtonSelection(adminBtn);
            System.out.println("Admin   Selecetd mode:"+mode);
        });
        buttonBox.getChildren().addAll(studentBtn, teacherBtn, adminBtn);
        return buttonBox;
    }
    //设置单选框
    private Button createRoleButton(String text, String iconName) {
        Button button = new Button(text);
        button.getStyleClass().add("role-btn");
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/" + iconName)));
        icon.setFitWidth(32);
        icon.setFitHeight(32);
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.TOP);
        //设置选中状态
        button.setOnAction(e -> handleButtonSelection(button));
        return button;
    }

    //设置选中状态
    private void handleButtonSelection(Button selected) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
        }
        selected.getStyleClass().add("selected");
        selectedButton = selected;
    }

    //右侧面板
    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(15);
        grid.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("账号:");
        usernameLabel.getStyleClass().add("form-label");
        usernameField.getStyleClass().add("form-field");
        usernameField.setPrefWidth(250);

        Label passwordLabel = new Label("密码:");
        passwordLabel.getStyleClass().add("form-label");
        passwordField.getStyleClass().add("form-field");
        passwordField.setPrefWidth(250);

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        return grid;
    }
    //设置底部按钮
    private HBox createActionButtons() {
        //初始化并设置位置
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        //设置重置按钮的样式和事件监听
        Button resetBtn = new Button("重置");
        resetBtn.getStyleClass().add("action-btn");
        resetBtn.setOnAction(e -> {
            usernameField.clear();
            passwordField.clear();
        });
        //设置登录按钮的样式和时间监听
        Button loginBtn = new Button("登录");
        loginBtn.getStyleClass().add("action-btn");
        loginBtn.setOnAction(e -> handleLogin());
        buttonBox.getChildren().addAll(resetBtn, loginBtn);
        return buttonBox;
    }

    //登陆面板底部区域
    private HBox createBottomArea() {
        HBox bottomArea = new HBox();
        bottomArea.setPadding(new Insets(40, 40, 40, 50));
        VBox checkBoxes = new VBox(10);
        RememberPassword();
        AutoLogin();
        checkBoxes.getChildren().addAll(rememberPass, autoLogin);
        checkBoxes.getStyleClass().add("check-boxes");
        Label forgotPassword = new Label("找回密码");
        forgotPassword.getStyleClass().add("forgot-pwd");
        forgotPassword.setOnMouseClicked(e -> showForgotPasswordDialog());
        HBox.setHgrow(checkBoxes, Priority.ALWAYS);
        bottomArea.getChildren().addAll(checkBoxes, forgotPassword);
        return bottomArea;
    }
    //记住密码
    private void RememberPassword(){
        rememberPass.setOnMouseClicked(e -> {
            //如果被选中
            IfRememberPassWord=rememberPass.isSelected();
            System.out.println("RememberPassWord:"+rememberPass.isSelected());
        });
    }
    //自动登陆
    private void AutoLogin(){
        autoLogin.setOnMouseClicked(e -> {
            //如果被选中
            IfAutoLogin=autoLogin.isSelected();
            System.out.println("IfAutoLogin:"+autoLogin.isSelected());
        });
    }
    //登录逻辑
    private void handleLogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectMapper objectMapper = new ObjectMapper();
                File jsonFile = new File("src/main/resources/package.json");
                PackageConfig packageConfig = null;
                UserEntity userEntity=null;
                try {
                    //导入配置
                    packageConfig = objectMapper.readValue(jsonFile, PackageConfig.class);
                    int port = packageConfig.getPort();
                    //连接服务器
                    Socket socket=new Socket("localhost",port);
                    System.out.println("Connecting to");
                    //读入读出流
                    PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printWriter.println("log in");
                    printWriter.println(",mode:"+mode);
                    printWriter.println(",username:"+usernameField.getText());
                    printWriter.println(",password:"+passwordField.getText());
                    String message;
                    ArrayList<String> fields = new ArrayList<String>();
                    while(!(message=bufferedReader.readLine()).equals("ok")){
                        System.out.println(message);
                        fields.add(message);
                   }
                    if (fields.get(0).equals("success")) { // 根据实际服务器响应判断
                        Platform.runLater(() -> {
                            // 关闭登录窗口
                            ((Stage)usernameField.getScene().getWindow()).close();
                            if(fields.get(1).equals("Get Student Successfully")){
                                new StudentMainStage(socket,fields.get(2)).show();
                            }else if(fields.get(1).equals("Get Teacher Successfully")){
                                String result=fields.get(2);
                                result=result.substring(8,result.length()-1);
                                Teacher teacher=new Teacher();
                                String[] kv=result.split(", ");
                                for (String k : kv) {
                                    String key=k.split("=",2)[0];
                                    String value=k.split("=",2)[1];
                                    switch (key){
                                        case "tid":
                                            teacher.setTid(Long.parseLong(value));
                                            break;
                                        case "username":
                                            teacher.setUsername(value);
                                            break;
                                        case "phonenumber":
                                            teacher.setPhonenumber(Long.parseLong(value));
                                            break;
                                    }
                                }
                                try {
                                    new TeacherMainStage(socket,teacher).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }else if(fields.get(1).equals("Get Manager Successfully")){
                                try {
                                    new ManagerMainStage(socket,Long.parseLong(fields.get(2)));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        });
                    }
                    //登录成功

                } catch (IOException e) {
                    Platform.runLater(() ->
                            showAlert("登录失败", "连接服务器失败"));
                }

            }
        }).start();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // 找回密码逻辑
    private void showForgotPasswordDialog() {
        getHostServices().showDocument("https://127.0.0.1");
    }
    //允许拖拽
    private void enableWindowDrag(Region region, Stage stage) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];
        //获取鼠标在窗体中的相对位置
        region.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        //用鼠标的位置减去相对位置就是窗体应该在的位置
        region.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
    // 学生主界面

}