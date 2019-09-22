package com.amairovi.keeper;

import java.util.concurrent.atomic.AtomicInteger;

public class Application {
    private static AtomicInteger counter = new AtomicInteger();

    public static String nextId(){
        return ((Integer) counter.incrementAndGet()).toString();
    }

    public static void main(String[] args) {

    }
}
