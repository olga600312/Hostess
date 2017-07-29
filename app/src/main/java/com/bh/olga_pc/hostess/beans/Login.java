package com.bh.olga_pc.hostess.beans;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class Login {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Login withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Login withPassword(String password){
        setPassword(password);
        return this;
    }
}