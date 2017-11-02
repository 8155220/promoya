package uagrm.promoya.Model;

/**
 * Created by Shep on 10/17/2017.
 */

public class Store {
    String backgroundImgUrl;
    String description;
    String displayName;
    String logoImgUrl;
    String storeId;
    String title;

    public Store() {
    }

    public String getBackgroundImgUrl() {
        return backgroundImgUrl;
    }

    public void setBackgroundImgUrl(String backgroundImgUrl) {
        this.backgroundImgUrl = backgroundImgUrl;
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
