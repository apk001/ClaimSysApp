package com.claimsysapp.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.claimsysapp.databaseClasses.userClass.DepartmentMember;
import com.google.firebase.database.DataSnapshot;
import com.claimsysapp.R;
import com.claimsysapp.databaseClasses.Ticket;
import com.claimsysapp.databaseClasses.userClass.User;

import java.util.ArrayList;

import static com.claimsysapp.utility.Globals.Downloads.Users.getUserList;

public class Globals {

    public static User currentUser;

    public static ArrayList<Integer> expandedItemsAvailable = new ArrayList<>();
    public static ArrayList<Integer> expandedItemsActive = new ArrayList<>();
    public static ArrayList<Integer> expandedItemsClosed = new ArrayList<>();

    /**
     * Метод, вызывающий информацию о приложении.
     * @param context Контекст вызывающего класса.
     * @param t id типа заявки
     */
    public static String getTicketTypeName(Context context, int t){
        String[] groups = context.getResources().getStringArray(R.array.ticket_types_array);
        return groups[t - 10];
    }

    public static void showKeyboardOnEditText(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Метод, вызывающий информацию о приложении.
     * @param context Контекст вызывающего класса.
     */
    public static void showAbout(Context context) {
        new MaterialDialog.Builder(context)
                .title("О программе")
                .content(String.format("Claim System App V%s", context.getString(R.string.app_version)))
                .positiveText("Ок")
                .show();
    }

    /**
     * Метод, инициализирующий и отображающий долговременное, всплываюее сообщение.
     * @param context Контекст приложения.
     * @param message Сообщение.
     */
    public static void showLongTimeToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean isEnglishWord(String word){
        for (int i = 0; i < word.length(); i++)
            if (!isEnglishLetterOrDigit(word.charAt(i)))
                return false;
        return true;
    }

    private static boolean isEnglishLetterOrDigit(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
    }

    public static class ImageMethods {
        /**
         * Метод, создающий квадратную картинку первой
         * @param name Отображаемое имя.
         * @return Возвращает картинку (класс TextDrawable) с первой буквой по центру.
         */
        public static TextDrawable getSquareImage(Context context, String name) {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(name);

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .useFont(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"))
                    .bold()
                    .withBorder(2)
                    .endConfig()
                    .buildRect(String.valueOf(name.charAt(0)).toUpperCase(), color);
            return drawable;
        }

        /**
         * Метод, создающий круглую картинку первой
         * @param name Отображаемое имя.
         * @return Возвращает картинку (класс TextDrawable) с первой буквой по центру.
         */
        public static TextDrawable getRoundImage(Context context, String name) {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(name);

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .useFont(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"))
                    .bold()
                    .withBorder(2)
                    .endConfig()
                    .buildRound(String.valueOf(name.charAt(0)).toUpperCase(), color);
            return drawable;
        }
    }

    public static void logInfoAPK(Context context, String message){
        String activity = context.toString();
        activity = activity.substring(activity.lastIndexOf('.') + 1, activity.indexOf("@"));
        Log.i("APK001/" + activity, message);
    }

    public static class Downloads {

        public static class Users {

            /**
             * Метод, скачивающий из базы данных всех подтвержденных пользователей, независимо от прав.
             * @param dataSnapshot Снимок базы данных.
             * @return Всех подтвержденных пользователей.
             */
            public static ArrayList<User> getUserList(DataSnapshot dataSnapshot, boolean isRootFolder) {
                ArrayList<User> resultList = new ArrayList<User>();
                resultList.addAll(getSpecificUserList(dataSnapshot, DatabaseVariables.Folders.UserFolder.DATABASE_CHIEF_TABLE, isRootFolder));
                resultList.addAll(getSpecificUserList(dataSnapshot, DatabaseVariables.Folders.UserFolder.DATABASE_USER_TABLE, isRootFolder));
                resultList.addAll(getSpecificUserList(dataSnapshot, DatabaseVariables.Folders.UserFolder.DATABASE_WORKER_TABLE, isRootFolder));
                resultList.addAll(getSpecificUserList(dataSnapshot, DatabaseVariables.Folders.UserFolder.DATABASE_MANAGER_TABLE, isRootFolder));
                return resultList;
            }

            /**
             * Метод, скачивающий из базы данных подтвержденных пользователей с определенными правами.
             * @param dataSnapshot Снимок базы данных.
             * @param userTypePath Путь в базе данных к необходимой категории подтвержденных пользователей.
             * @return Всех подтвержденных пользователей с определенными правами.
             */
            public static ArrayList<User> getSpecificUserList(DataSnapshot dataSnapshot, String userTypePath, boolean isRootFolder) {
                ArrayList<User> resultList = new ArrayList<User>();
                if (isRootFolder)
                    for (DataSnapshot userRecord :
                            dataSnapshot.child(DatabaseVariables.Folders.DATABASE_ALL_USER_TABLE).child(userTypePath).getChildren())
                        resultList.add(userRecord.getValue(User.class));
                else
                    for (DataSnapshot userRecord : dataSnapshot.child(userTypePath).getChildren())
                        resultList.add(userRecord.getValue(User.class));
                return resultList;
            }

            public static ArrayList<DepartmentMember> getDepartmentMemberList(DataSnapshot dataSnapshot, boolean isRootFolder) {
                ArrayList<DepartmentMember> resultList = new ArrayList<DepartmentMember>();
                if (isRootFolder)
                    for (DataSnapshot userRecord :
                            dataSnapshot.child(DatabaseVariables.FullPath.Users.DATABASE_WORKER_TABLE).getChildren())
                        resultList.add(userRecord.getValue(DepartmentMember.class));
                else
                    for (DataSnapshot userRecord : dataSnapshot.child(DatabaseVariables.Folders.UserFolder.DATABASE_WORKER_TABLE).getChildren())
                        resultList.add(userRecord.getValue(DepartmentMember.class));
                return resultList;
            }

        }

        public static class Strings {

            /**
             * Метод, скачивающий из базы данных логины всех пользователей.
             * @param dataSnapshot Снимок базы данных.
             * @return Логины всех пользователей.
             */
            public static ArrayList<String> getLogins(DataSnapshot dataSnapshot, boolean rootFolder) {
                ArrayList<String> resultList = new ArrayList<String>();
                ArrayList<User> userList = getUserList(dataSnapshot, rootFolder);
                for (User user : userList)
                    resultList.add(user.getLogin());
                return resultList;
            }

            public static ArrayList<String> getUserMarkedTicketIDs(DataSnapshot dataSnapshot, String userId){
                ArrayList<String> resultList = new ArrayList<String>();
                for (DataSnapshot ticketRecord : dataSnapshot.child(DatabaseVariables.FullPath.Tickets.DATABASE_MARKED_TICKET_TABLE).getChildren()){
                    Ticket ticket = ticketRecord.getValue(Ticket.class);
                    if (ticket.getUserId().equals(userId))
                        resultList.add(ticket.getTicketId());
                }
                return resultList;
            }

        }

        public static class Tickets {

            /**
             * Метод, скачивающий из базы данных все заявки, ответственным за которые является определенный работник.
             * @param dataSnapshot Снимок базы данных.
             * @param overseerLogin Логин ответственного, заявки которого нужно получить.
             * @return Заявки определенного ответственного.
             */
            public static ArrayList<Ticket> getOverseerTicketList(DataSnapshot dataSnapshot, String overseerLogin, boolean exceptFolder) {
                ArrayList<Ticket> resultList = new ArrayList<Ticket>();
                if (exceptFolder) {
                    for (DataSnapshot ticketRecord : dataSnapshot.child(DatabaseVariables.Folders.TicketFolder.DATABASE_MARKED_TICKET_TABLE).getChildren()) {
                        Ticket ticket = ticketRecord.getValue(Ticket.class);
                        if (ticket.getSpecialistId().equals(overseerLogin))
                            resultList.add(ticket);
                    }
                } else {
                    for (DataSnapshot ticketRecord : dataSnapshot.child(DatabaseVariables.FullPath.Tickets.DATABASE_MARKED_TICKET_TABLE).getChildren()) {
                    Ticket ticket = ticketRecord.getValue(Ticket.class);
                    if (ticket.getSpecialistId().equals(overseerLogin))
                        resultList.add(ticket);
                    }
                }
                return resultList;
            }

            /**
             * Метод, скачивающий из базы данных заявки определнного пользователя, находящиеся в определенном состоянии.
             * @param dataSnapshot Снимок базы данных.
             * @param databaseTablePath Путь в базе данных к необходимой категории заявок.
             * @param userLogin Логин пользователя.
             * @return Заявки определенного пользователя, находящиеся в определенном состоянии.
             */
            public static ArrayList<Ticket> getUserSpecificTickets(DataSnapshot dataSnapshot, String databaseTablePath, String userLogin) {
                ArrayList<Ticket> resultList = new ArrayList<Ticket>();
                for (DataSnapshot ticketRecord : dataSnapshot.child(databaseTablePath).getChildren()) {
                    Ticket markedTicket = ticketRecord.getValue(Ticket.class);
                    if (markedTicket.getUserId().equals(userLogin))
                        resultList.add(markedTicket);
                }
                return resultList;
            }

            /**
             * Метод, скачивающий из базы данных все заявки, находящиеся в определенном состоянии.
             * @param dataSnapshot Снимок базы данных.
             * @param databaseTablePath Путь в базе данных к необходимой категории заявок.
             * @return Заявки, находящиеся в определенном состоянии.
             */
            public static ArrayList<Ticket> getSpecificTickets(DataSnapshot dataSnapshot, String databaseTablePath) {
                ArrayList<Ticket> resultList = new ArrayList<Ticket>();
                for (DataSnapshot ticketRecord : dataSnapshot.child(databaseTablePath).getChildren())
                    resultList.add(ticketRecord.getValue(Ticket.class));
                return resultList;
            }

            public static ArrayList<Ticket> getAllTickets(DataSnapshot dataSnapshot) {
                ArrayList<Ticket> resultList = getSpecificTickets(dataSnapshot, DatabaseVariables.Folders.TicketFolder.DATABASE_MARKED_TICKET_TABLE);
                resultList.addAll(getSpecificTickets(dataSnapshot, DatabaseVariables.Folders.TicketFolder.DATABASE_UNMARKED_TICKET_TABLE));
                resultList.addAll(getSpecificTickets(dataSnapshot, DatabaseVariables.Folders.TicketFolder.DATABASE_SOLVED_TICKET_TABLE));
                return resultList;
            }

        }

    }

}
