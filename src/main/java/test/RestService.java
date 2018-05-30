package test;

import org.javalite.activejdbc.Base;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestScoped
@Path("/schemas")
public class RestService {


    @GET
    @Path("/a")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFromSchemaA() {
        return proceedWithNewTransaction(a -> {
            List<ModelAEntity> result = ModelAEntity.findAll();
            List<ModelARest> restObjects = new ArrayList<>();

            if (result != null && !result.isEmpty()) {
                restObjects = result.stream().map(ModelARest::new).collect(Collectors.toList());
            }

            return Response.ok(restObjects).build();
        });
    }

    @GET
    @Path("/b")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFromSchemaB() {
        return proceedWithNewTransaction(a -> {
            List<ModelBEntity> result = ModelBEntity.findAll();
            List<ModelBRest> restObjects = new ArrayList<>();

            if (result != null && !result.isEmpty()) {
                restObjects = result.stream().map(ModelBRest::new).collect(Collectors.toList());
            }

            return Response.ok(restObjects).build();
        });
    }

    /**
     * Handle database transactions for REST calls. Transaction will be rolled back if any errors occur.
     *
     * @param function
     * @return
     */
    private Response proceedWithNewTransaction(Function<Object, Response> function) {
        boolean openedTransaction = false;
        try {
            Base.open();
            Base.openTransaction();
            openedTransaction = true;
            Response response = function.apply(null);
            Base.commitTransaction();
            return response;
        } catch (Exception e) {
            if (openedTransaction)
                Base.rollbackTransaction();
            throw e;
        } finally {
            Base.close();
        }
    }
}
