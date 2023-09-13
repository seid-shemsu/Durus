package ethio.islamic.durus.courses;

import java.io.Serializable;

public class CourseObject implements Serializable {
    String course_name, img_url;

    public CourseObject(String course_name, String img_url) {
        this.course_name = course_name;
        this.img_url = img_url;
    }


    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
