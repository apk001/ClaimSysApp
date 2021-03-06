package com.claimsysapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.claimsysapp.databaseClasses.userClass.User;
import com.claimsysapp.utility.DatabaseVariables;
import com.claimsysapp.utility.Globals;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryToConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(valueEventListener);
    }

    private void tryToConnect(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("Login","").equals("") || preferences.getString("Password","").equals("")){
            showNextActivity();
        } else {
            if (hasConnection()) {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<User> userList = Globals.Downloads.Users.getUserList(dataSnapshot, false);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                        checkVerificationData(userList, preferences.getString("Login", ""), preferences.getString("Password", ""));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Ошибка в работе базы данных. Обратитесь к администратору компании или разработчику", Toast.LENGTH_LONG).show();
                        SplashActivity.this.finish();
                    }
                };
                databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseVariables.Folders.DATABASE_ALL_USER_TABLE);
            } else {
                Toast.makeText(getApplicationContext(), "Нет подключения к интернету", Toast.LENGTH_LONG).show();
                showNextActivity();
            }
        }
    }

    private void showNextActivity(){
        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
        SplashActivity.this.finish();
    }

    private void signIn(User user){
        Toast.makeText(getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT).show();

        Globals.currentUser = user;

        switch (user.getRole()) {
            case User.SIMPLE_USER: Globals.currentUser = user.toSimpleUser(); break;
            case User.MANAGER: Globals.currentUser = user.toManager(); break;
            case User.DEPARTMENT_MEMBER: Globals.currentUser = user.toDepartmentMember(); break;
            case User.DEPARTMENT_CHIEF: Globals.currentUser = user.toDepartmentChief(); break;
        }
        Globals.currentUser.signIn(this, SplashActivity.this);
        SplashActivity.this.finish();
    }

    private void checkVerificationData(ArrayList<User> userList, String login, String password) {
        int i = 0;
        while (!login.equals(userList.get(i).getLogin()) && ++i < userList.size());
        if (i >= userList.size()) {
            Toast.makeText(getApplicationContext(), "Логин и/или пароль введен неверно. Повторите попытку", Toast.LENGTH_LONG).show();
            showNextActivity();
        }
        else if (login.equals(userList.get(i).getLogin()) && password.equals(userList.get(i).getPassword()))
            signIn(userList.get(i));
        else {
            Toast.makeText(getApplicationContext(), "Логин и/или пароль введен неверно. Повторите попытку", Toast.LENGTH_LONG).show();
            showNextActivity();
        }
    }

    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager)SplashActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}