package com.project.judging.Utils;

import com.project.judging.Constant.PasswordGenPattern;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {
        // Access constants directly from PasswordGenPattern
        return RandomStringUtils.random(PasswordGenPattern.PASSWORD_LENGTH, 0, PasswordGenPattern.possibleCharacters.length - 1, false, false, PasswordGenPattern.possibleCharacters, RANDOM);
    }
}
