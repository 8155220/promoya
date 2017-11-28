package uagrm.promoya.Common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uagrm.promoya.Model.Store;
import uagrm.promoya.Model.User;

/**
 * Created by Shep on 11/19/2017.
 */

public class FirebaseDatabaseHelper {
    public static ArrayList<User> users;
    //public static ArrayList<Store> stores;
    public static ArrayList<Long> likes;
    public static ArrayList<Long> views;
    public static ArrayList<Long> subscription;
    static FirebaseDatabaseHelper instance=null;
    public final static String STATISTICS="statistics";
    public final static String LIKES="likes";
    public final static String VIEWS="views";
    public final static String SUBSCRIPTION="subscriptions";
    private FirebaseDatabaseHelper() {
        users =new ArrayList<>();
        //stores =new ArrayList<>();
        likes =new ArrayList<>();
        views =new ArrayList<>();
        subscription =new ArrayList<>();
        retrieveUsers();
        retrieveStatisticsLikes();
        retrieveStatisticsViews();
        retrieveStatisticsSubscription();
    }

    private void retrieveStatisticsSubscription() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(STATISTICS).child(SUBSCRIPTION).child(Common.currentUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    fetchSubscriptionData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchSubscriptionData(DataSnapshot dataSnapshot) {
        subscription.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Long a = ds.getValue(Long.class);
            System.out.println("SUBSCRIPTION :" + a);
            subscription.add(a);
        }
    }

    private void retrieveStatisticsViews() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(STATISTICS).child(VIEWS).child(Common.currentUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchViewsData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchViewsData(DataSnapshot dataSnapshot) {
        views.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Long a = ds.getValue(Long.class);
            views.add(a);
        }
    }


    public void retrieveUsers() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {fetchUserData(dataSnapshot);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void fetchUserData(DataSnapshot dataSnapshot) {
        users.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            User usuario=ds.getValue(User.class);
            users.add(usuario);
        }
    }

    public void retrieveStatisticsLikes()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(STATISTICS).child(LIKES).child(Common.currentUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    fetchLikesData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchLikesData(DataSnapshot dataSnapshot) {
        likes.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Long a = ds.getValue(Long.class);
            likes.add(a);
        }
    }


    public  static FirebaseDatabaseHelper getInstance() {
        if(instance == null) {
            instance = new FirebaseDatabaseHelper();
        }
        return instance;
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static User getUser(String uId){
        ArrayList<User> lista = FirebaseDatabaseHelper.getUsers();
        for (int i = 0; i < lista.size(); i++) {
            User user = lista.get(i);
            if(user.getUid().equals(uId))
                return user;
        }
        return null;
    }
}
