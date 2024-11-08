package com.peninsula.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Utils {
	private static final Argon2 argon2=Argon2Factory.create();
	
	@SuppressWarnings("deprecation")
	public static String hashPassword(String password) {
		return argon2.hash(2, 65536, 1, password);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean verifyPassword(String password,String hashPassword) {
		return argon2.verify(hashPassword, password);
	}
	
	//for generating random password length of 8
	public static String generatePassword() {
		 	String ALL_CHARS = "ABCDEFGHJLMNPQRTabcdefghijmnpqrt23456789@#";
		return RandomStringUtils.random(8, ALL_CHARS);
	}
}
