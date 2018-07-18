package de.iolite.apps.ioliteslackbot.messagecontroller;

import java.util.EnumSet;

public class test {


    EnumSet<testenum> testenumEnumSet;

    public test(){
        testenumEnumSet = EnumSet.of(testenum.eins);
    }

    /*public static void main(String args[]){
        Object tests = new test();
        ((test) tests).gettestenum();
        ((test) tests).setEins();
        ((test) tests).setZwei();
        ((test) tests).gettestenum();



    }*/

    public enum testenum{
        eins, zwei;
    }

    public void gettestenum(){
        System.out.println(testenumEnumSet);
    }

    public void setEins(){
        testenumEnumSet = EnumSet.of(testenum.eins);
        System.out.println(testenumEnumSet);
    }

    public void setZwei(){
        if (testenumEnumSet.contains(testenum.eins))
            testenumEnumSet = EnumSet.of(testenum.zwei);

        System.out.println(testenumEnumSet);

    }



}
