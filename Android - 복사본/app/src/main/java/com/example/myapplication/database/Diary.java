package com.example.myapplication.database;

public class Diary {
    String _id;
    String picture0;
    String food0;
    String mood0;
    String comment0;
    String picture1;
    String food1;
    String mood1;
    String comment1;
    String picture2;
    String food2;
    String mood2;
    String comment2;
    String picture3;
    String food3;
    String mood3;
    String comment3;
    String tan;
    String dan;
    String gi;
    String kcal;

    public Diary(){

    }


    public Diary(String _id, String picture0, String food0, String mood0, String comment0, String picture1, String food1, String mood1, String comment1, String picture2, String food2, String mood2, String comment2, String picture3, String food3, String mood3, String comment3,String tan,String dan,String gi,String kcal){
        this._id = _id;
        this.picture0 = picture0;
        this.food0 = food0;
        this.mood0 = mood0;
        this.comment0 = comment0;
        this.picture1 = picture1;
        this.food1 = food1;
        this.mood1 = mood1;
        this.comment1 = comment1;
        this.picture2 = picture2;
        this.food2 = food2;
        this.mood2 = mood2;
        this.comment2 = comment2;
        this.picture3 = picture3;
        this.food3 = food3;
        this.mood3 = mood3;
        this.comment3 = comment3;
        this.tan = tan;
        this.dan = dan;
        this.gi = gi;
        this.kcal = kcal;
    }

    public String get_id() {
        return _id;
    }

    public String getPicture0() {
        return picture0;
    }

    public String getFood0() {
        return food0;
    }

    public String getMood0() {
        return mood0;
    }

    public String getComment0() {
        return comment0;
    }

    public String getPicture1() {
        return picture1;
    }

    public String getFood1() {
        return food1;
    }

    public String getMood1() {
        return mood1;
    }

    public String getComment1() {
        return comment1;
    }

    public String getPicture2() {
        return picture2;
    }

    public String getFood2() {
        return food2;
    }

    public String getMood2() {
        return mood2;
    }

    public String getComment2() {
        return comment2;
    }

    public String getPicture3() {
        return picture3;
    }

    public String getFood3() {
        return food3;
    }

    public String getMood3() {
        return mood3;
    }

    public String getComment3() {
        return comment3;
    }

    public String getTan(){return tan;}

    public String getDan(){return dan;}

    public String getGi(){return gi;}

    public String getKcal(){return kcal;}

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setPicture0(String picture0) {
        this.picture0 = picture0;
    }

    public void setFood0(String food0) {
        this.food0 = food0;
    }

    public void setMood0(String mood0) {
        this.mood0 = mood0;
    }

    public void setComment0(String comment0) {
        this.comment0 = comment0;
    }

    public void setPicture1(String picture1) {
        this.picture1 = picture1;
    }

    public void setFood1(String food1) {
        this.food1 = food1;
    }

    public void setMood1(String mood1) {
        this.mood1 = mood1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public void setFood2(String food2) {
        this.food2 = food2;
    }

    public void setMood2(String mood2) {
        this.mood2 = mood2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public void setPicture3(String picture3) {
        this.picture3 = picture3;
    }

    public void setFood3(String food3) {
        this.food3 = food3;
    }

    public void setMood3(String mood3) {
        this.mood3 = mood3;
    }

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    public void setTan(String tan){this.tan = tan;}

    public void setDan(String dan){this.dan = dan;}

    public void setGi(String gi){this.gi = gi;}

    public void setKcal(String kcal){this.kcal = kcal;}
}
