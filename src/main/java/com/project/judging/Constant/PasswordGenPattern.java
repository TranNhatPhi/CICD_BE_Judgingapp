package com.project.judging.Constant;

public class PasswordGenPattern {
    // Make constants public or provide getters
    public static final char[] possibleCharacters = "abcdefghijklmnopqrstuvwxyz0123456789!@".toLowerCase().toCharArray();
    public static final int PASSWORD_LENGTH = 8;
//    #$%^&*()-_=+[{]}|;:',<.>/?
//    ABCDEFGHIJKLMNOPQRSTUVWXYZ
    // Private constructor to prevent instantiation
    private PasswordGenPattern() {
    }
}
