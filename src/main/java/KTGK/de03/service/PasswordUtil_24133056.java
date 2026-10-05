package KTGK.de03.service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public final class PasswordUtil_24133056 {
	private PasswordUtil_24133056() {
	}

	public static String hash(String v) {
		try {
			return Base64.getEncoder()
					.encodeToString(MessageDigest.getInstance("SHA-256").digest(v.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}



