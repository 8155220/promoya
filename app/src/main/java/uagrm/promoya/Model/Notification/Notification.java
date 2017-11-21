package uagrm.promoya.Model.Notification;

/**
 * Created by Shep on 10/29/2017.
 */

public class Notification {
    public String title;
    public String body;
    public String tag;

    public Notification(String title, String body,String tag) {
        this.title = title;
        this.body = body;
        this.tag= tag;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
