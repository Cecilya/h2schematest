package test;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("b.modelb")
public class ModelBEntity extends Model implements ModelB {

    public String getAttribute() {
        return this.getString("attribute");
    }

    public void setAttribute(String attribute) {
        this.setString("attribute", attribute);
    }

}
