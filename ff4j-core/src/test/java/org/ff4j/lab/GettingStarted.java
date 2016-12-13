package org.ff4j.lab;

import org.ff4j.FF4j;

public class GettingStarted {
    
    public static void main(String[] args) {
        // Init FF4j
        FF4j ff4j = new FF4j("myFeatures.xml");
        // Story
        if (ff4j.check("story_42")) {
            System.out.println("Enable");
        } else {
            System.out.println("Elements");
        }
    }
    
    
}
