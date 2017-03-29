package com.claimsysapp.databaseClasses;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.MyTicketsActivity;

public class SimpleUser extends User{

    public SimpleUser(String branchId, boolean isBlocked, String login, String password, int role, String userName, String workPlace) {
        super(branchId, isBlocked, login, password, role, userName, workPlace);
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, MyTicketsActivity.class));
    }

}
