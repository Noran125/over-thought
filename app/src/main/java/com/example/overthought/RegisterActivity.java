package com.example.overthought;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText username = findViewById(R.id.username);
        EditText fullname = findViewById(R.id.fullname);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        Button register = findViewById(R.id.register);

        TextView login = findViewById(R.id.txt_login);


        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String atr_username = username.getText().toString();
                String atr_fullname = fullname.getText().toString();
                String atr_email = email.getText().toString();
                String atr_password = password.getText().toString();

                if (TextUtils.isEmpty(atr_username) || TextUtils.isEmpty(atr_fullname) || TextUtils.isEmpty(atr_email)
                        || TextUtils.isEmpty(atr_password)){
                    Toast.makeText(RegisterActivity.this, "all fields are required", Toast.LENGTH_SHORT).show();
                } else if (atr_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    register(atr_username, atr_fullname, atr_email, atr_password);
                }
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("please wait...");
                pd.show();
            }
        });

    }

    private void register(String username, String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/overthought-d52f4.appspot.com/o/profile%20icon.png?alt=media&token=6aaa7ee1-66ca-4c70-8d46-896e47a7ca67");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "you can't register with this e-mail or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}