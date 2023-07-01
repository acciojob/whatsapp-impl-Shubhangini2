package com.driver;

import java.util.Objects;

public class User {
    private String name;
    private String mobile;

    //Will make the constructor
    public User(String name, String mobile){
        this.name=name;
        this.mobile=mobile;
    }

    //Will get the getters
    public String getName() {

        return name;
    }

    public String getMobile() {

        return mobile;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof User)) return false;
//        User user = (User) o;
//        return Objects.equals(getName(), user.getName()) && Objects.equals(getMobile(), user.getMobile());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getName(), getMobile());
//    }
}
