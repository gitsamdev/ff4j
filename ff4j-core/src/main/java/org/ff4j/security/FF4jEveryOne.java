package org.ff4j.security;

public class FF4jEveryOne extends FF4jGrantees {
    
    @Override
    public boolean isUserGranted(FF4jUser user) {
        return true;
    }

}
