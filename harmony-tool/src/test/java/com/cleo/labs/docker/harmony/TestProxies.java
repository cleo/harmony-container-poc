package com.cleo.labs.docker.harmony;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestProxies {

    @Test
    public void testParseErr() {
        try {
            Proxies proxies = new Proxies();
            proxies.add("foo:bar");
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("vlproxy://host:port;option=value expected: foo:bar", e.getMessage());
        }
    }

    @Test
    public void testOptionErr() {
        try {
            Proxies proxies = new Proxies();
            proxies.add("vlproxy://vlp;nope=value");
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("No enum constant com.cleo.labs.docker.harmony.Proxies.vlproxyOption.Nope", e.getMessage());
        }
    }

    @Test
    public void testProxy() {
        try {
            Proxies proxies = new Proxies();
            proxies.add("vlproxy://vlproxy1;enablereverseproxying=true;loadbalance");
            proxies.write(System.out);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProxyies() {
        try {
            Proxies proxies = new Proxies();
            proxies.add("vlproxy://vlproxy1,vlproxy2;enablereverseproxying=true;loadbalance");
            proxies.write(System.out);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
