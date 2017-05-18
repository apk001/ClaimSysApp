package com.claimsysapp.databaseClasses.userClass;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Класс, агрегирующий регистрационные данные подтвержденного пользователя.
 * Основной {@link #User(String branchId, boolean isBlocked, String login, String password, int role, String userName, String workPlace) конструктор}.
 * @author Monarch
 */
public class User extends AbstractUser implements Serializable {

    //region Constants

    /**
     * Роль простого пользователя.
     */
    public final static int SIMPLE_USER = 0;

    /**
     * Член отдела поддержки. Роль консультанта пользователей.
     */
    public final static int DEPARTMENT_MEMBER = 1;

    /**
     * Диспетчер службы поддержки. Распределяет заявки между консультантами.
     */
    public final static int MANAGER = 2;

    /**
     * Начальник отдела поддержки. Роль консультанта пользователей.
     */
    public final static int DEPARTMENT_CHIEF = 4;

    //endregion

    //region Fields

    /**
     * Идентификатор узла, объединяющего данные одного объекта класса в базе данных.
     */
    private String branchId;

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

    /**
     * Уровень прав пользователя
     */
    private int role;

    /**
     * Имя пользователя.
     */
    private String userName;

    //endregion

    //region Constructors

    /**
     * Конструктор по-умолчанию.
     * Используется для восстановления данных из базы данных.
     */
    public User() {

    }

    /**
     * Конструктор, использующийся для добавления новых подтвержденных пользователей в систему.
     * @param branchId Задает идентификатор узла, объединяющего данные одного объекта класса в базе данных.
     * @param login Задает логин пользователя.
     * @param password Задает пароль пользователя.
     * @param userName Имя пользователя.
     */
    public User(String branchId, String login, String password, int role, String userName) {
        this.branchId = branchId;
        this.login = login;
        this.password = password;

        this.role = role;

        this.userName = userName;

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        this.registrationDate = formatter.format(Calendar.getInstance().getTime());
    }

    //endregion

    //region Methods

    public void signIn(Context context, Context source){

    }

    public SimpleUser toSimpleUser(){
        return new SimpleUser(this.branchId, this.login, this.password, this.role, this.userName);
    }

    public Manager toManager(){
        return new Manager(this.branchId, this.login, this.password, this.role, this.userName);
    }

    public DepartmentMember toDepartmentMember(){
        return new DepartmentMember(this.branchId, this.login, this.password, this.role, this.userName);
    }

    public DepartmentChief toDepartmentChief(){
        return new DepartmentChief(this.branchId, this.login, this.password, this.role, this.userName);
    }

    //endregion

    //region Getters

    /**
     * @return Идентификатор узла, объединяющего данные одного объекта класса в базе данных.
     */
    public String getBranchId() {
        return branchId;
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

    //endregion

}
