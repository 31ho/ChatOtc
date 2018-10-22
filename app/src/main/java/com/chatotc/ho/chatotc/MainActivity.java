package com.chatotc.ho.chatotc;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.chatotc.ho.chatotc.fragment.AccountFragment;
import com.chatotc.ho.chatotc.fragment.ChatFragment;
import com.chatotc.ho.chatotc.fragment.PeopleFragment;
import com.chatotc.ho.chatotc.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainActivity_bottom_navigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_people :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new PeopleFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.action_account :
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new AccountFragment()).commit();
                        return true;
                }
                return false;
            }
        });

        passPushTokenToServer();
    }

    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", token);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .updateChildren(map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 30 && resultCode == RESULT_OK) {
            Log.d("확인2", data.getData().toString());
            String photoUri = getPath(data.getData());
            Uri file = Uri.fromFile(new File(photoUri));
            final StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("userImages").child(file.getLastPathSegment());
            final UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return riversRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                               String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                Map<String,Object> stringObjectMap = new HashMap<>();

                                stringObjectMap.put("profileImageUrl", downloadUri.toString());
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(uid)
                                        .updateChildren(stringObjectMap);

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                }
            });
        }
    }

    public String getPath(Uri uri){



        String [] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);



        Cursor cursor = cursorLoader.loadInBackground();

        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);



        cursor.moveToFirst();



        return cursor.getString(index);



    }
}
