package io.github.sunshinewzy.sunnyflow.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SunnyFlowUtil {
	
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		
		if(bytes == null || bytes.length == 0) return null;
		for(byte aByte : bytes){
			int v = aByte & 0xFF;
			String hv = Integer.toHexString(v);
			if(hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		
		return builder.toString();
	}
	
	public static String stringToMD5(String string) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] bytes = md5.digest(string.getBytes(StandardCharsets.UTF_8));
		return bytesToHexString(bytes);
	}
	
}
