package test;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("modela")
public class ModelAEntity extends Model implements ModelA {

    @Override
    public void setField(String field) {
        this.setString("field", field);
    }

    @Override
    public String getField() {
        return this.getString("field");
    }
}
