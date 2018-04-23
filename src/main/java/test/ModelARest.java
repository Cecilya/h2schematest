package test;

public class ModelARest implements ModelA {

    private String field;

    public ModelARest() {
        // left empty intentionally
    }

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
