package com.cleo.labs.docker.harmony;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import com.cleo.lexicom.certmgr.external.CertificateInfo;
import com.cleo.lexicom.certmgr.external.CertificateInfo.PubKeyAlg;
import com.cleo.lexicom.certmgr.external.ICertManagerRunTime;
import com.cleo.lexicom.external.ILexiCom;
import com.cleo.lexicom.external.LexiComFactory;

public class VersaLex {
    private static File     home;
    private static ILexiCom lexicom = null;

    private static void connect() throws Exception {
        if (lexicom == null) {
            String cleohome = System.getenv("CLEO_HOME");
            if (cleohome==null || cleohome.isEmpty()) {
                cleohome = ".";
            }
            home = new File(cleohome);
            lexicom = LexiComFactory.getVersaLex(LexiComFactory.HARMONY, home.getAbsolutePath(), LexiComFactory.CLIENT_OR_SERVER);
        }
    }

    public static void trust(String fn) throws Exception {
        connect();
        ICertManagerRunTime certManager = lexicom.getCertManager();
        @SuppressWarnings("unchecked")
        Collection<X509Certificate> certs = certManager.readCert(new File(fn));
        certManager.trustCACert(certs.toArray(new X509Certificate[certs.size()]));
    }

    public static void importP12(String alias, String fn, String password) throws Exception {
        connect();
        ICertManagerRunTime certManager = lexicom.getCertManager();
        certManager.importUserPKCS12(alias, new File(fn), password, /*replace*/true, /*addPasssword*/false);
    }

    public static void generateKey(String alias, String password, List<String> properties) throws Exception {
        connect();
        alias = alias.toUpperCase();
        CertificateInfo info = new CertificateInfo();
        info.setSignatureAlgorithm(CertificateInfo.SHA256);
        info.setPublicKeyAlgorithm(PubKeyAlg.RSA);
        info.setStrength(2048);
        info.setSubjectKeyIdentifier(true);
        for (String property : properties) {
            String[] kv = property.split("=", 2);
            if (kv.length==2) {
                switch (kv[0].toLowerCase()) {
                case "cn":     info.setCommonName(kv[1]);                break;
                case "c":      info.setCountry(kv[1]);                   break;
                case "email":  info.setEmailAddress(kv[1]);              break;
                case "l":      info.setLocality(kv[1]);                  break;
                case "o":      info.setOrganization(kv[1]);              break;
                case "ou":     info.setOrganizationalUnit(kv[1]);        break;
                case "st":     info.setStateOrProvince(kv[1]);           break;
                case "months": info.setValidFor(Integer.valueOf(kv[1])); break;
                default:
                    throw new Exception("unrecognized certificate property: "+kv[0]);
                }
            } else {
                switch (kv[0].toLowerCase()) {
                case "md5":    info.setSignatureAlgorithm(CertificateInfo.MD5);    break;
                case "sha1":   info.setSignatureAlgorithm(CertificateInfo.SHA1);   break;
                case "sha256": info.setSignatureAlgorithm(CertificateInfo.SHA256); break;
                case "sha384": info.setSignatureAlgorithm(CertificateInfo.SHA384); break;
                case "sha512": info.setSignatureAlgorithm(CertificateInfo.SHA512); break;
                case "rsa":    info.setPublicKeyAlgorithm(PubKeyAlg.RSA);          break;
                case "dsa":    info.setPublicKeyAlgorithm(PubKeyAlg.DSA);          break;
                case "512":    info.setStrength(512);                              break;
                case "1024":   info.setStrength(1024);                             break;
                case "2048":   info.setStrength(2048);                             break;
                case "3072":   info.setStrength(3072);                             break;
                case "4096":   info.setStrength(4096);                             break;
                case "keyencipherment":  info.setKeyEncipherment(true);            break;
                case "digitalsignature": info.setDigitalSignature(true);           break;
                case "serverauth":       info.setServerAuth(true);                 break;
                case "clientauth":       info.setClientAuth(true);                 break;
                default:
                    throw new Exception("unrecognized certificate property: "+kv[0]);
                }
            }
        }
        ICertManagerRunTime certManager = lexicom.getCertManager();
        certManager.generateUserCertKey(alias, info, password, /*replace*/true);
    }

}
