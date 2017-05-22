package com.claimsysapp.adapters;

import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.claimsysapp.R;
import com.claimsysapp.databaseClasses.userClass.DepartmentMember;
import com.claimsysapp.databaseClasses.userClass.User;
import com.claimsysapp.fragments.BottomSheetFragment;
import com.claimsysapp.utility.Globals;

import java.util.ArrayList;

public class SpecialistsRecyclerAdapter extends RecyclerView.Adapter<SpecialistsRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<DepartmentMember> list;
    FragmentManager fragmentManager;

    public SpecialistsRecyclerAdapter(Context context, ArrayList<DepartmentMember> list, FragmentManager fragmentManager) {
        this.context = context;
        this.list = list;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialist, parent, false);
        return new SpecialistsRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            final int pos = position;
            holder.userName.setText(list.get(position).getUserName());
            holder.ticketCount.setText(String.valueOf(list.get(position).getSolvingTicketCount()));
            holder.image.setImageDrawable(Globals.ImageMethods.getRoundImage(context, list.get(position).getUserName()));

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(pos).getUserName() != null) {
                        BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetFragment.newInstance(list.get(pos).getLogin(), list.get(pos).getLogin(), list.get(pos));
                        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.getTag());
                    }
                }
            });
        }
        catch (Exception e) {
            Globals.showLongTimeToast(context, e.getMessage() + "Обратитесь к разработчику");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView userName;
        private TextView ticketCount;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.userImage);
            userName = (TextView)itemView.findViewById(R.id.userName);
            ticketCount = (TextView)itemView.findViewById(R.id.ticketCount);
        }
    }

}

