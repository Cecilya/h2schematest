package test;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("modela")
public class ModelA extends Model {

    public void setField(String field) {
        this.setString("field", field);
    }

    public String getField() {
        return this.getString("field");
    }
}
