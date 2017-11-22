package uagrm.promoya.Model.Notification.SenderTopic;

/**
 * Created by Shep on 21/11/2017.
 */

public class Data {
    String message;

    public Data() {
    }

    public Data(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Data{" +
                "message='" + message + '\'' +
                '}';
    }
}
