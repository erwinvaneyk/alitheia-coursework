package eu.sqooss.service.abstractmetric;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.abstractmetric.PluginAdminServiceImpl;
import eu.sqooss.impl.service.abstractmetric.PluginInfoImpl;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import eu.sqooss.service.db.Plugin;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Plugin.class, AlitheiaCore.class})
public class PluginAdminServiceImplTest {

    PluginAdminServiceImpl pa;

    @Before
    public void start() {
        pa = new PluginAdminServiceImpl();
    }

    @Test
    public void testInstallPluginStringNull() {
        String hash = null;

        assertFalse(pa.installPlugin(hash));
    }


    @Test
    public void testUninstallPluginStringNull() {
        String hash = null;

        assertFalse(pa.uninstallPlugin(hash));
    }


    @Test
    public void testListPluginProvidersEmpty() {
        assertEquals(0, pa.listPluginProviders(DAObject.class).size());
    }


    @Test
    public void testInstallPluginLongNull() throws InvalidSyntaxException {
        Long serviceId = null;

        BundleContext bc = mock(BundleContext.class);
        when(bc.getServiceReferences(anyString(), anyString())).thenReturn(null);

        Logger logger = mock(Logger.class);

        pa.setInitParams(bc, logger);

        // Calls getPluginService
        assertFalse(pa.installPlugin(serviceId));
    }

    @Test
    public void testInstallPluginLongWithResult() throws Exception {
        Long serviceId = 123l;

        ServiceReference[] srs = new ServiceReference[1];
        ServiceReference sr = mock(ServiceReference.class);
        srs[0] = sr;

        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        when(ap.install()).thenReturn(true);
        when(ap.getUniqueKey()).thenReturn("123!");

        BundleContext bc = mock(BundleContext.class);
        when(bc.getServiceReferences(anyString(), anyString())).thenReturn(srs);
        when(bc.getService(any(ServiceReference.class))).thenReturn(ap);

        Plugin p = mock(Plugin.class);
        when(p.getHashcode()).thenReturn("hash321!");
        mockStatic(Plugin.class);
        when(Plugin.getPluginByHashcode(any(String.class))).thenReturn(p);

        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);

