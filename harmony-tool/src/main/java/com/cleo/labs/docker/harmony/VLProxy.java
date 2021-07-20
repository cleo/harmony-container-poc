package com.cleo.labs.docker.harmony;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class VLProxy {

    private enum VLProperty {
        configPwd                    ("Configuration Password", true, false),
        serialNumbers                ("Serial Numbers"),
        proxyHTTPPorts               ("Internal Forward Proxy HTTP Ports"),
        internalFTPAddress           ("Internal Address", false, true),
        internalAddress              ("Internal Address"),
        internalNetworkIDs           ("Internal Network IDs"),
        externalAddress              ("External Address"),
        reverseProxyHTTPPorts        ("External Reverse Proxy HTTP Ports"),
        reverseProxyHTTPsPorts       ("External Reverse Proxy HTTPs Ports"),
        reverseProxyFTPPorts         ("External Reverse Proxy FTP Ports"),
        reverseProxyExplicitFTPsPorts("External Reverse Proxy FTPs Explicit Ports"),
        reverseProxyImplicitFTPsPorts("External Reverse Proxy FTPs Implicit Ports"),
        reverseProxyFTPDataPorts     ("External Reverse Proxy FTP Data Ports"),
        reverseProxySSHFTPPorts      ("External Reverse Proxy SSH FTP Ports"),
        reverseProxyOFTPPorts        ("External Reverse Proxy OFTP Ports"),
        reverseProxyOFTPsPorts       ("External Reverse Proxy OFTPs Ports"),
        vlReadTimeout                ("VersaLex Read Timeout (seconds)"),
        remoteReadTimeout            ("Remote Read Timeout (seconds)"),
        portBacklog                  ("Connection Backlog Size"),
        mailServerAddress            ("SMTP Mail Server Address"),
        mailServerUsername           ("SMTP Mail Server Username"),
        mailServerPassword           ("SMTP Mail Server Password"),
        emailOnFailAddr              ("Email on Fail Addresses"),
        executeOnFailCommand         ("Execute on Failure Command"),
        maxLogFileSize               ("Max Log File Size (Mb)"),
        logExternalAddress           ("Log External Address"),
        unknownPartnerMsgAction      ("Unknown Partner Message Action"),
        reverseProxyLoadBalancing    ("Reverse Proxy Load Balancing"),
        reverseProxyRetry            ("Reverse Proxy Retry"),
        internalFTPMask              ("Internal FTP Mask", false, true);

        private String label;
        private boolean mask;
        private boolean deprecated;
        private VLProperty(String label) {
            this.label = label;
            this.mask = false;
            this.deprecated = false;
        }
        private VLProperty(String label, boolean mask, boolean deprecated) {
            this.label = label;
            this.mask = mask;
            this.deprecated = deprecated;
        }
        @SuppressWarnings("unused")
        public String label() {
            return label;
        }
        @SuppressWarnings("unused")
        public boolean deprecated() {
            return deprecated;
        }
        @SuppressWarnings("unused")
        public boolean mask() {
            return mask;
        }
    }

    @SuppressWarnings("serial")
    private static class VLProperties extends EnumMap<VLProperty,String> {
        public VLProperties(byte[] properties) throws IOException {
            super(VLProperty.class);
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(properties));
            props.forEach((k,v) ->
                put(VLProperty.valueOf((String)k), v.toString()));
        }

        private static int maxPropertyLabelLength () {
            return EnumSet.allOf(VLProperty.class)
                .stream()
                .map(VLProperty::label)
                .map(String::length)
                .reduce(0, Math::max);
        }

        private String nullToEmpty(String s) {
            return s == null ? "" : s;
        }

        private static final String MASK = "******";

        private void print(PrintStream out) throws IOException {
            String format = String.format("%%2d. %%-%ds: %%s", maxPropertyLabelLength());
            int index = 1;
            for (VLProperty p : EnumSet.allOf(VLProperty.class)) {
                if (!p.deprecated) {
                    out.println(String.format(format, index, p.label(),
                            p.mask() ? MASK : nullToEmpty(get(p))));
                    index++;
                }
            }
        }

        private void store(OutputStream out) throws IOException {
            Properties properties = new Properties();
            EnumSet.allOf(VLProperty.class)
                .forEach(p -> properties.put(p.name(), nullToEmpty(get(p))));
            properties.store(out, "VLProxy properties");
        }

        public VLProperties() {
            super(VLProperty.class);
            put(VLProperty.configPwd, "Admin");
            put(VLProperty.proxyHTTPPorts, "8080");
            put(VLProperty.vlReadTimeout, "150");
            put(VLProperty.remoteReadTimeout, "150");
            put(VLProperty.portBacklog, "50");
            put(VLProperty.maxLogFileSize, "5");
            put(VLProperty.logExternalAddress, "No");
            put(VLProperty.unknownPartnerMsgAction, "Defer");
            put(VLProperty.reverseProxyLoadBalancing, "No");
            put(VLProperty.reverseProxyRetry, "No");
        }
    }

    private static SecretKey getKey() throws Exception {
        String secretKeyFactory = "PBEWithSHA1AndDESede";
        String passPhrase = "VersaLex by CLEO";

        // create a KeySpec from our password
        PBEKeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
        SecretKeyFactory kf = SecretKeyFactory.getInstance(secretKeyFactory);
        return kf.generateSecret(keySpec);
        
    }

    private static Cipher getCipher(int mode, SecretKey sk) throws Exception {
        String algorithm = "PBEWithSHA1AndDESede";
        byte[] salt = {(byte) 0x10, (byte) 0x18, (byte) 0x19, (byte) 0x63,
                       (byte) 0x11, (byte) 0x08, (byte) 0x19, (byte) 0x61 };
        int iterationCount = 19;
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        Cipher cipher = Cipher.getInstance(algorithm);

        // initialize the cipher
        cipher.init(mode, sk, paramSpec);

        return cipher;
    }

    public static byte[] encode(List<String> args) throws Exception { 
        VLProperties properties = new VLProperties();
        for (String arg : args) {
            String[] kv = arg.split("=", 2);
            VLProperty property = VLProperty.valueOf(kv[0]);
            if (kv.length == 1) {
                properties.remove(property);
            } else if (property == VLProperty.serialNumbers) {
                String serials = Stream.of(kv[1].split(","))
                        .map(Serial::expand)
                        .collect(Collectors.joining(","));
                properties.put(VLProperty.serialNumbers, serials);
            } else {
                properties.put(property, kv[1]);
            }
        }
        properties.print(System.err);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        properties.store(output);
        SecretKey sk = getKey();
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, sk);
        byte[] encrypted = cipher.doFinal(output.toByteArray());
        return encrypted;
    }

}
