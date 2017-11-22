package uagrm.promoya.Model.Notification.SenderTopic;

/**
 * Created by Shep on 21/11/2017.
 */

public class SenderTopic {
    String to;
    Data data;

    public SenderTopic() {
    }

    public SenderTopic(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SenderTopic{" +
                "to='" + to + '\'' +
                ", data=" + data +
                '}';
    }
}
