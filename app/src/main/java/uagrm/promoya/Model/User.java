package uagrm.promoya.Model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Shep on 11/12/2017.
 */

public class User {
    String displayName
            ,email
            ,photoUrl
            ,uid
            ,token
            ,storeName;
    int     hasStore;


    public User() {
    }

    public User(String displayName, String email, String photoUrl, String uid, String token, int hasStore) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.uid = uid;
        this.token = token;
        this.hasStore = hasStore;
    }

    public User(FirebaseUser firebaseUser){
        this.displayName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
        this.photoUrl = firebaseUser.getPhotoUrl().toString();
        this.token = FirebaseInstanceId.getInstance().getToken();
        this.uid = firebaseUser.getUid();
        this.hasStore=0;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getHasStore() {
        return hasStore;
    }

    public void setHasStore(int hasStore) {
        this.hasStore = hasStore;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", hasStore=" + hasStore +
                '}';
    }
}
