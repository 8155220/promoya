package uagrm.promoya.Model.Notification.SenderTopic;

/**
 * Created by Shep on 21/11/2017.
 */

public class TopicResponse {
    String message_id;
    String error;

    public TopicResponse() {
    }

    public TopicResponse(String message_id, String error) {
        this.message_id = message_id;
        this.error = error;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
