package com.claimsysapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.claimsysapp.databaseClasses.userClass.User;
import com.claimsysapp.utility.DatabaseVariables;
import com.claimsysapp.utility.Globals;

import java.util.ArrayList;

/**
 * Класс для аутентификации пользователей.
 * @author Monarch
 */
public class SignInActivity extends AppCompatActivity {

    //region Fields

    private ArrayList<User> userList = new ArrayList<User>();

    private DatabaseReference databaseReference;

    private boolean isDownloaded;

    //endregion

    //region Composite Controls

    private Button signInBut;

    private EditText loginET;
    private EditText passwordET;

    private CheckBox rememberPasCB;

    private TextView signUpTV;

    private MaterialDialog loadingDialog;

    //endregion

    //region Listeners

    private ValueEventListener databaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            isDownloaded = false;

            userList = Globals.Downloads.Users.getUserList(dataSnapshot, false);

            isDownloaded = true;
            if (loadingDialog != null){
                closeLoadingDialog();
                signInBut.callOnClick();
                loadingDialog = null;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Ошибка в работе базы данных. Обратитесь к администратору компании или разработчику", Toast.LENGTH_LONG).show();
        }
    };

    //endregion

    //region Override Methods

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
            .title("Закрыть приложение")
            .content("Вы действительно хотите закрыть приложение?")
            .positiveText("Да")
            .negativeText("Нет")
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    savePassAndLogin();
                    SignInActivity.this.finishAffinity();
                }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.cancel();
                }
            })
            .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        dataConstruction();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePassAndLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePassAndLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        loginET.setText(settings.getString("Login",""));
        passwordET.setText(settings.getString("Password",""));
        rememberPasCB.setChecked(settings.getBoolean("cbState", false));
    }

    //endregion

    /**
     * Проверка полей логина и пароля, проверка подтверждения заявки на получение аккаунта.
     * @return true - если поля заполнены и аккаунт не находится на рассмотрении на добавление.
     * false - если хотя бы одно поле не заплонено
     */
    private boolean checkFields() {
        if (loginET.getText().toString().isEmpty()) {
            loginET.requestFocus();
            TextInputLayout loginLayout = (TextInputLayout) findViewById(R.id.login_layout);
            loginLayout.setErrorEnabled(true);
            loginLayout.setError(getResources().getString(R.string.empty_field));
            Globals.showKeyboardOnEditText(SignInActivity.this, loginET);
            return false;
        } else if (passwordET.getText().toString().isEmpty()) {
            passwordET.requestFocus();
            TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(getResources().getString(R.string.empty_field));
            Globals.showKeyboardOnEditText(SignInActivity.this, passwordET);
            return false;
        } else return true;
    }

    /**
     * Проверка правильности логина и соответствия пароля. При успешном сопоставлении выполняется
     * вход в систему.
     */
    private void checkVerificationData() {
        int i = 0;
        while (!loginET.getText().toString().equals(userList.get(i).getLogin())
                && ++i < userList.size()); //TODO binarySearch
        if (i >= userList.size()) {
            passwordET.setText("");
            Toast.makeText(getApplicationContext(), "Введенного логина не существует. " +
                    "Повторите попытку", Toast.LENGTH_LONG).show();
        }
        else if (loginET.getText().toString().equals(userList.get(i).getLogin()) &&
                passwordET.getText().toString().equals(userList.get(i).getPassword()))
            signIn(userList.get(i));
        else {
            passwordET.setText("");
            Toast.makeText(getApplicationContext(), "Логин и/или пароль введен неверно. Повторите попытку", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Закрытие окна загрузки.
     */
    private void closeLoadingDialog(){
        loadingDialog.dismiss();
    }

    /**
     * Назначение начальних данных и параметров программы.
     */
    private void dataConstruction(){
        initializeComponents();
        setEvents();
    }

    /**
     * Проверка наличия подключения к Интернету.
     * @return true - если подключение есть. false - если подключение отсутствует.
     */
    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager)SignInActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Инициализация переменных и элементов макета.
     */
    private void initializeComponents() {
        signInBut = (Button)findViewById(R.id.signInButton);

        loginET = (EditText)findViewById(R.id.loginET);
        passwordET = (EditText)findViewById(R.id.passwordET);

        signUpTV = (TextView) findViewById(R.id.signUp);

        rememberPasCB = (CheckBox)findViewById((R.id.checkBoxBold));

        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseVariables.Folders.DATABASE_ALL_USER_TABLE);

        isDownloaded = false;
    }

    /**
     * Сохранение данных логина и пароля для повторного входа.
     */
    private void savePassAndLogin(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        if (rememberPasCB.isChecked()) {
            String login = loginET.getText().toString();
            String password = passwordET.getText().toString();
            editor.putString("Login", login);
            editor.putString("Password", password);
            editor.putBoolean("cbState", true);
        }
        else
            editor.clear();
        editor.apply();
    }

    /**
     * Создание методов для событий.
     */
    private void setEvents() {
        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnection()) {
                    if (!isDownloaded) {
                        databaseReference.addValueEventListener(databaseListener);
                        showLoadingDialog();
                        return;
                    }
                    if (!checkFields())
                        return;
                    checkVerificationData();
                }
                else Toast.makeText(getApplicationContext(), "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            }
        });

        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (hasConnection()) {
                    if (!isDownloaded) {
                        databaseReference.addValueEventListener(databaseListener);
                        showLoadingDialog();
                        return true;
                    }
                    if (!checkFields())
                        return true;
                    checkVerificationData();
                }
                else Toast.makeText(getApplicationContext(), "Нет подключения к интернету", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        loginET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextInputLayout loginLayout = (TextInputLayout) findViewById(R.id.login_layout);
                loginLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
                passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    /**
     * Отображение окна загрузки.
     */
    private void showLoadingDialog(){
        loadingDialog = new MaterialDialog.Builder(this)
                .content("Загрузка...")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .show();
    }

    /**
     * Вход в систему под определенным пользователем.
     * @param user Данные пользователя для входа в систему.
     */
    private void signIn(User user){
        Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT).show();

        savePassAndLogin();

        databaseReference.removeEventListener(databaseListener);

        switch (user.getRole()) {
            case User.SIMPLE_USER: Globals.currentUser = user.toSimpleUser(); break;
            case User.MANAGER: Globals.currentUser = user.toManager(); break;
            case User.DEPARTMENT_MEMBER: Globals.currentUser = user.toDepartmentMember(); break;
            case User.DEPARTMENT_CHIEF: Globals.currentUser = user.toDepartmentChief(); break;
        }
        Globals.currentUser.signIn(this, SignInActivity.this);
    }
}
