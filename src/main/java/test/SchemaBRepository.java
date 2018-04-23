package test;

import java.util.ArrayList;
import java.util.List;

public class SchemaBRepository {

    public List<ModelB> loadFromSchemaB() {
        return new ArrayList<>(ModelBEntity.findAll());
    }

}
