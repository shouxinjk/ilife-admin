package com.pcitech.iLife.util;

import java.util.UUID;

public class Util {
	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	
	public static String md5(String source) {
		return md5(source.getBytes());
	}
	
    public static String md5(byte[] source) {  
        String s = null;  
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符  
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};  
        try {  
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");  
            md.update(source);  
            byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，  
            // 用字节表示就是 16 个字节  
            char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，  
            // 所以表示成 16 进制需要 32 个字符  
            int k = 0;                                // 表示转换结果中对应的字符位置  
            for (int i = 0; i < 16; i++) {    // 从第一个字节开始，对 MD5 的每一个字节  
                // 转换成 16 进制字符的转换  
                byte byte0 = tmp[i];  // 取第 i 个字节  
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换,  
                // >>> 为逻辑右移，将符号位一起右移  
                str[k++] = hexDigits[byte0 & 0xf];   // 取字节中低 4 位的数字转换  
            }  
            s = new String(str);  // 换后的结果转换为字符串  
   
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return s;  
    } 
    
    //生成8位大小写随机码
    public static String get8bitCode() {
    	String get32uuid = get32UUID();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			sb.append(get32uuid.charAt((int) Math.round(Math.random() * 31)));
		}
		String KeyString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		sb.insert((int)Math.round(Math.random()*3), KeyString.charAt((int) Math.round(Math.random() * 25)));
		sb.insert((int)Math.round(Math.random()*4), KeyString.charAt((int) Math.round(Math.random() * 25)));
		String get32uuid2 = get32UUID();
		sb.insert((int)Math.round(Math.random()*5),get32uuid2.charAt((int) Math.round(Math.random() * 31)));
		sb.insert((int)Math.round(Math.random()*6),get32uuid2.charAt((int) Math.round(Math.random() * 31)));
		return sb.toString();
    }
    
    //生成6位大小写随机码
    public static String get6bitCode() {
    	String get32uuid = get32UUID();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			sb.append(get32uuid.charAt((int) Math.round(Math.random() * 31)));
		}
		String KeyString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		sb.insert((int)Math.round(Math.random()*3), KeyString.charAt((int) Math.round(Math.random() * 25)));
//		sb.insert((int)Math.round(Math.random()*4), KeyString.charAt((int) Math.round(Math.random() * 25)));
		String get32uuid2 = get32UUID();
		sb.insert((int)Math.round(Math.random()*5),get32uuid2.charAt((int) Math.round(Math.random() * 31)));
//		sb.insert((int)Math.round(Math.random()*6),get32uuid2.charAt((int) Math.round(Math.random() * 31)));
		return sb.toString();
    }
    
    //根据输入字符串生成8位大小写随机码：输入字符串相同则返回相同
    public static String get8bitCode(String seed) {
    	if(seed==null)
    		return get8bitCode();
    	String get32uuid = md5(seed);
		StringBuffer sb = new StringBuffer();
		int[] indexes = {0,1,8,9,16,17,24,25};//取固定位置字符
		for (int i = 0; i < indexes.length; i++) {
			sb.append(get32uuid.charAt(indexes[i]));
		}
		return sb.toString();
    }
    
    //根据输入字符串生成6位大小写随机码：输入字符串相同则返回相同
    public static String get6bitCode(String seed) {
    	if(seed==null)
    		return get6bitCode();
    	String get32uuid = md5(seed);
		StringBuffer sb = new StringBuffer();
		int[] indexes = {0,1,8,9,16,17};//取固定位置字符
		for (int i = 0; i < indexes.length; i++) {
			sb.append(get32uuid.charAt(indexes[i]));
		}
		return sb.toString();
    }
}
