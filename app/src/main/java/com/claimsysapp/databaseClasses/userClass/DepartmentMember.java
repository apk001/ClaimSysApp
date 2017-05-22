package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;
import android.content.Intent;

import com.claimsysapp.MyTicketsActivity;

public class DepartmentMember extends User{

    private int solvingTicketCount;

    /**
     * Конструктор по-умолчанию.
     * Используется для восстановления данных из базы данных.
     */
    public DepartmentMember() {

    }

    public DepartmentMember(String branchId, String login, String password, int role, String userName, int solvingTicketCount) {
        super(branchId, login, password, role, userName);
        this.solvingTicketCount = solvingTicketCount;
    }

    @Override
    public void signIn(Context context, Context source) {
        context.startActivity(new Intent(source, MyTicketsActivity.class));
    }

    public int getSolvingTicketCount() {
        return solvingTicketCount;
    }

}
