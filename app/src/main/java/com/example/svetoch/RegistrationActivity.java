package com.example.svetoch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svetoch.Const.StringConst;
import com.example.svetoch.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Timer;
import java.util.TimerTask;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    Button btnReg;

    RelativeLayout root, root_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //отображение стрелочки "Назад"
        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();      //запускается авторизация
        db = FirebaseDatabase.getInstance();    //подключение к БД
        users = db.getReference("Users"); //с какой таблицей работаем

        btnReg = findViewById(R.id.button_registration);
        root = findViewById(R.id.root_registration_activity);
        root_main = findViewById(R.id.root_main_activity);

        registrationOfUser();
    }

    private void registrationOfUser() {
        final MaterialEditText email = this.findViewById(R.id.editText_main_email);
        final MaterialEditText phone = this.findViewById(R.id.editText_phone);
        final MaterialEditText name = this.findViewById(R.id.editText_name);
        final MaterialEditText surname = this.findViewById(R.id.editText_surname);
        final MaterialEditText pass = this.findViewById(R.id.editText_main_password);
        final MaterialEditText rep_pass = this.findViewById(R.id.editText_repeat_password);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString())) {
                    checkingForErrors(StringConst.error_input_name);
                    //Snackbar.make(root, StringConst.error_input_name, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(surname.getText().toString())) {
                    checkingForErrors(StringConst.error_input_surname);
                    //Snackbar.make(root, StringConst.error_input_surname, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(phone.getText().toString())) {
                    checkingForErrors(StringConst.error_input_phone);
                    //Snackbar.make(root, StringConst.error_input_phone, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email.getText().toString())) {
                    checkingForErrors(StringConst.error_input_email);
                    //Snackbar.make(root, StringConst.error_input_email, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(pass.getText().toString().length() < 6) {
                    checkingForErrors(StringConst.error_input_password);
                    //Snackbar.make(root, StringConst.error_input_password, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(rep_pass.getText().toString())) {
                    checkingForErrors(StringConst.error_no_input_repeat_password);
                    //Snackbar.make(root, StringConst.error_no_input_repeat_password, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(!pass.getText().toString().equals(rep_pass.getText().toString())) {
                    checkingForErrors(StringConst.error_input_repeat_password);
                    //Snackbar.make(root, StringConst.error_input_repeat_password, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //регистрация пользователя
                auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            User user = new User();
                            user.setName(name.getText().toString());
                            user.setSurname(surname.getText().toString());
                            user.setPhone(phone.getText().toString());
                            user.setEmail(email.getText().toString());
                            user.setPass(pass.getText().toString());

                            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //Snackbar.make(root, StringConst.add_user_to_BD, Snackbar.LENGTH_SHORT).show();

                                            TimerTask task = new TimerTask() {
                                                public void run() {
                                                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            };
                                            Timer timer = new Timer();
                                            timer.schedule(task, 1000);     //задваивается экран, если нажать стрелочку назад
                                        }
                                    });
                        }
                    });
            }
        });
    }

    private void checkingForErrors(String text_error) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.failed_toast,
                (ViewGroup) findViewById(R.id.failed_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(text_error);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 155);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    //логика работы стрелочки "Назад"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
