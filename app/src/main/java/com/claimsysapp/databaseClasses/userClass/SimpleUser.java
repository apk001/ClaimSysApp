package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.MyTicketsActivity;

public class SimpleUser extends User{

    public SimpleUser(String branchId, String login, String password, int role, String userName) {
        super(branchId, login, password, role, userName);
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, MyTicketsActivity.class));
    }

}
