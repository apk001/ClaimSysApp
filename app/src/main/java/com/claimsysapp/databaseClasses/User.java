package com.claimsysapp.databaseClasses;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Класс, агрегирующий регистрационные данные подтвержденного пользователя.
 * Основной {@link #User(String branchId, boolean isBlocked, String login, String password, String userName, String workPlace) конструктор}.
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
     * @param workPlace Рабочее место пользователя.
     * @param isBlocked Флаг, показывающий, заблокирован ли пользователь.
     */
    public User(String branchId, boolean isBlocked, String login, String password, int role, String userName, String workPlace) {
        this.branchId = branchId;
        this.login = login;
        this.password = password;

        this.role = role;

        this.userName = userName;
        this.workPlace = workPlace;
        this.isBlocked = isBlocked;

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        this.registrationDate = formatter.format(Calendar.getInstance().getTime());
    }

    //endregion

    //region Methods

    public void signIn(Context context, Context source){

    }

    public SimpleUser toSimpleUser(){
        return new SimpleUser(this.branchId, this.isBlocked, this.login, this.password, this.role, this.userName, this.workPlace);
    }

    public Manager toManager(){
        return new Manager(this.branchId, this.isBlocked, this.login, this.password, this.role, this.userName, this.workPlace);
    }

    public DepartmentMember toDepartmentMember(){
        return new DepartmentMember(this.branchId, this.isBlocked, this.login, this.password, this.role, this.userName, this.workPlace);
    }

    public DepartmentChief toDepartmentChief(){
        return new DepartmentChief(this.branchId, this.isBlocked, this.login, this.password, this.role, this.userName, this.workPlace);
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
