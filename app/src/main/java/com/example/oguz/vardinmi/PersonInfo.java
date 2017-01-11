package com.example.oguz.vardinmi;


public class PersonInfo implements Comparable<PersonInfo>{

    private String name;
    private String phoneNumber;
    private boolean isUsing;
    private String uid;

    public String getName(){
        return this.name;
    }

    @Override
    public int compareTo(PersonInfo another) {
        return this.name.compareTo(another.name);
    }
    public boolean isUsing(){
        return this.isUsing;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public String getUid(){
        return this.uid;
    }
    public PersonInfo(String name, String phoneNumber,boolean isUsing,String uid){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isUsing = isUsing;
        this.uid = uid;
    }
}