// Tạo file tạm BcryptTest.java trong D:\code_internship\week_1\Microservice_Example\quarkus-sample\src\main\java\com\example
package com.example;
import org.mindrot.jbcrypt.BCrypt;
public class BcryptTest {
    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("pass123", BCrypt.gensalt()));
    }
}