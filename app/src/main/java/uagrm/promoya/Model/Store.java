package uagrm.promoya.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shep on 10/17/2017.
 */

public class Store implements Serializable{
    String backgroundImgUrl;
    String description;
    String displayName;
    String logoImgUrl;
    String storeId;
    String title;
    public int subscriptionsCount;
    public Map<String, Boolean> subscriptions = new HashMap<>(); //cambiar nombre subscriptions->subscriptions

    public Store() {
    }

    public String getBackgroundImgUrl() {
        return backgroundImgUrl;
    }

    public void setBackgroundImgUrl(String backgroundImgUrl) {
        this.backgroundImgUrl = backgroundImgUrl;
    }

    public int getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public void setSubscriptionsCount(int subscriptionsCount) {
        this.subscriptionsCount = subscriptionsCount;
    }

    public Map<String, Boolean> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<String, Boolean> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLogoImgUrl() {
        return logoImgUrl;
    }

    public void setLogoImgUrl(String logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Store{" +
                "backgroundImgUrl='" + backgroundImgUrl + '\'' +
                ", description='" + description + '\'' +
                ", displayName='" + displayName + '\'' +
                ", logoImgUrl='" + logoImgUrl + '\'' +
                ", storeId='" + storeId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
