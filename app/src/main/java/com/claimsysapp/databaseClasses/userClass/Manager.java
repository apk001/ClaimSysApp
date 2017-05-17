package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.ListOfTicketsActivity;

public class Manager extends User{

    public Manager(String branchId, boolean isBlocked, String login, String password, int role, String userName, String workPlace) {
        super(branchId, isBlocked, login, password, role, userName, workPlace);
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, ListOfTicketsActivity.class));
    }
}
