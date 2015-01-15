package eu.sqooss.service.abstractmetric;

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
    DOUBLE;

    public boolean validate(String value) {
        if(value == null)
            return false;

        try {
            switch (this) {
                case STRING:
                    break;

                case INTEGER:
                    Integer.parseInt(value);
                    break;

                case BOOLEAN:
                    Boolean.parseBoolean(value);
                    break;

                case DOUBLE:
                    Double.parseDouble(value);
                    break;
            }

            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