        assertTrue(pa.installPlugin(serviceId));
        assertEquals(1, pa.listPlugins().size());
        assertEquals(0, pa.listPluginProviders(DAObject.class).size());
    }


    @Test
    public void uninstallPluginLongNull() {
        Long serviceId = null;

        AlitheiaCore ac = mock(AlitheiaCore.class);
        Scheduler s = mock(Scheduler.class);
        when(ac.getScheduler()).thenReturn(s);

        mockStatic(AlitheiaCore.class);
        when(AlitheiaCore.getInstance()).thenReturn(ac);

        assertTrue(pa.uninstallPlugin(serviceId));
    }

    @Test
    public void uninstallPluginLongException() throws Exception{
        Long serviceId = null;

        AlitheiaCore ac = mock(AlitheiaCore.class);
        Scheduler s = mock(Scheduler.class);
        when(ac.getScheduler()).thenReturn(s);
        doThrow(new SchedulerException("")).when(s).enqueue(any(Job.class));

        mockStatic(AlitheiaCore.class);
        when(AlitheiaCore.getInstance()).thenReturn(ac);

        ServiceReference[] srs = new ServiceReference[1];
        ServiceReference sr = mock(ServiceReference.class);
        srs[0] = sr;

        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);

        BundleContext bc = mock(BundleContext.class);
        when(bc.getServiceReferences(anyString(), anyString())).thenReturn(srs);
        when(bc.getService(any(ServiceReference.class))).thenReturn(ap);

        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);

        assertFalse(pa.uninstallPlugin(serviceId));
    }


    @Test
    public void testGetPluginInfoEmpty() {
        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        assertNull(pa.getPluginInfo(ap));
    }

    @Test
    public void testGetPluginInfoNotFound() {
        PluginInfo pi = mock(PluginInfo.class);
        when(pi.getPluginName()).thenReturn("123");
        when(pi.getPluginVersion()).thenReturn("1.2.3");

        List<PluginInfo> c = new ArrayList<>();
        c.add(pi);

        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        when(ap.getName()).thenReturn("456");
        when(ap.getVersion()).thenReturn("4.5.6");

        PluginAdminServiceImpl test = mock(PluginAdminServiceImpl.class);

        when(test.listPlugins()).thenReturn(c);
        assertNull(test.getPluginInfo(ap));
    }

    @Test
    public void testGetPluginInfoFound() {
        PluginInfo pi = new PluginInfoImpl(null);
        pi.setPluginName("123");
        pi.setPluginVersion("1.2.3");

        Collection<PluginInfo> c = mock(Collection.class);
        c.add(pi);

        PluginAdminServiceImpl test = mock(PluginAdminServiceImpl.class);
        when(test.listPlugins()).thenReturn(c);

        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        when(ap.getName()).thenReturn("123");
        when(ap.getVersion()).thenReturn("1.2.3");

        // TODO fix
        assertEquals(null, test.getPluginInfo(ap));
    }

    @Test
    public void testGetPluginInfo() {
        String hash = "123";
        assertNull(pa.getPluginInfo(hash));
    }


    @Test
    public void testGetPluginNull() {
        assertNull(pa.getPlugin(null));
    }


    @Test
    public void testGetPluginObject() {
        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);
        BundleContext bc = mock(BundleContext.class);
        when(bc.getService(any(ServiceReference.class))).thenReturn(ap);

        ServiceReference sr = mock(ServiceReference.class);
        PluginInfo pi = mock(PluginInfo.class);
        when(pi.getServiceRef()).thenReturn(sr);

        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);

        assertEquals(ap, pa.getPlugin(pi));
    }

    @Test
    public void testPluginUpdated() {
        AlitheiaPlugin ap = mock(AlitheiaPlugin.class);

        BundleContext bc = mock(BundleContext.class);
        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);

        pa.pluginUpdated(ap);
    }


    @Test
    public void testStartUp() throws Exception {
        BundleContext bc = mock(BundleContext.class);
        doNothing().when(bc).addServiceListener(any(ServiceListener.class), anyString());

        AlitheiaCore ac = mock(AlitheiaCore.class);
        DBService db = mock(DBService.class);
        when(ac.getDBService()).thenReturn(db);
        mockStatic(AlitheiaCore.class);
        when(AlitheiaCore.getInstance()).thenReturn(ac);

        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);

        assertTrue(pa.startUp());
    }


    @Test
    public void testServiceChanged() throws Exception {
        // vv   same as 'testStartUp'
        BundleContext bc = mock(BundleContext.class);
        doNothing().when(bc).addServiceListener(any(ServiceListener.class), anyString());

        AlitheiaCore ac = mock(AlitheiaCore.class);
        DBService db = mock(DBService.class);
        when(db.startDBSession()).thenReturn(true);
        when(db.commitDBSession()).thenReturn(true);
        when(ac.getDBService()).thenReturn(db);
        mockStatic(AlitheiaCore.class);
        when(AlitheiaCore.getInstance()).thenReturn(ac);

        Logger logger = mock(Logger.class);
        pa.setInitParams(bc, logger);
        // ^^

        // Init 'sobjDB'
        pa.startUp();

        ServiceReference sr = mock(ServiceReference.class);
        pa.serviceChanged(new ServiceEvent(ServiceEvent.MODIFIED_ENDMATCH, sr));
        pa.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sr));
        pa.serviceChanged(new ServiceEvent(ServiceEvent.UNREGISTERING, sr));
        pa.serviceChanged(new ServiceEvent(ServiceEvent.MODIFIED, sr));
    }

    /**
     * PluginAdminServiceImpl test = PowerMock.createPartialMock(PluginAdminServiceImpl.class, "getPluginObject");
     * PowerMock.expectPrivate(test, "getPluginObject", anyObject()).andReturn(ap);
     * replay(test);
     */
}
