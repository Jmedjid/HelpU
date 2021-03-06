package fr.miage.paris10.projetm1.helpu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private String to_userName;
    private String destinataireID;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List messages = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intent = getIntent();
        Button btn_send;
        listView = (ListView) findViewById(R.id.listViewConversations);
        to_userName = intent.getStringExtra("user");
        setTitle(to_userName);
        adapter = new ArrayAdapter<String>(ChatRoomActivity.this,android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(adapter);
        btn_send = (Button) findViewById(R.id.btn_send);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference = databaseReference.child("/users");
        databaseReference.addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                        UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                                        if(userInformation.getCompletName().equals(to_userName)) {
                                                            destinataireID = dataSnapshot.getKey();
                                                        }
                                                    }

                                                    @Override
                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                    }

                                                    @Override
                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.champText_msg);
                String msg = editText.getText().toString();
                String from = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Message message = new Message(from,destinataireID,msg);
                FirebaseDatabase.getInstance()
                        .getReference().child("messages")
                        .push()
                        .setValue(message);
                editText.setText("");
                listView.setAdapter(adapter);
            }
        });
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference = databaseReference.child("/messages");
        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentlyUser = user.getUid();
                Message message = dataSnapshot.getValue(Message.class);
                //messages.add("Destinataire :"+destinataireID);
                //destinataireID = "WvVnW8sZt0UxBpRPUV4FABrYCFM2";
                if( (message.getFrom().equals(currentlyUser) && message.getDestinataire().equals(destinataireID)) ||
                        (message.getFrom().equals(destinataireID) && message.getDestinataire().equals(currentlyUser))  ){
                    messages.add(message.getMessage());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}