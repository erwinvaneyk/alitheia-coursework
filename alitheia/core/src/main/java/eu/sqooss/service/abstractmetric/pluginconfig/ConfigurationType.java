package eu.sqooss.service.abstractmetric.pluginconfig;


import java.util.Collection;
import java.util.HashSet;

public abstract class ConfigurationType {
    private static Collection<ConfigurationType> configTypes = new HashSet<>();

    public static void addConfigurationType(ConfigurationType type) {
        configTypes.add(type);
    }

    public static ConfigurationType valueOf(String type) throws IllegalArgumentException{
        for(ConfigurationType configType : configTypes) {
            if(configType.toString().equalsIgnoreCase(type)) {
                return configType;
            }
        }
        throw new IllegalArgumentException("Unknown ConfigurationType: " + type + " found");
    }

    public abstract boolean validateString(String value);

    public abstract String toString();
}
