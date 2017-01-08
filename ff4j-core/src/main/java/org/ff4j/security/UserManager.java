package org.ff4j.security;

import java.util.stream.Stream;

public interface UserManager {
    
    FF4jUser getUserByUid(String userName);
    
    FF4jUser login(String userName, String password);
    
    Stream < FF4jUser> listUsers();
    
    Stream < FF4jUser> searchUsers(String query);
    
}
