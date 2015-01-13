package eu.sqooss.test.service.abstractmetric;

import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.osgi.framework.BundleContext;

import java.util.List;

public class PluginTest {
    private static ExamplePlugin plugin;

    @BeforeClass
    public static void setUp() {
        BundleContext bc = MockOsgi.newBundleContext();
        plugin = new ExamplePlugin(bc);
    }

    @Test
    public void test1() {
        assertTrue(true);
    }
}
