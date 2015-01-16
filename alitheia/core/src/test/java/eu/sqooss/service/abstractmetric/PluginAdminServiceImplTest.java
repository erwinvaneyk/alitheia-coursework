package eu.sqooss.service.abstractmetric;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.abstractmetric.PluginAdminServiceImpl;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    /**
     * PluginAdminServiceImpl test = PowerMock.createPartialMock(PluginAdminServiceImpl.class, "getPluginObject");
     * PowerMock.expectPrivate(test, "getPluginObject", anyObject()).andReturn(ap);
     * replay(test);
     */
}
