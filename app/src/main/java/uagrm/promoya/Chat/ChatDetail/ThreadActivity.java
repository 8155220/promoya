package uagrm.promoya.Chat.ChatDetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import uagrm.promoya.Chat.BaseActivity;
import uagrm.promoya.Chat.HomeChat.EmptyStateRecyclerView;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Model.Message;
import uagrm.promoya.Model.Notification.Notification;
import uagrm.promoya.Model.User;
import uagrm.promoya.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ThreadActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.activity_thread_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_thread_messages_recycler)
    EmptyStateRecyclerView messagesRecycler;
    @BindView(R.id.activity_thread_send_fab)
    FloatingActionButton sendFab;
    @BindView(R.id.activity_thread_input_edit_text)
    TextInputEditText inputEditText;
    @BindView(R.id.activity_thread_empty_view)
    TextView emptyView;
    @BindView(R.id.activity_thread_editor_parent)
    RelativeLayout editorParent;
    @BindView(R.id.activity_thread_progress)
    ProgressBar progress;

    private DatabaseReference mDatabase;

    @State
    String userUid;
    @State
    boolean emptyInput;

    private User user;
    private FirebaseUser owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (savedInstanceState == null) {
            userUid = getIntent().getStringExtra(Common.USER_ID_EXTRA);
        }
        sendFab.requestFocus();

        owner = Common.currentUser;
        loadUserDetails();
//        initializeAuthListener();
        initializeMessagesRecycler();
        initializeInteractionListeners();
    }

    private void initializeInteractionListeners() {
        inputEditText.addTextChangedListener(this);
    }



    private void loadUserDetails() {
        DatabaseReference userReference = mDatabase
                .child("users")
                .child(userUid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                initializeMessagesRecycler();
                displayUserDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ThreadActivity.this, R.string.error_loading_user, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initializeMessagesRecycler() {
        if (user == null || owner == null) {
            //Log.d("@@@@", "initializeMessagesRecycler: User:" + user + " Owner:" + owner);
            return;
        }
        Query messagesQuery = mDatabase
                .child("messages")
                .child(owner.getUid())
                .child(user.getUid())
                .orderByChild("negatedTimestamp");
        MessagesAdapter adapter = new MessagesAdapter(this, owner.getUid(), messagesQuery);
        messagesRecycler.setAdapter(null);
        messagesRecycler.setAdapter(adapter);
        messagesRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messagesRecycler.setEmptyView(emptyView);
        messagesRecycler.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                messagesRecycler.smoothScrollToPosition(0);
            }
        });
    }

    @OnClick(R.id.activity_thread_send_fab)
    public void onClick() {
        if (user == null || owner == null) {
            //Log.d("@@@@", "onSendClick: User:" + user + " Owner:" + owner);
            return;
        }
        long timestamp = new Date().getTime();
        long dayTimestamp = getDayTimestamp(timestamp);
        String body = inputEditText.getText().toString().trim();
        String ownerUid = owner.getUid();
        String userUid = user.getUid();
        Message message =
                new Message(timestamp, -timestamp, dayTimestamp, body, ownerUid, userUid);
        mDatabase
                .child("notifications")
                .child("messages")
                .push()
                .setValue(message);
        mDatabase
                .child("messages")
                .child(userUid)
                .child(ownerUid)
                .push()
                .setValue(message);
        if (!userUid.equals(ownerUid)) {
            mDatabase
                    .child("messages")
                    .child(ownerUid)
                    .child(userUid)
                    .push()
                    .setValue(message);
        }
        Notification notification = new Notification(
                owner.getDisplayName() +" (Mensaje)"
                ,message.getBody(),ownerUid);
        Common.sendNotification(user.getToken(),notification);
        System.out.println("uSERtOKEN :"+user.getToken());
        inputEditText.setText("");
    }

    @Override
    protected void displayLoadingState() {
        //was considering a progress bar but firebase offline database makes it unnecessary

        //TransitionManager.beginDelayedTransition(editorParent);
        progress.setVisibility(isLoading ? VISIBLE : INVISIBLE);
        //displayInputState();
    }

    private void displayInputState() {
        //inputEditText.setEnabled(!isLoading);
        sendFab.setEnabled(!emptyInput && !isLoading);
        //sendFab.setImageResource(isLoading ? R.color.colorTransparent : R.drawable.ic_send);
    }

    private long getDayTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    private void displayUserDetails() {
        //todo[improvement]: maybe display the picture in the toolbar.. WhatsApp style
        toolbar.setTitle(user.getDisplayName());
        //toolbar.setSubtitle(user.getEmail());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        emptyInput = s.toString().trim().isEmpty();
        displayInputState();
    }
}
