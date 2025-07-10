package com.example;
import org.mindrot.jbcrypt.BCrypt;
public class BcryptTest {
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("pass123", BCrypt.gensalt()));
    }
}