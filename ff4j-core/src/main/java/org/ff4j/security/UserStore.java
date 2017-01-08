package org.ff4j.security;

import org.ff4j.store.FF4jRepository;

public interface UserStore extends FF4jRepository<String, FF4jUser >, UserManager {
    
}
