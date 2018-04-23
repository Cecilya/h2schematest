package test;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.ContextResource;

import javax.servlet.ServletException;
import java.io.File;

/**
 * This takes care of an embedded tomcat.
 * Starts the tomcat and keeps it alive as long it is used by the integration tests.
 *
 * @author elv
 */
public class EmbeddedServer {
    private static final Object SERVER_CREATION = new Object();

    public static final int PORT = 12346;

    /**
     * the root path of the web application.
     */
    private static final String CONTEXT_PATH = "/h2schematest";
    /**
     * the base directory with the static files and WEB-INF/web.xml of the web application.
     */
    private static final String DOC_BASE = "src/main/webapp/";

    private static Tomcat server;

    public static void startServer() {
        try {
            if (server == null) {
                createServer();
            }

        } catch (LifecycleException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createServer() throws ServletException, LifecycleException {
        synchronized (SERVER_CREATION) {
            if (server == null) {
                server = new Tomcat();
                server.setPort(PORT);

                Context ctxt = server.addWebapp(CONTEXT_PATH, new File(DOC_BASE).getAbsolutePath());
                // delegate needs to be true, otherwise the parent classloader will not be used and two classloaders are used
                ((StandardContext)ctxt).setDelegate(true);
                ctxt.setParentClassLoader(Thread.currentThread().getContextClassLoader());
                ctxt.addParameter("org.jboss.weld.environment.servlet.archive.isolation", "false");
                server.enableNaming();


                ContextResource resource = new ContextResource();
                resource.setAuth("Container");
                resource.setName("BeanManager");
                resource.setType("javax.enterprise.inject.spi.BeanManager");
                resource.setProperty("factory", "org.jboss.weld.resources.ManagerObjectFactory");
                ctxt.getNamingResources().addResource(resource);

                // Declare an alternative location for your "WEB-INF/classes" dir
                // Servlet 3.0 annotation will work
                final WebResourceRoot resources = new StandardRoot(ctxt);
                resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("target/h2schematest/WEB-INF/classes").getAbsolutePath(), "/"));
                resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/lib/", new File("target/h2schematest/WEB-INF/lib").getAbsolutePath(), "/"));
                ctxt.setResources(resources);

                server.start();
            }
        }
    }

}
