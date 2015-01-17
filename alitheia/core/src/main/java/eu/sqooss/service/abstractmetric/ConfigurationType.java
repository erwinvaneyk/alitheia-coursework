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

    /**
     * Validates a string value to the given ConfigurationType.
     *
     * @param value the value to be evaluated
     * @return If the value is of a valid format for the type true will be returned.
     */
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
                    if(!value.equals("true") && !value.equals("false")) {
                        throw new Exception("Invalid value");
                    }
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
