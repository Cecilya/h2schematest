package test;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;


/**
 * JAXActivator is an arbitrary name, what is important is that javax.ws.rs.core.Application
 * is extended and the @ApplicationPath annotation is used with a "rest" path.
 */
@ApplicationPath("rest")
public class JAXActivator extends Application {


    public JAXActivator(@Context ServletContext servletContext) {

    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        return resources;
    }

}
