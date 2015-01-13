package eu.sqooss.test.service.abstractmetric;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import org.osgi.framework.BundleContext;

public class ExamplePlugin extends AbstractMetric {

    /**
     * Init basic services common to all implementing classes
     *
     * @param bc - The bundle context of the implementing metric - to be passed
     *           by the activator.
     */
    protected ExamplePlugin(BundleContext bc) {
        super(bc);
    }
}
