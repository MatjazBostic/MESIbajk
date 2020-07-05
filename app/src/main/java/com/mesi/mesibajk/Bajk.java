package com.mesi.mesibajk;

public class Bajk {

    private final String name;
    private boolean status;

    Bajk(String name, boolean status){
        this.name = name;
        this.status = status;
    }

    String getName(){
        return name;
    }

    boolean getStatus(){
        return status;
    }
}
