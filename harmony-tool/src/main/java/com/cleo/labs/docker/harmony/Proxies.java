package com.cleo.labs.docker.harmony;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Proxies {

    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private Document proxies;
    private Element root;

    public Proxies() throws Exception {
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        proxies = docBuilder.newDocument();
        root = proxies.createElement("Proxies");
        proxies.appendChild(root);
    }

    private enum vlproxyOption {
        Backupproxy               ("false"),
        Enablereverseproxying     ("true"),
        Forwardproxygroup         (""),
        Loadbalance               ("true"),
        Reverseforwardconnections ("false"),
        Uselistenersshcerts       ("true"),
        Uselistenersslcerts       ("true"),
        Usesamecerts              ("false");

        private final String defaultValue;
        private vlproxyOption(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        public String defaultValue() {
            return defaultValue;
        }
    };

    private static String toInitialCapital(String s) {
        if (s==null || s.isEmpty()) {
            return s;
        }
        return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
    }

    private static String orDefault(String s, String orDefault) {
        if (s==null || s.isEmpty()) {
            return orDefault;
        }
        return s;
    }
    
    private static final Pattern VLPROXY = Pattern.compile("vlproxy://([^:;]+)(?::(\\d+))?(?:;(.*))?");

    public void add(String spec) throws Exception {
        // vlproxy://host:port;option=value;option=value
        Matcher m = VLPROXY.matcher(spec);
        if (!m.matches()) {
            throw new IllegalArgumentException("vlproxy://host:port;option=value expected: "+spec);
        }
        String addresses = m.group(1);
        for (String address : addresses.split(",")) {
            Element proxy = proxies.createElement("Proxy");
            proxy.setAttribute("address", address);
            proxy.setAttribute("port", orDefault(m.group(2), "8080"));
            proxy.setAttribute("type", "http");
            EnumMap<vlproxyOption,String> props = new EnumMap<>(vlproxyOption.class);
            EnumSet.allOf(vlproxyOption.class).forEach(o -> props.put(o, o.defaultValue()));
            String list = m.group(3);
            if (list!=null && !list.isEmpty()) {
                String[] options = list.split(";");
                for (String option : options) {
                    String[] kv = option.split("=",2);
                    String value = kv.length > 1 ? kv[1] : "true";
                    // note: valueOf will throw IllegalArgumentException for unknowns
                    props.put(vlproxyOption.valueOf(toInitialCapital(kv[0])), value);
                }
            }
            for (vlproxyOption o : EnumSet.allOf(vlproxyOption.class)) {
                if (props.get(o)!=null && !props.get(o).isEmpty()) {
                    Element prop = proxies.createElement(o.name());
                    prop.setTextContent(props.get(o));
                    proxy.appendChild(prop);
                }
            }
            Element Usingvlproxy = proxies.createElement("Usingvlproxy");
            Usingvlproxy.setTextContent("true");
            proxy.appendChild(Usingvlproxy);
            root.appendChild(proxy);
        }
    }

    private static String xml2string(Node doc) throws TransformerException {
       DOMSource domSource = new DOMSource(doc);
       StringWriter writer = new StringWriter();
       StreamResult result = new StreamResult(writer);
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
       transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
       transformer.transform(domSource, result);
       writer.flush();
       return writer.toString();
    }

    public void write(PrintStream o) throws Exception {
        o.print(xml2string(proxies));
    }

    public void write(Path path) throws Exception {
        Files.copy(new ByteArrayInputStream(xml2string(proxies).getBytes()), path, StandardCopyOption.REPLACE_EXISTING);
    }
}
