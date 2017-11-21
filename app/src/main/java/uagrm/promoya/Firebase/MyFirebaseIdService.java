package uagrm.promoya.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import uagrm.promoya.Common.Common;

/**
 * Created by Shep on 10/28/2017.
 */

public class MyFirebaseIdService  extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if(Common.currentUser!=null)
            updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("users").child(Common.currentUser.getUid()).child("token");
        tokens.setValue(tokenRefreshed);
        //Token token = new Token(tokenRefreshed,false);
       // tokens.child(Common.currentUser.getPhone()).setValue(token);

    }
}
