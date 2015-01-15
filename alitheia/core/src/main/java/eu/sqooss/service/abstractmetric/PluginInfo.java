package eu.sqooss.service.abstractmetric;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.PluginConfiguration;
import org.osgi.framework.ServiceReference;

import java.util.Set;

/**
 * This class holds runtime and configuration information about single metric
 * plug-in.
 * <br/>
 * Usually an instance of a <code>PluginInfo</code> implementation is created from the
 * <code>PluginAdmin</code> implementation, just after a new metric plug-in
 * bundle is installed in the OSGi framework, who registers a metric
 * plug-in service. Some of the information provided from the metric
 * plug-in object registered with that OSGi service, as well as part of
 * the service's information are copied into this new <code>PluginInfoImpl</code>
 * instance.
 */
public interface PluginInfo extends Comparable<PluginInfo>  {

    /**
     * This enumeration includes all permitted types of configuration values,
     * that a metrics can support. The various configuration parameters and
     * their values are used mostly from internal metric processes, like
     * results rendering and validation.
     */
    public enum ConfigurationType {
        INTEGER,
        STRING,
        BOOLEAN,
        DOUBLE
    }

    /**
     * Initializes the configuration set that is available for this plug-in.
     *
     * @param c the plug-in configuration set
     */
    public void setPluginConfiguration (Set<PluginConfiguration> c);

    /**
     * Returns the list of existing metric configuration parameters.
     *
     * @return The list of configuration parameters.
     */
    public Set<PluginConfiguration> getConfiguration();

    /**
     * Returns the Id of the given configuration property.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return The property's Id, or <code>null</code> if the property does
     *   not exist.
     */
    public Long getConfPropId (String name, String type);

    /**
     * Verifies, if the specified configuration property exist in this
     * plug-in's information object.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return <code>true</code>, if such property is found,
     *   or <code>false</code> otherwise.
     */
    public boolean hasConfProp (String name, String type);

    /**
     * Sets a new value of existing metric plugins configuration property
     * by creating a new database record.
     *
     * @param name the configuration property's name
     * @param newVal the new value, that should be assigned to the
     *   selected configuration property
     *
     * @return <code>true</code> upon successful update, of <code>false</code>
     *   when a corresponding database record does not exist.
     *
     * @throws Exception upon incorrect value's syntax, or
     *   invalid property's type.
     */
    public boolean updateConfigEntry(String name, String newVal) throws IllegalArgumentException;

    /**
     * Adds a new configuration property for this metric plug-in by creating
     * a new database record for it.
     *
     * @param name the configuration property's name
     * @param description the configuration property's description
     * @param type the configuration property's type
     * @param value the configuration property's value
     *
     * @return <code>true</code> upon successful append, of <code>false</code>
     *   when a corresponding database record can not be created.
     *
     * @throws Exception upon incorrect value's syntax,
     *   invalid property's type, or invalid property's name.
     */
    public boolean addConfigEntry(String name, String description, String type, String value) throws IllegalArgumentException;

    /**
     * Removes an existing configuration property of this metric plug-in by
     * deleting its database record.
     *
     * @param name the configuration property's name
     * @param type the configuration property's type
     *
     * @return <code>true</code> upon successful remove, or <code>false</code>
     *   when a corresponding database record can not be found.
     *
     * @throws Exception upon invalid property's type or name.
     */
    public boolean removeConfigEntry(String name, String type) throws IllegalArgumentException;

    /**
     * Sets the metrics name. In practice the <code>metricName</code>
     * parameter must be equal with the name of the associated metric
     * plug-in.
     *
     * @param metricName - the metric name
     */
    public void setPluginName(String metricName);

    /**
     * Returns the metric name stored in this <code>MetricInfo</code>
     * object.
     *
     * @return Metric name.
     */
    public String getPluginName();

    /**
     * Sets the metrics version. In practice the <code>metricVersion</code>
     * parameter must be equal with the version of the associated metric
     * plug-in.
     *
     * @param metricVersion - a metric version
     */
    public void setPluginVersion(String metricVersion);

    /**
     * Returns the metric version stored in this <code>MetricInfo</code>
     * object.
     *
     * @return Metric version.
     */
    public String getPluginVersion();

    /**
     * Initializes the corresponding local field with the reference to the
     * service, that registered the associated metric plug-in.
     *
     * @param serviceRef - the service reference
     */
    public void setServiceRef(ServiceReference serviceRef);

    /**
     * Returns the service reference that points to the associated metric
     * plug-in.
     *
     * @return The service reference.
     */
    public ServiceReference getServiceRef();

    /**
     * Sets the hash code's value of this <code>MetricInfo</code> instance.
     * <br/>
     * The value must be unique, which means that no other
     * <code>MetricInfo</code> with the same hash code should be kept by
     * the <code>PluginAdmin</code> instance that created this object.
     *
     * @param hashcode - the hash code's value of this object
     */
    public void setHashcode(String hashcode);

    /**
     * Returns the hash code's value of this <code>MetricInfo</code> instance.
     *
     * @return The hash code's value of this object.
     */
    public String getHashcode();

    public boolean isInstalled();

    public void setInstalled(boolean isInstalled);

    // ActivationTypes
    /**
     * Sets the list of supported activation interfaces (types). In practice
     * the given list must contain the same entries like those supported by
     * the associated metric plug-in.<br/>
     * <br/>
     * Note: Any previous entries in this list will be deleted by this action.
     *
     * @param activationTypes - the list of supported activation interfaces
     */
    public void setActivationTypes(ActivationTypes activationTypes);

    /**
     * Returns the list off all activation interfaces (types) supported by the
     * associated metric plug-in.
     *
     * @return - the list of supported activation interfaces
     */
    public ActivationTypes getActivationTypes();
}
