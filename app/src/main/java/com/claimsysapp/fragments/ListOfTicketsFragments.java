package com.claimsysapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.claimsysapp.R;
import com.claimsysapp.adapters.TicketExpandableRecyclerAdapter;
import com.claimsysapp.databaseClasses.Ticket;
import com.claimsysapp.utility.Globals;

import java.util.ArrayList;

/**
 * Класс для фрагментов ViewPager, находящегося в ListOfTicketsActivity.class.
 * @author ahgpoug
 */
public class ListOfTicketsFragments {
    /**
     * Адаптер для фрагментов
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private FirstFragment firstFragment;
        private SecondFragment secondFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Метод, загружающий новые фрагменты по их номеру
         */
        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return FirstFragment.newInstance();
            else return SecondFragment.newInstance();
        }

        //Получение числа фрагментов
        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Установка заголовков в TabLayout для фрагментов
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Доступные";
                case 1:
                    return "Активные";
            }
            return null;
        }

        /**
         * Метод для запоминания ссылок на фрагменты
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    firstFragment = (FirstFragment) createdFragment;
                    break;
                case 1:
                    secondFragment = (SecondFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        /**
         * Метод для обновления информации на первом фрагменте.
         * @param listOfAvailableTickets список заявок, доступных для принятия.
         * @param context контекст Activity, где был создан фрагмент.
         * @param databaseReference контекст ссыылка на базу данных для ее редактирования.
         */
        public void updateFirstFragment(ArrayList<Ticket> listOfAvailableTickets, Context context, DatabaseReference databaseReference){
            firstFragment.updateContent(listOfAvailableTickets, context, databaseReference);
        }

        /**
         * Метод для обновления информации на втором фрагменте.
         * @param listOfMyActiveTickets список заявок, закрытых текущим пользователем.
         * @param context контекст Activity, где был создан фрагмент.
         */
        public void updateSecondFragment(ArrayList<Ticket> listOfMyActiveTickets, Context context){
            secondFragment.updateContent(listOfMyActiveTickets, context);
        }

    }

    /**
     * Фрагемент списка доступных для принятия заявок
     */
    public static class FirstFragment extends Fragment {
        RecyclerView viewOfAvailableTickets;

        /**
         * Метод, вызывающийся при создании фрагмента
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_recycler, container, false);

            viewOfAvailableTickets = (RecyclerView) v.findViewById(R.id.recycler);

            return v;
        }

        /**
         * Метод для обновления информации на первом фрагменте.
         * @param listOfAvailableTickets список заявок, доступных для принятия.
         * @param context контекст Activity, где был создан фрагмент.
         * @param databaseReference контекст ссыылка на базу данных для ее редактирования.
         */
        public void updateContent(final ArrayList<Ticket> listOfAvailableTickets, final Context context, final DatabaseReference databaseReference){
            //Создание списка заявок для передачи в TicketExpandableRecyclerAdapter.class
            ArrayList<TicketExpandableRecyclerAdapter.TicketListItem> ticketListItems = new ArrayList<>();

            //Заполнение списка заявок для передачи в TicketExpandableRecyclerAdapter.class
            for (int i = 10; i < 13; i++) {
                ticketListItems.add(new TicketExpandableRecyclerAdapter.TicketListItem(Globals.getTicketTypeName(context, i)));
                int index = 0;
                for (Ticket ticket : listOfAvailableTickets)
                    if (ticket.getType() == i) {
                        ticketListItems.add(new TicketExpandableRecyclerAdapter.TicketListItem(ticket));
                        index++;
                    }
                ticketListItems.set(ticketListItems.size() - index - 1, new TicketExpandableRecyclerAdapter.TicketListItem(Globals.getTicketTypeName(context, i) + " (" + index + ")"));
            }

            //Создание нового адаптера для viewOfAvailableTickets
            TicketExpandableRecyclerAdapter adapter = new TicketExpandableRecyclerAdapter(TicketExpandableRecyclerAdapter.TYPE_AVAILABLE, context, ticketListItems);
            adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            viewOfAvailableTickets.setLayoutManager(mLayoutManager);
            viewOfAvailableTickets.setHasFixedSize(false);
            viewOfAvailableTickets.setAdapter(adapter);

            //Раскрытие категорий, которые были раскрыты ранее
            try {
;                for (int position : Globals.expandedItemsAvailable)
                    adapter.expandItems(position, true);
            } catch (Exception e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }

        /**
         * Создание нового экземпляра текущего фрагемента
         */
        public static FirstFragment newInstance() {
            FirstFragment f = new FirstFragment();
            return f;
        }
    }

    /**
     * Фрагемент списка заявок, закрытых текущим пользователем
     */
    public static class SecondFragment extends Fragment {
        RecyclerView viewOfMyClosedTickets;

        /**
         * Метод, вызывающийся при создании фрагмента
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_recycler, container, false);

            viewOfMyClosedTickets = (RecyclerView)v.findViewById(R.id.recycler);

            return v;
        }

        /**
         * Метод для обновления информации на втором фрагменте.
         * @param listOfActiveTickets список текущих заявок
         * @param context контекст Activity, где был создан фрагмент.
         */
        public void updateContent(ArrayList<Ticket> listOfActiveTickets, Context context){
            //Создание списка заявок для передачи в TicketExpandableRecyclerAdapter.class
            ArrayList<TicketExpandableRecyclerAdapter.TicketListItem> ticketListItems = new ArrayList<>();

            //Заполнение списка заявок для передачи в TicketExpandableRecyclerAdapter.class
            for (int i = 10; i < 13; i++) {
                ticketListItems.add(new TicketExpandableRecyclerAdapter.TicketListItem(Globals.getTicketTypeName(context, i)));
                int index = 0;
                for (Ticket ticket : listOfActiveTickets)
                    if (ticket.getType() == i) {
                        ticketListItems.add(new TicketExpandableRecyclerAdapter.TicketListItem(ticket));
                        index++;
                    }
                ticketListItems.set(ticketListItems.size() - index - 1, new TicketExpandableRecyclerAdapter.TicketListItem(Globals.getTicketTypeName(context, i) + " (" + index + ")"));
            }

            //Создание нового адаптера для viewOfMyClosedTickets
            TicketExpandableRecyclerAdapter adapter = new TicketExpandableRecyclerAdapter(TicketExpandableRecyclerAdapter.TYPE_ACTIVE, context, ticketListItems);
            adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            viewOfMyClosedTickets.setLayoutManager(mLayoutManager);
            viewOfMyClosedTickets.setHasFixedSize(false);
            viewOfMyClosedTickets.setAdapter(adapter);

            //Раскрытие категорий, которые были раскрыты ранее
            try {
                for (int position : Globals.expandedItemsActive)
                    adapter.expandItems(position, true);
            } catch (Exception e){
                e.printStackTrace();
            }

            adapter.notifyDataSetChanged();

        }

        /**
         * Создание нового экземпляра текущего фрагемента
         */
        public static SecondFragment newInstance() {
            SecondFragment f = new SecondFragment();
            return f;
        }
    }

}
