package com.claimsysapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.claimsysapp.adapters.SpecialistsRecyclerAdapter;
import com.claimsysapp.databaseClasses.userClass.DepartmentMember;
import com.claimsysapp.utility.ItemClickSupport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.claimsysapp.databaseClasses.Ticket;
import com.claimsysapp.databaseClasses.userClass.User;
import com.claimsysapp.utility.DatabaseVariables;
import com.claimsysapp.utility.Globals;

import java.io.Serializable;
import java.util.ArrayList;

public class AssignTicketActivity extends AppCompatActivity {

    RecyclerView specialistsView;
    ArrayList<DepartmentMember> specialistsList;
    DatabaseReference databaseReference;
    Ticket currentTicket;

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Globals.logInfoAPK(AssignTicketActivity.this, "Скачивание данных пользователей - НАЧАТО");
            specialistsList = Globals.Downloads.Users.getDepartmentMemberList(dataSnapshot, true);

            SpecialistsRecyclerAdapter adapter = new SpecialistsRecyclerAdapter(AssignTicketActivity.this, specialistsList, getSupportFragmentManager());

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(AssignTicketActivity.this, LinearLayoutManager.VERTICAL, false);
            specialistsView.setLayoutManager(mLayoutManager);
            specialistsView.setHasFixedSize(false);
            specialistsView.setAdapter(adapter);

            Globals.logInfoAPK(AssignTicketActivity.this, "Скачивание данных пользователей - ОКОНЧЕНО");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_ticket);
        setTitle("Специалисты");

        currentTicket = (Ticket) getIntent().getExtras().getSerializable("currentTicket");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeComponents();
        setEvents();
    }

    private void initializeComponents() {
        specialistsView = (RecyclerView) findViewById(R.id.specialistsList);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void setEvents(){
        ItemClickSupport.addTo(specialistsView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(final RecyclerView recyclerView, int position, View v) {
                final DepartmentMember worker = specialistsList.get(position);
                new MaterialDialog.Builder(AssignTicketActivity.this)
                        .title(currentTicket.getTopic())
                        .content("Вы действительно хотите назначить:\n" + worker.getUserName() + " заявку?")
                        .positiveText("Назначить")
                        .negativeText("Отмена")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                databaseReference.child(DatabaseVariables.FullPath.Users.DATABASE_WORKER_TABLE).child(worker.getBranchId()).child("solvingTicketCount").setValue(worker.getSolvingTicketCount()+1);
                                currentTicket.addSpecialist(worker.getLogin(), worker.getUserName());
                                databaseReference.child(DatabaseVariables.FullPath.Tickets.DATABASE_MARKED_TICKET_TABLE).child(currentTicket.getTicketId()).setValue(currentTicket);
                                databaseReference.child(DatabaseVariables.FullPath.Tickets.DATABASE_UNMARKED_TICKET_TABLE).child(currentTicket.getTicketId()).removeValue();
                                AssignTicketActivity.this.finish();
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.addValueEventListener(userListener);
        Globals.logInfoAPK(AssignTicketActivity.this, "onResume - ВЫПОЛНЕН");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(userListener);
        Globals.logInfoAPK(AssignTicketActivity.this, "onPause - ВЫПОЛНЕН");
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(userListener);
        Globals.logInfoAPK(AssignTicketActivity.this, "onStop - ВЫПОЛНЕН");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
