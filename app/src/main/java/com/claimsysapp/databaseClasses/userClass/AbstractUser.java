package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Класс, агрегирующий регистрационные данные подтвержденного пользователя.
 * @author Monarch
 */
public abstract class AbstractUser implements Serializable{

    //region Methods

    public abstract void signIn(Context context, Context source);

    //endregion

}
