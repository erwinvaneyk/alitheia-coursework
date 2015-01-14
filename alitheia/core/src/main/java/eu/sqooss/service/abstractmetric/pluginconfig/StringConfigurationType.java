package eu.sqooss.service.abstractmetric.pluginconfig;

public class StringConfigurationType extends ConfigurationType {

    static {
        // could be moved to a central init place.. this might cause problems with unit-testing
        ConfigurationType.addConfigurationType(new StringConfigurationType());
    }

    public String toString() {
        return "String";
    }

    public boolean validateString(String value) {
        return value != null;
    }
}
