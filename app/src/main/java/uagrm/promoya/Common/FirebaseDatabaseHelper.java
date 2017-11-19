package uagrm.promoya.Common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uagrm.promoya.Model.User;

/**
 * Created by Shep on 11/19/2017.
 */

public class FirebaseDatabaseHelper {
    private static ArrayList<User> users;
    static FirebaseDatabaseHelper instance=null;
    private FirebaseDatabaseHelper() {
        users =new ArrayList<>();
        retrieveUsers();
    }

    private void fetchUserData(DataSnapshot dataSnapshot)
    {
        users.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            User usuario=ds.getValue(User.class);
            System.out.println("EN FECTHUSERDATA :"+usuario.toString());
            users.add(usuario);
        }
    }
    public void retrieveUsers()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    fetchUserData(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            System.out.println("LISTASIZE :"+lista.size());
            System.out.println("usuarioForId :"+ user.getUid());
            System.out.println("usuarioParametro :"+ uId);
            if(user.getUid().equals(uId))
                return user;
        }
        return null;
    }
}
