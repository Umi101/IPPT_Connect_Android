package com.example.fyp_ippt_connect_android.app;

public class User {
    public String name;
    public String phone;
    public String gender;
    public int age;
    public float height;
    public float weight;
    public int pushUpTotalCount;
    public int sitUpTotalCount;

    public int getPushUpTotalCount() {
        return pushUpTotalCount;
    }

    public void setPushUpTotalCount(int pushUpTotalCount) {
        this.pushUpTotalCount = pushUpTotalCount;
    }

    public int getSitUpTotalCount() {
        return sitUpTotalCount;
    }

    public void setSitUpTotalCount(int sitUpTotalCount) {
        this.sitUpTotalCount = sitUpTotalCount;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String phone, String gender, int age, float height, float weight, int pushUpTotalCount, int sitUpTotalCount) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.pushUpTotalCount = pushUpTotalCount;
        this.sitUpTotalCount = sitUpTotalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

}
