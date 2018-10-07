import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


/** 
 ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
	private static Configuration configuration = null;

    /**
     * Handles HTTP requests GET(database search) and POST (webhook)
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException {
        Http webHandler = new Http(configuration);
        switch (request.getMethod()){
            case "POST":
                String event_type = request.getHeader("X-GitHub-Event");
                webHandler.handleWebhook(event_type, request.getReader());
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                response.getWriter().println("CI job done");
                break;

            case "GET":
                try {
                    String database_response = webHandler.handleDatabaseSearch(request.getPathInfo());
                    response.setContentType("text/html;charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    baseRequest.setHandled(true);
                    out.print(database_response);
                    out.flush();
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
    /**
     * Used to start the CI server in command line
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        // parse configuration
        configuration = new Configuration(new FileInputStream("etc/config.properties"));

        // start server listener
        Server server = new Server(configuration.getTcpPort()); //Runs 8080 default to use with ngrok locally
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();

    }
}
