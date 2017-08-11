package com.example.sarthak.firebasesignin.models;

/**
 * POJO class to store current user login details
 *
 * @author Sarthak Grover
 */

public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {

        this.username = username;
        this.email = email;
    }
}
