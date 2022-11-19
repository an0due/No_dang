package com.banana.Nodang;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String email;
    public String name;
    public String gender;
    public String move;
    public String height;
    public String weight;
    public String goal;
    public double dailyKCal;

    public User() { }
    public User( String email, String name, String gender, String height, String weight, String goal, String move, double dailyKCal) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.move = move;
        this.dailyKCal = dailyKCal;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public void setGoal(String goal) {
        this.goal = goal;
    }
    public void setMove(String move) {
        this.move = move;
    }
    public void setDailyKCal(double dailyKCal) {
        this.dailyKCal = dailyKCal;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getGender() {
        return gender;
    }
    public String getHeight() {return height;}
    public String getWeight() {return weight;}
    public String getGoal() {
        return goal;
    }
    public String getMove() {return move;}
    public double getDailyKCal() {return dailyKCal;}

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender + '\'' +
                ", height=" + height + '\'' +
                ", weight=" + weight + '\'' +
                ", goal=" + goal + '\'' +
                ", move=" + move + '\'' +
                ", dailyKCal=" + dailyKCal + '\'' +
                '}';
    }
}