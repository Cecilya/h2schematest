package test;

import java.util.ArrayList;
import java.util.List;

public class SchemaARepository {

    public List<ModelA> loadFromSchemaA() {
        List<ModelAEntity> result = ModelAEntity.findAll();
        return new ArrayList<>(result);
    }

}
