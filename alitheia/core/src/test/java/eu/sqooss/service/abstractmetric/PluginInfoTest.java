package eu.sqooss.service.abstractmetric;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.PluginConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

public class PluginInfoTest {
    private PluginInfo pi;
    private Set<PluginConfiguration> spc;

    @Before
    public void init() {
        pi = new PluginInfo();
    }

    private void setSetWithOneElement() {
        PluginConfiguration pc = mock(PluginConfiguration.class);

        spc = new HashSet<>();
        spc.add(pc);

        pi = new PluginInfo(spc);
    }

    @Test
     public void defaultConstructorEmptyConfig() {
        pi = new PluginInfo();
        assertEquals(0, pi.getConfiguration().size());
    }

    @Test
    public void constructorWithConfig() {
        setSetWithOneElement();
        assertEquals(spc, pi.getConfiguration());
    }

    @Test
    public void testGetConfPropIdInvalidName() {
        assertNull(pi.getConfPropId(null, "type"));
    }

    @Test
    public void testGetConfPropIdInvalidType() {
        assertNull(pi.getConfPropId("name", null));
    }

    @Test
    public void testGetConfPropIdEmptySet() {
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
        pi = new PluginInfo(set);

        assertEquals(id, (long) (pi.getConfPropId("aName", "aType")));
    }

    @Test(expected=Exception.class)
    public void testUpdateConfigEntryException() throws Exception {
        pi.updateConfigEntry(null, null, null);
    }

    @Test
    public void testUpdateConfigEntryNotFound() throws Exception {
        assertFalse(pi.updateConfigEntry(null, "aName", null));
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
        pi = new PluginInfo(set);

        // Mock DBService and return the mocked PluginConfiguration
        DBService dbservice = mock(DBService.class);
        when(dbservice.attachObjectToDBSession(any(PluginConfiguration.class))).thenReturn(pc);

        assertTrue(pi.updateConfigEntry(dbservice, "aName", "42"));
        verify(pc).setValue("42");
    }
}
