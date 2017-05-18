package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.MyTicketsActivity;

public class DepartmentMember extends User{

    public DepartmentMember(String branchId, String login, String password, int role, String userName) {
        super(branchId, login, password, role, userName);
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, MyTicketsActivity.class));
    }
}
