package com.cleo.labs.docker.harmony;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.cleo.security.hashing.VLPBKDF2Authenticator;

public class Tool {

    private static String pbkdf2(String password) throws Exception {
        return VLPBKDF2Authenticator.SCHEME_NAME+":"+VLPBKDF2Authenticator.createHash(password);
    }

    private static Consumer<List<String>> pbkdf2 = (args -> 
        args.forEach(s -> {
            try {
                System.out.println(pbkdf2(s));
            } catch (Exception e) {
                System.err.println("error hashing "+s);
                e.printStackTrace();
                System.exit(1);
            }
        }));

    private static Consumer<List<String>> serial = (args -> 
        args.forEach(s -> {
            System.out.println(Serial.expand(s));
        }));

    private static Consumer<List<String>> vlpconfig = (args -> 
        {
            try {
                byte[] config = VLProxy.encode(args);
                System.out.write(config);
                System.out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    
    private static Consumer<List<String>> trust = (args -> 
        args.forEach(s -> {
            try {
                VersaLex.trust(s);
                System.err.println(s+" imported as trusted CA");
            } catch (Exception e) {
                System.err.println("error importing "+s+" as trusted CA");
                e.printStackTrace();
                System.exit(1);
            }
        }));

    private static Consumer<List<String>> importp12 = (args -> 
    {
        // importp12 alias file password
        if (args.size() != 3) {
            System.err.println("usage: importp12 alias file password");
            System.exit(1);
        } else {
            String alias = args.get(0);
            String file = args.get(1);
            String password = args.get(2);
            try {
                VersaLex.importP12(alias, file, password);
                System.err.println(file+" imported as p12 key");
            } catch (Exception e) {
                System.err.println("error importing "+file+" as p12 key");
                e.printStackTrace();
                System.exit(1);
            }
        }
    });

    private static Consumer<List<String>> generatekey = (args -> 
    {
        // generateKey alias password properties
        if (args.size() < 2) {
            System.err.println("usage: generatekey alias password properties...");
            System.exit(1);
        } else {
            String alias = args.get(0);
            String password = args.get(1);
            try {
                VersaLex.generateKey(alias, password, args.subList(2, args.size()));
                System.err.println("generated "+alias);
            } catch (Exception e) {
                System.err.println("error generating "+alias);
                e.printStackTrace();
                System.exit(1);
            }
        }
    });

    private static Consumer<List<String>> proxies = (args ->
    {
        try {
            Proxies proxies = new Proxies();
            for (String proxy : args) {
                proxies.add(proxy);
            }
            String cleohome = System.getenv("CLEO_HOME");
            if (cleohome==null || cleohome.isEmpty()) {
                cleohome = ".";
            }
            proxies.write(Paths.get(cleohome, "conf", "Proxies.xml"));
        } catch (Exception e) {
            System.err.println("error processing proxies");
            e.printStackTrace();
            System.exit(1);
        }
    });

    public static void main(String[] argv) {
        List<String> args = Arrays.asList(argv);
        Consumer<List<String>> processor = null;
        // make sure there are arguments else fail
        if (args.isEmpty()) {
            System.err.println("nothing to do");
            System.exit(1);
        }
        // map command name to processor else fail
        String command = args.get(0);
        switch (command.toLowerCase()) {
        case "pbkdf2":
            processor = pbkdf2;
            break;
        case "serial":
            processor = serial;
            break;
        case "vlpconfig":
            processor = vlpconfig;
            break;
        case "trust":
            processor = trust;
            break;
        case "importp12":
            processor = importp12;
            break;
        case "generatekey":
            processor = generatekey;
            break;
        case "proxies":
            processor = proxies;
            break;
        default:
            System.err.println("unrecognized command: "+command);
            System.exit(1);
        }
        // make sure there are arguments for command else fail
        if (args.size() <= 1) {
            System.err.println("no arguments");
            System.exit(1);
        }
        // process and exit
        args = args.subList(1, args.size());
        processor.accept(args);
        System.exit(0);
    }
}
