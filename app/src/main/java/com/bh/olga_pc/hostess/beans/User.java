package com.bh.olga_pc.hostess.beans;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class User implements Cloneable{
    private int id;
    private String name;
    private int type;
    private Login login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
