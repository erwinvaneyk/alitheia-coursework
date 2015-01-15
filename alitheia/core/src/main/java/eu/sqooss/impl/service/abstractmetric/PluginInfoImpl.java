/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.abstractmetric;

import java.util.HashSet;
import java.util.Set;

import eu.sqooss.service.abstractmetric.ActivationTypes;
import eu.sqooss.service.abstractmetric.PluginInfo;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.util.StringUtils;

/**
 * This class holds runtime and configuration information about single metric
 * plug-in.
 * <br/>
 * Usually an instance of a <code>PluginInfoImpl</code> is created from the
 * <code>PluginAdmin</code> implementation, just after a new metric plug-in
 * bundle is installed in the OSGi framework, who registers a metric
 * plug-in service. Some of the information provided from the metric
 * plug-in object registered with that OSGi service, as well as part of
 * the service's information are copied into this new <code>PluginInfoImpl</code>
 * instance.
 */
public class PluginInfoImpl implements PluginInfo, Comparable<PluginInfo> {

    /**
     * The service reference of the service that registered this metric
     * plug-in
     */
    private ServiceReference serviceRef = null;

    /**
     * The name of the associated  metric plug-in
     */
    private String pluginName = null;

    /**
     * The version of the associated metric plug-in
     */
    private String pluginVersion = null;

    /**
     * This list include all activation interfaces supported by the associated
     * metric plug-in.
     * <br/>
     * The list of permitted activation interfaces is described in the
     * {@link eu.sqooss.service.abstractmetric.AlitheiaPlugin} interface and currently includes:
     */
    private ActivationTypes activationTypes = new ActivationTypes();

    /**
     * The hash code's value of the associated metric metric plug-in.
     * <br/>
     * After a new metric plug-in is registered as service in the OSGi
     * framework, the <code>PluginAdmin</code> initializes this field with
     * the service's ID value, by calling the <code>setHashcode(String)</code>
     * method.
     * <br/>
     * Once the metric plug-in's <code>install()</code> method is called,
     * the <code>PluginAdmin</code> replaces the old <code>PluginInfoImpl</code>
     * with a new one, whose <code>hashcode</code> field is initialized with
     * the hash code's value, that this metric plug-in stored in its database
     * record.
     */
    private String hashcode;

    /**
     * A list containing the current set of configuration parameters of the
     * associated metric plug-in
     */
    private Set<PluginConfiguration> config = new HashSet<>();

    /**
     * This flag is set to <code>false<code> on a newly registered metric
     * plug-ins, and changed to <code>true</code> after the metric plug-in's
     * <code>install()</code> method is called (and successfully performed).
     */
    private boolean installed = false;

    private DBService db;

    /**
     * Empty constructor.
     */
    public PluginInfoImpl(DBService db) {
        this.db = db;
    }

    /**
     * Simple constructor, that creates a new <code>PluginInfoImpl</code> instance
     * and initializes it with the given metric plug-in's configuration
     * parameters.
     *
     * @param c - the list of configuration parameters
     */
    public PluginInfoImpl(DBService db, Set<PluginConfiguration> c) {
        this.db = db;
        setPluginConfiguration(c);
    }

    /**
     * Creates a new <code>PluginInfoImpl</code> instance, and initializes it with
     * the given metric plug-in's configuration parameters and the description
     * fields found in the given plug-in instance.
     *
     * @param c - the list of configuration parameters
     * @param name the name of the plugin
     * @param version the version of the plugin
     * @param activationTypes the activationTypes of the plugin
     */
    public PluginInfoImpl(DBService db, Set<PluginConfiguration> c, String name, String version, ActivationTypes activationTypes) {
        this.db = db;
        setPluginConfiguration(c);
        setPluginName(name);
        setPluginVersion(version);
        setActivationTypes(activationTypes);
    }


    /**
     * Initializes the configuration set that is available for this plug-in.
     * 
     * @param c the plug-in configuration set
     */
    public void setPluginConfiguration (Set<PluginConfiguration> c) {
        this.config = c;
    }

    /**
     * Returns the list of existing metric configuration parameters.
     *
     * @return The list of configuration parameters.
     */
    public Set<PluginConfiguration> getConfiguration() {
        return this.config;
    }

    /**
     * Returns the Id of the given configuration property.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return The property's Id, or <code>null</code> if the property does
     *   not exist.
     */
    public Long getConfPropId (String name, String type) {
        // Search for a matching property
        for (PluginConfiguration property : config) {
            if ((property.getName().equals(name))
                    && (property.getType().equals(type))) {
                return property.getId();
            }
        }
        return null;
    }

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
    public boolean hasConfProp (String name, String type) {
        return getConfPropId(name, type) != null;
    }

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
    public boolean updateConfigEntry(String name, String newVal)
        throws IllegalArgumentException {
        // Check for an invalid name
        if (name == null) {
            throw new IllegalArgumentException("Invalid name: " + name + "!");
        }
        // Check if such configuration property exists
        for (PluginConfiguration pc : config) {
            if (pc.getName().equals(name)) {
                // Retrieve the configuration property's type
                ConfigurationType type =
                    ConfigurationType.valueOf(pc.getType());
                // Check for invalid value/type combinations
                checkConfigValue(type, newVal);

                // Update the given configuration property
                pc = db.attachObjectToDBSession(pc);
                pc.setValue(newVal);
                return true;
            }
        }
        return false;
    }

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
    public boolean addConfigEntry(
            String name,
            String description,
            String type,
            String value)
    throws IllegalArgumentException {
        // Check for an invalid name
        if (name == null) {
            throw new IllegalArgumentException("Invalid name: " + name + "!");
        }
        // Check for invalid value/type combinations
        checkConfigValue(ConfigurationType.valueOf(type), value);

        // Add the new configuration property
        PluginConfiguration newParam =
            new PluginConfiguration();
        newParam.setName(name);
        newParam.setMsg((description != null) ? description : "");
        newParam.setType(type);
        newParam.setValue(value);
        Plugin p = Plugin.getPluginByHashcode(hashcode);
        newParam.setPlugin(p);
        return p.getConfigurations().add(newParam);
    }

