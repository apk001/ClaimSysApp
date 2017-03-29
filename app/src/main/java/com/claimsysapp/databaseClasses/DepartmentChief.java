package com.claimsysapp.databaseClasses;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.MyTicketsActivity;

public class DepartmentChief extends User{

    public DepartmentChief(String branchId, boolean isBlocked, String login, String password, int role, String userName, String workPlace) {
        super(branchId, isBlocked, login, password, role, userName, workPlace);
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, MyTicketsActivity.class));
    }

}
