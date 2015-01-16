package eu.sqooss.service.abstractmetric;

import eu.sqooss.impl.service.abstractmetric.PluginInfoImpl;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Plugin.class)
public class PluginInfoImplTest {
    private PluginInfo pi;
    private Set<PluginConfiguration> spc;
    private DBService db;

    @Before
    public void setDB() {
        db = mock(DBService.class);
    }

    private Set<PluginConfiguration> setWithOneElement() {
        PluginConfiguration pc = mock(PluginConfiguration.class);

        spc = new HashSet<>();
        spc.add(pc);

        return spc;
    }


    @Test
     public void defaultConstructorEmptyConfig() {
        pi = new PluginInfoImpl(db);
        assertEquals(0, pi.getConfiguration().size());
    }


    @Test
    public void constructorWithConfig() {
        pi = new PluginInfoImpl(db, setWithOneElement());
        assertEquals(spc, pi.getConfiguration());
    }


    /**@Test
    public void constructorWithConfigAndNull() {
        pi = new PluginInfo(setWithOneElement(), null);
        assertEquals(spc, pi.getConfiguration());
    }*/


    @Test
    public void constructorWithConfigAndNull() {
        pi = new PluginInfoImpl(db, setWithOneElement(), null, null, null);
        assertEquals(spc, pi.getConfiguration());
    }


    /**@Test
    public void constructorWithConfigAndPlugin() {
        String name = "someName";
        String version = "1.2.3";
        Set<Class<? extends DAObject>> activationTypes = new HashSet<>();

        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        when(ap.getName()).thenReturn(name);
        when(ap.getVersion()).thenReturn(version);
        when(ap.getActivationTypes()).thenReturn(activationTypes);

        pi = new PluginInfo(setWithOneElement(), ap);
        assertEquals(spc, pi.getConfiguration());
        assertEquals(name, pi.getPluginName());
        assertEquals(version, pi.getPluginVersion());
        assertEquals(activationTypes, pi.getActivationTypes());
    }*/


    @Test
    public void constructorWithConfigAndPlugin() {
        String name = "someName";
        String version = "1.2.3";
        ActivationTypes activationTypes = new ActivationTypes();

        pi = new PluginInfoImpl(db, setWithOneElement(), name, version, activationTypes);
        assertEquals(spc, pi.getConfiguration());
        assertEquals(name, pi.getPluginName());
        assertEquals(version, pi.getPluginVersion());
        assertEquals(activationTypes, pi.getActivationTypes());
    }


    @Test
    public void testGetConfPropIdInvalidName() {
        pi = new PluginInfoImpl(db);
        assertNull(pi.getConfPropId(null, "type"));
    }


    @Test
    public void testGetConfPropIdInvalidType() {
        pi = new PluginInfoImpl(db);
        assertNull(pi.getConfPropId("name", null));
    }


    @Test
    public void testGetConfPropIdEmptySet() {
        pi = new PluginInfoImpl(db);
        assertNull(pi.getConfPropId("name", "type"));
    }


    @Test
    public void testGetConfPropIdOneElement() {
        long id = 123;
        PluginConfiguration pc = mock(PluginConfiguration.class);
        when(pc.getName()).thenReturn("aName");
        when(pc.getType()).thenReturn("aType");
        when(pc.getId()).thenReturn(id);

        Set<PluginConfiguration> set = new HashSet<>();
        set.add(pc);
        pi = new PluginInfoImpl(db, set);

        assertEquals(id, (long) (pi.getConfPropId("aName", "aType")));
    }


    @Test(expected=Exception.class)
    public void testUpdateConfigEntryException() throws Exception {
        pi.updateConfigEntry(null, null);
    }


    @Test
    public void testUpdateConfigEntryNotFound() throws Exception {
        pi = new PluginInfoImpl(db);
        assertFalse(pi.updateConfigEntry("aName", null));
    }


