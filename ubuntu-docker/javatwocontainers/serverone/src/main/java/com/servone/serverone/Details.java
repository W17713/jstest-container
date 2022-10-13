package com.servone.serverone.details; 

import java.util.ArrayList;
import java.lang.String;

public class Details {
        // Creating an object of ArrayList
        public ArrayList<String> Data  = new ArrayList<String>();
        String number;
        String name;
        Details(String number, String name)
        {
         // This keyword refers to parent instance itself
         this.number = number;
         this.name = name;
        }
}
