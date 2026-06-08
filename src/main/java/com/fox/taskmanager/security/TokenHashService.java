package com.fox.taskmanager.security;

public interface TokenHashService {

    String hash(String token);
}
