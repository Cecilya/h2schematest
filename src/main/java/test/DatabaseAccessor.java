package test;

import org.javalite.activejdbc.Base;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccessor {

    public List<ModelA> getFromSchemaA() {
        boolean openedTransaction = false;
        try {
            Base.open();
            Base.openTransaction();
            openedTransaction = true;
            List<ModelA> result = ModelA.findAll();

            Base.commitTransaction();
            return new ArrayList<>(result);
        } catch (Exception e) {
            if (openedTransaction)
                Base.rollbackTransaction();
            throw e;
        } finally {
            Base.close();
        }
    }

    public List<ModelB> getFromSchemaB() {
        boolean openedTransaction = false;
        try {
            Base.open();
            Base.openTransaction();
            openedTransaction = true;
            List<ModelB> result = ModelB.findAll();

            Base.commitTransaction();
            return new ArrayList<>(result);
        } catch (Exception e) {
            if (openedTransaction)
                Base.rollbackTransaction();
            throw e;
        } finally {
            Base.close();
        }
    }

}
