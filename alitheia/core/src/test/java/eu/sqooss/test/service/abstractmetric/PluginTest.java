package eu.sqooss.test.service.abstractmetric;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;


import org.junit.runner.RunWith;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.File;

//@ExamReactorStrategy(PerClass.class)
@RunWith(PaxExam.class)
public class PluginTest {

    @Inject
    BundleContext context;

    @Configuration
    public Option[] config() {
        File workingDir = new File(System.getProperty("user.dir"));
        if(workingDir.getName().equals("core")) {
            // you are a hack yourself..
            workingDir = workingDir.getParentFile().getParentFile();
        }
        Option[] options = options(
                frameworkProperty("org.osgi.service.http.port").value("8080"),
                frameworkProperty("eu.sqooss.db").value("MySQL"),
                frameworkProperty("eu.sqooss.db.host").value("localhost"),
                frameworkProperty("eu.sqooss.db.schema").value("alitheia"),
                frameworkProperty("eu.sqooss.db.user").value("alitheia"),
                frameworkProperty("eu.sqooss.db.passwd").value("alitheia"),
                frameworkProperty("eu.sqooss.db.conpool").value("c3p0"),
                mavenBundle("org.osgi", "org.osgi.core", "4.2.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.webconsole", "1.2.8"),
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.0.8"),
                mavenBundle("org.apache.felix", "org.apache.felix.http.jetty", "1.0.1"),
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin", "1.0.0"),
                // This is not the dirty hack you are looking for..
                bundle("file:" + workingDir + "/external/org.hibernate.core/target/org.hibernate-3.5.1-Final.jar"),
                mavenBundle("eu.sqooss.alitheia", "core", "0.95-SNAPSHOT"),
                junitBundles()
        );
        return options;
    }

    @Test
    public void checkInject() {
        for(Bundle x : context.getBundles()) {
            System.out.println("Bundle: " + x.getSymbolicName());
        }
        assertNotNull(new Object());
    }
}