    @Test
    public void testUpdateConfigEntryFound() throws Exception {
        // Mock PluginConfiguration
        PluginConfiguration pc = mock(PluginConfiguration.class);
        when(pc.getName()).thenReturn("aName");
        when(pc.getType()).thenReturn("INTEGER");
        when(pc.getValue()).thenReturn("1");

        // Create PluginInfo with this PluginConfiguration
        Set<PluginConfiguration> set = new HashSet<>();
        set.add(pc);
        pi = new PluginInfoImpl(db, set);

        // Mock DBService and return the mocked PluginConfiguration
        when(db.attachObjectToDBSession(any(PluginConfiguration.class))).thenReturn(pc);

        assertTrue(pi.updateConfigEntry("aName", "42"));
        verify(pc).setValue("42");
    }


    @Test
    public void testRemoveConfigEntry() throws Exception {
        // Mock PluginConfiguration
        PluginConfiguration pc = mock(PluginConfiguration.class);
        long id = 123;
        when(pc.getName()).thenReturn("aName");
        when(pc.getType()).thenReturn("INTEGER");
        when(pc.getId()).thenReturn(id);

        // Create PluginInfo with this PluginConfiguration
        Set<PluginConfiguration> set = new HashSet<>();
        set.add(pc);
        pi = new PluginInfoImpl(db, set);

        // Mock DBService and return the mocked PluginConfiguration
        when(db.findObjectById(PluginConfiguration.class, id)).thenReturn(pc);
        when(db.deleteRecord(pc)).thenReturn(true);

        assertTrue(pi.removeConfigEntry("aName", "INTEGER"));
    }


    @Test
    public void testAddConfigEntry() throws Exception {
        Plugin p = mock(Plugin.class);

        mockStatic(Plugin.class);
        when(Plugin.getPluginByHashcode(any(String.class))).thenReturn(p);

        pi = new PluginInfoImpl(db);

        assertTrue(pi.addConfigEntry("name", "description", "BOOLEAN", "true"));
        assertFalse(pi.hasConfProp("name", "BOOLEAN"));
    }


    /**
     *    COVERAGE BOOST FTW
     */
    @Test
    public void testGetConfPropIdOneElementNotFound() {
        PluginConfiguration pc = mock(PluginConfiguration.class);
        when(pc.getName()).thenReturn("someName");
        when(pc.getType()).thenReturn("INTEGER");
        when(pc.getValue()).thenReturn("1");

        // Create PluginInfo with this PluginConfiguration
        Set<PluginConfiguration> set = new HashSet<>();
        set.add(pc);
        pi = new PluginInfoImpl(db, set);

        assertNull(pi.getConfPropId("someOtherName", "INTEGER"));
    }


    @Test
    public void testUpdateConfigEntryNotFound2() throws Exception {
        PluginConfiguration pc = mock(PluginConfiguration.class);
        when(pc.getName()).thenReturn("someName");
        when(pc.getType()).thenReturn("INTEGER");

        // Create PluginInfo with this PluginConfiguration
        Set<PluginConfiguration> set = new HashSet<>();
        set.add(pc);
        pi = new PluginInfoImpl(db, set);

        assertFalse(pi.updateConfigEntry("aName", null));
        assertNotNull(pi.toString());
    }


    @Test
    public void testAddActivationType() {
        pi = new PluginInfoImpl(db);
        pi.getActivationTypes().add(DAObject.class);

        assertTrue(pi.getActivationTypes().contains(DAObject.class));
    }

    @Test
    public void testIsActivationTypeFalse() {
        pi = new PluginInfoImpl(db);

        assertFalse(pi.getActivationTypes().contains(DAObject.class));
    }


    @Test(expected = NullPointerException.class)
    public void testCheckConfigValueException() throws Exception {
        pi.addConfigEntry("name", "description", "DOUBLE", null);
    }


    @Test
    public void testServiceRef() {
        pi = new PluginInfoImpl(db);

        ServiceReference serviceRef = mock(ServiceReference.class);
        pi.setServiceRef(serviceRef);
        assertEquals(serviceRef, pi.getServiceRef());
    }


    @Test
    public void testCompareTo() {
        pi = new PluginInfoImpl(db);

        PluginInfo pi1 = new PluginInfoImpl(db);
        pi1.setHashcode("123");
        PluginInfo pi2 = new PluginInfoImpl(db);
        pi2.setHashcode("456");

        assertTrue(pi1.compareTo(pi2) != 0);
    }
}
