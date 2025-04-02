package org.example.sis.Entity;

public class Course {
    Long courseId;
    String courseName;
    int courseHour;
    int cousePoint;
    CourseTime courseTime;
    //构造函数
    public Course(Long courseid, String course, int coursehour, int cousePoint,String courseTime) {
        this.courseId=courseid;
        this.courseName=course;
        this.courseHour=coursehour;
        this.cousePoint=cousePoint;
        this.courseTime=new CourseTime(courseTime);
    }
    public static Course parseCourse(String str) {
        try {
            // 去除字符串首尾的"Course{"和"}"
            String content = str.substring(7, str.length() - 1);
            // 分割各个键值对
            String[] parts = content.split(", ");

            Long courseId = null;
            String courseName = null;
            int courseHour = 0;
            int cousePoint = 0;
            int startWeek = 0;
            int endWeek = 0;
            int dayOfWeek = 0;
            int classNum = 0;

            for (String part : parts) {
                String[] keyValue = part.split("=", 2);
                if (keyValue.length != 2) continue;
                if(keyValue[0].equals("{courseId")){
                    keyValue[0]="courseId";
                }
                if(keyValue[0].equals("Id")){
                    keyValue[0]="courseId";

                }
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                switch (key) {
                    case "courseId":
                        courseId = Long.parseLong(value);
                        break;
                    case "courseName":
                        // 去除单引号
                        courseName = value.substring(1, value.length() - 1);
                        break;
                    case "courseHour":
                        courseHour = Integer.parseInt(value);
                        break;
                    case "cousePoint":
                        cousePoint = Integer.parseInt(value);
                        break;
                    case "courseTime":
                        // 解析课程时间部分
                        String[] timeParts = value.split(",");
                        if (timeParts.length != 3) {
                            throw new IllegalArgumentException("Invalid courseTime format: " + value);
                        }
                        // 处理周数部分
                        String weekPart = timeParts[0].trim().replace("周", "");
                        String[] weeks = weekPart.split("-");
                        startWeek = Integer.parseInt(weeks[0]);
                        endWeek = Integer.parseInt(weeks[1]);
                        // 处理星期几
                        dayOfWeek = Integer.parseInt(timeParts[1].trim().replace("周", ""));
                        // 处理节数
                        classNum = Integer.parseInt(timeParts[2].trim().replace("节", ""));
                        break;
                }
            }

            // 构造符合CourseTime构造参数的字符串
            String courseTimeStr = String.format("%d-%d周,周%d,%d节", startWeek, endWeek, dayOfWeek, classNum);
            // 创建Course对象
            return new Course(courseId, courseName, courseHour, cousePoint, courseTimeStr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse course from string: " + str, e);
        }
    }

    public Long getId() {
        return courseId;
    }

    class CourseTime{
        //开始周
        int startWeek;
        //结束周
        int endWeek;
        //星期几
        int dayOfWeek;
        //第几节课
        int classNum;
        CourseTime(int startWeek,int endWeek,int dayOfWeek,int classNum){
            this.startWeek=startWeek;
            this.endWeek=endWeek;
            this.dayOfWeek=dayOfWeek;
            this.classNum=classNum;
        }

         CourseTime(String courseTime) {
            String[] split = courseTime.split(",");
            startWeek = Integer.parseInt(split[0].split("-")[0].trim());
            endWeek = Integer.parseInt(split[0].split("-")[1].substring(0,split[0].split("-")[1].length()-1));
            dayOfWeek = Integer.parseInt(split[1].substring(1).trim());
            classNum = Integer.parseInt(split[2].substring(0,split[2].length()-1).trim());
        }
        @Override
        public String toString() {
            return  startWeek+"-"
                     +endWeek +"周"+",周"
                +dayOfWeek +","+
                    classNum +
                    '节';
        }
    }
    public Long getCourseId(){
        return courseId;
    }
    public String getCourseName(){
        return courseName;
    }
    public int getCourseHour(){
        return courseHour;
    }
    public int getCousePoint(){
        return cousePoint;
    }
    public String getCourseTime(){
        return courseTime.toString();
    }
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseHour=" + courseHour +
                ", cousePoint=" + cousePoint +
                ", courseTime=" + courseTime +
                '}';
    }
}
