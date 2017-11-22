package uagrm.promoya.Model.Notification.SenderTopic;

/**
 * Created by Shep on 21/11/2017.
 */

public class Data {
    String title;
    String body;
    String image;

    public Data() {
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
