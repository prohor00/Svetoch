package com.example.svetoch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.svetoch.Const.StringConst;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Button button_registration, button_sign_in;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();      //запускается авторизация
        db = FirebaseDatabase.getInstance();    //подключение к БД
        users = db.getReference("Users"); //с какой таблицей работаем


        button_registration = findViewById(R.id.button_registration);
        button_sign_in = findViewById(R.id.button_sign_in);
        root = findViewById(R.id.root_main_activity);

        addListenerOnButton();
        signInUser();
    }

    //работа кнопки "Регистрация"
    public void addListenerOnButton () {
        button_registration.setOnClickListener(
                new View.OnClickListener() {
                    //отрисовка страницы регистрации
                    @Override
                    public void onClick (View v) {
                        Intent intent = new Intent("com.example.svetoch.RegistrationActivity");
                        startActivity(intent);
                    }
                }
        );
    }

    private void signInUser() {
        final MaterialEditText email = this.findViewById(R.id.editText_main_email);
        final MaterialEditText pass = this.findViewById(R.id.editText_main_password);

        button_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, StringConst.error_input_email, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(pass.getText().toString().length() < 6) {
                    Snackbar.make(root, StringConst.error_input_password, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {     //успешная авторизация
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {               //зафейлиная авторизация
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Ошибка авторизации!", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
