package test;

public class ModelARest implements ModelA {

    private String field;

    public ModelARest() {
        // left empty intentionally
        // necessary for (de-)serialisation via Jackson
    }

    /**
     * Copy constructor is necessary because database connected entities can't be instantiated
     * without a database connection, e.g. in an integration test.
     * @param toCopy entity to copy to REST object
     */
    public ModelARest(ModelA toCopy) {
        this.field = toCopy.getField();
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getField() {
        return field;
    }
}
