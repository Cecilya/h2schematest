package test;

public class ModelBRest implements ModelB {

    private String attribute;

    public ModelBRest() {
        // left empty intentionally
        // necessary for (de-)serialisation via Jackson
    }

    /**
     * Copy constructor is necessary because database connected entities can't be instantiated
     * without a database connection, e.g. in an integration test.
     * @param toCopy entity to copy to REST object
     */
    public ModelBRest(ModelB toCopy) {
        this.attribute = toCopy.getAttribute();
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
