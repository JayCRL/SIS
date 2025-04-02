# SIS

## 基于JavaFX学生信息管理系统

分为

学生端

教师端

管理员端

## 项目结构说明

#### 	Resources

#### 		数据表

​			User_Students.txt： 存放学生登录信息 分别是：					    

​            						Uid(Long),name(String),password,sex(Long),phonenumber(Long) 

​			User_Teachers.txt:  存放老师登录信息

​									Sid(Long),name(String),password,sex(Long),phonenumber(Long) 

​	   别的基本表信息可以参照DBMS的init函数里面的导入数据部分解析的部分查看

#### 	     图片文件：

​				看下英文名就知道是什么文件了

#### 		 style.css:

​				布局优化的美化css文件 优化组件样式

## 代码逻辑：

#### 			     Entity：

   					定义的都是实体类 

   					并重写了toString 也写了根据String字符串解析函数 有的有多个一起解析的函数有的没有  

​       				继承关系：

​					   登陆类：

​					   UserEntity: username,password

​										Teacher

​										Student

​										Manager											

​						信息类独立不继承

#### 				Relation:

​						关系信息类 就是数据库里面的关系链接表

​						也有toString和解析方法 Parse

#### 				DBMS:

​						先Init每个信息表并保存在本地的HashMap里面

​								 其中Student存的时候做了类哈希函数处理 涉及相关Key操作的时候就也要用哈希函数

​						其它的操作都是基于在运行时存储的HashMap里面增删改查

​						保存本地逻辑：暂时还是直接遍历 并控制输出格式并在Updata里面修改后直接存储了

​						排课函数和课程冲突检测函数还没写，，，只是模拟的写上去的 

​																									具体还得自己参照所有的实现 不过也不难

#### 				Service:

​							直接起一个ServerSocket并与客户端连接获取socket

​							网络请求范式：	1.操作名

​															2.数据

​							按每行区分的 直接解析的String

​							具体操作直接调用dbms方法

#### 					Client:

​							起一个socket 造一个UI 自动登陆和保存密码还没写 不过也不难

​							连接的时候解析服务端发来的数据并启动对应的界面

#### 			    	 三个MainStage(操作界面)：

​							UI与获取数据接口分离 

​							只剩管理端的解决冲突 删除数据和教师端的评分功能没具体实现前端交互写好了的

​				总体还有未完成的地方： 课程冲突检测 信息删除 评论新建 总体安排课 

​							完成进度95%吧 实在有点重复性工作了 那就先这样了

 							

​							

## 快速开始

### Maven导入JavaFX

```xml
  <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>17.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>17.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-web</artifactId>
            <version>17.0.1</version> <!-- 使用适合你的项目的版本 -->
        </dependency>
        <dependency>
            <groupId>org.controlsfx</groupId>
            <artifactId>controlsfx</artifactId>
            <version>11.1.2</version>
        </dependency>

        <dependency>
            <groupId>com.dlsc.formsfx</groupId>
            <artifactId>formsfx-core</artifactId>
            <version>11.6.0</version>
            <exclusions>
                <exclusion>
                    <groupId>org.openjfx</groupId>
                    <artifactId>*</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.12.3</version>
        </dependency>
        <dependency>
            <groupId>org.kordamp.bootstrapfx</groupId>
            <artifactId>bootstrapfx-core</artifactId>
            <version>0.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
```

### 启动service

### 启动client



## 一些想法

> 运用现代化前后端分离思想人工用java手动实现前后端分离 服务器和客户端
> IO流模拟网络请求…
> 正则表达式和java原生String类的函数实现模拟json的数据解析
> 不用数据库那只能人工实现一个DBMS…
> 花了很久很久写了几十个函数…
> 运用关系型数据库思想管理数据Entity（实体类）和联系（relation）
> 逻辑蓝图与具体实现之间隔着巨大的鸿沟和努力
> 每一个轻而易举点击背后都有无数IT工作者过往的付出和辛苦努力
> 当现代技术直接用原始编程语言模拟功能
> 便捷性和前人的努力触及动容
> 重复性的工作及其复杂性仿佛回到了几十年前的模样
> 承接好技术发展的车轮并将其继续推向前方
> 

```
 Computer is ancient

 Software  is young

 Chores makes tired

 Core   brings  excitement
```

# Just For Fun

 ![img](https://img0.baidu.com/it/u=2009540241,2568963939&fm=253&fmt=auto&app=138&f=JPEG?w=814&h=500) 

​																																						

​																																							JSPV

​																																										2025/4/2