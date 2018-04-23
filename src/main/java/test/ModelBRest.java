package test;

public class ModelBRest implements ModelB {

    private String attribute;

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
