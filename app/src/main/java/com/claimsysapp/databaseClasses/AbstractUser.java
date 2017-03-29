package com.claimsysapp.databaseClasses;

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

    //region Fields

    /**
     * Идентификатор узла, объединяющего данные одного объекта класса в базе данных.
     */
    private String branchId;

    /**
     * Флаг, показывающий, заблокирован ли пользователь.
     */
    private boolean isBlocked;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Дата регистрации пользователя.
     */
    private String registrationDate;

    private int role;

    /**
     * Имя пользователя.
     */
    private String userName;

    /**
     * Рабочее место пользователя.
     */
    private String workPlace;

    //endregion

    //region Methods

    public abstract void signIn(Context context, Context source);

    //endregion

    //region Getters

    /**
     * @return Идентификатор узла, объединяющего данные одного объекта класса в базе данных.
     */
    public String getBranchId() {
        return branchId;
    }

    /**
     * @return Флаг, показывающий, заблокирован ли пользователь.
     */
    public boolean getIsBlocked() {
        return isBlocked;
    }

    /**
     * @return Логин пользователя.
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return Пароль пользователя.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return Дату регистрации пользователя.
     */
    public String getRegistrationDate() {
        return registrationDate;
    }

    public int getRole() { return role; }

    /**
     * @return Имя пользователя.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return Рабочее место пользователя.
     */
    public String getWorkPlace() {
        return workPlace;
    }

    //endregion

}