    /**
     * Checks if the string-based value is compatible with the given type
     *
     * @param type based on the specified ConfigurationTypes
     * @param value a string-based value, which should be compatible with <code>type</code>.
     * @throws IllegalArgumentException the value is not compatible
     */
    private void checkConfigValue(ConfigurationType type, String value) throws IllegalArgumentException {
        try {
            // Creative NullPointerException-checks
            value.hashCode();
            type.hashCode();
            // Given the value check if it is compatible with the given type
            switch (type) {
                case BOOLEAN:
                    if ((!value.equals("true"))
                            && (!value.equals("false"))) {
                        throw new Exception("Not a valid boolean value!");
                    }
                    break;
                case INTEGER:
                    Integer.valueOf(value);
                    break;
                case DOUBLE:
                    Double.valueOf(value);
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(value + " is not a valid " + type + " value!", e);
        }
    }

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
    public boolean removeConfigEntry(
            String name,
            String type)
    throws IllegalArgumentException {
        // Check for an invalid name
        if (name == null) {
            throw new IllegalArgumentException("Invalid name: " + name + "!");
        }

        // Check for invalid type
        ConfigurationType.valueOf(type);

        // Get the property's Id
        Long propId = getConfPropId(name, type);
        if (propId != null) {
            // Remove the specified configuration property
            PluginConfiguration prop = db.findObjectById(
                    PluginConfiguration.class, propId);

            return ((prop != null) && (db.deleteRecord(prop)));
        }

        return false;
}

    /**
     * Sets the metrics name. In practice the <code>metricName</code>
     * parameter must be equal with the name of the associated metric
     * plug-in.
     *
     * @param metricName - the metric name
     */
    public void setPluginName(String metricName) {
        this.pluginName = metricName;
    }

    /**
     * Returns the metric name stored in this <code>MetricInfo</code>
     * object.
     *
     * @return Metric name.
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Sets the metrics version. In practice the <code>metricVersion</code>
     * parameter must be equal with the version of the associated metric
     * plug-in.
     *
     * @param metricVersion - a metric version
     */
    public void setPluginVersion(String metricVersion) {
        this.pluginVersion = metricVersion;
    }

    /**
     * Returns the metric version stored in this <code>MetricInfo</code>
     * object.
     *
     * @return Metric version.
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * Sets the list of supported activation interfaces (types). In practice
     * the given list must contain the same entries like those supported by
     * the associated metric plug-in.<br/>
     * <br/>
     * Note: Any previous entries in this list will be deleted by this action.
     *
     * @param activationTypes - the list of supported activation interfaces
     */
    public void setActivationTypes(ActivationTypes activationTypes) {
        this.activationTypes = activationTypes;
    }

    /**
     * Returns the list off all activation interfaces (types) supported by the
     * associated metric plug-in.
     *
     * @return - the list of supported activation interfaces
     */
    public ActivationTypes getActivationTypes() {
        return this.activationTypes;
    }
    /**
     * Initializes the corresponding local field with the reference to the
     * service, that registered the associated metric plug-in.
     *
     * @param serviceRef - the service reference
     */
    public void setServiceRef(ServiceReference serviceRef) {
        this.serviceRef = serviceRef;
    }

    /**
     * Returns the service reference that points to the associated metric
     * plug-in.
     *
     * @return The service reference.
     */
    public ServiceReference getServiceRef() {
        return serviceRef;
    }

    /**
     * Sets the hash code's value of this <code>MetricInfo</code> instance.
     * <br/>
     * The value must be unique, which means that no other
     * <code>MetricInfo</code> with the same hash code should be kept by
     * the <code>PluginAdmin</code> instance that created this object.
     *
     * @param hashcode - the hash code's value of this object
     */
    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    /**
     * Returns the hash code's value of this <code>MetricInfo</code> instance.
     *
     * @return The hash code's value of this object.
     */
    public String getHashcode() {
        return hashcode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInstalled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setInstalled(boolean isInstalled) {
        installed = isInstalled;
    }

    /**
     * Creates a text representation of this <code>MetricInfo</code>
     * instance.
     *
     * @return The text representation of this object.
     */
    public String toString() {
        StringBuilder b = new StringBuilder();
        // Add the metric plug-in's name
        b.append(displayIfNotEmpty(getPluginName(), "[UNKNOWN]"));
        // Add the metric plug-in's version
        b.append(displayIfNotEmpty(getPluginVersion(), "[UNKNOWN]"));
        // Add the metric plug-in's class name
        b.append(" [");
        if (getServiceRef() != null) {
            String[] classNames =
                (String[]) serviceRef.getProperty(Constants.OBJECTCLASS);
            b.append(displayIfNotEmpty(
                    StringUtils.join(classNames, ","), "UNKNOWN"));
        }
        else {
            b.append("UNKNOWN");
        }
        b.append("]");
        return b.toString();
    }

    private String displayIfNotEmpty(String val, String orElse) {
        return (val != null) && (!val.isEmpty()) ? val : orElse;
    }

	public int compareTo(PluginInfo pi) {
		return hashcode.compareTo(pi.getHashcode());
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
