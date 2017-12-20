package com.hikvision.myapplication;

/**
 * Created by Administrator on 2017/12/19.
 */

public class DataBean {
    public int getTatalMan() {
        return tatalMan;
    }

    public void setTatalMan(int tatalMan) {
        this.tatalMan = tatalMan;
    }

    public int getTatalfloor() {
        return tatalfloor;
    }

    public void setTatalfloor(int tatalfloor) {
        this.tatalfloor = tatalfloor;
    }

    public int getOnefloorman() {
        return onefloorman;
    }

    public void setOnefloorman(int onefloorman) {
        this.onefloorman = onefloorman;
    }

    public int getTatolonefloorman() {
        return tatolonefloorman;
    }

    public void setTatolonefloorman(int tatolonefloorman) {
        this.tatolonefloorman = tatolonefloorman;
    }

    public int getOnefloorbedroom() {
        return onefloorbedroom;
    }

    public void setOnefloorbedroom(int onefloorbedroom) {
        this.onefloorbedroom = onefloorbedroom;
    }

    public int getOnebedroomman() {
        return onebedroomman;
    }

    public void setOnebedroomman(int onebedroomman) {
        this.onebedroomman = onebedroomman;
    }

    public int getTatolonebedroomman() {
        return tatolonebedroomman;
    }

    public void setTatolonebedroomman(int tatolonebedroomman) {
        this.tatolonebedroomman = tatolonebedroomman;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int tatalMan;            //整栋楼总人数
    public int tatalfloor;          //整栋楼总层数
    public int onefloorman;         //一层楼的人数
    public int tatolonefloorman;    //一层楼的总人数
    public int onefloorbedroom;     //一层楼的寝室数量
    public int onebedroomman;       //每个寝室的人数
    public int tatolonebedroomman;  //每个寝室的总人数
    public int studentId;
}
