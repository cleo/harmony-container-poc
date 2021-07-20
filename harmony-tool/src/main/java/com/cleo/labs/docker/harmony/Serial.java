package com.cleo.labs.docker.harmony;

public class Serial {

    private Serial() { }

    private static String rotate(String s) {
        if (s.matches("[a-zA-Z]{2}\\d{4}")) {
            StringBuffer sb = new StringBuffer(s.toUpperCase());
            int mask = 0;
            for (int i=0; i<sb.length(); i++) mask ^= sb.charAt(i);
            for (int i=0; i<sb.length(); i++) {
                int m = 26-16*((i+2)/4);
                sb.setCharAt(i,(char)((int)(m*1.07+38)+(sb.charAt(i)+mask+12+(int)Math.abs(5-2*i)-(i==2?1:0))%m));
            }
            s = sb.toString();
        }
        return s;
    }

    public static String expand(String serial) {
        if (serial.matches("[A-Z]{2}[0-9]{4}")) {
            return serial+"-"+rotate(serial);
        }
        return serial;
    }

}
