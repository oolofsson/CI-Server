import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCursor;

import javax.servlet.http.HttpServletResponse;
//import javax.swing.text.Document;
import java.io.*;
import java.util.Scanner;

class Http
{
	private static Gson gson;
	private static Configuration configuration;
	Http(Configuration configuration){

		gson = new Gson();
		this.configuration = configuration;
	}

	static void handleWebhook(String event_type,  BufferedReader request)
	{
		//compile  where change has been made

		// test the code where change has been made
		switch (event_type){

			case "pull_request":
				System.out.println("Recieved pull_request");
				break;

			case "push":
				Push push_obj = gson.fromJson(request, Push.class);
				System.out.println(event_type + " is parsed");
				handle_push(push_obj);


			default: //default
				break;
		}
	}


	/**
	 * Creates new task, compiles and tests push
	 * @param push_obj
	 */
	private static void handle_push(Push push_obj){

		if (push_obj.getHead_commit() == null) {
			return;
		}

		try {
			Task task = new Task(push_obj.getRepository().getSsh_url(), push_obj.getHead_commit().getId(), configuration);
			task.compile();
			task.test();
			System.out.println("Compilation exit-code: " + task.getCompilation().getExitCode());
			System.out.println("Testing exit-code: " + task.getTests().getExitCode());
			Status.State s = Status.State.failure;
			if(task.getCompilation().getExitCode() == 0 && task.getTests().getExitCode() == 0){
                s = Status.State.success;
            }
			task.setCompilationStatus(s, "status");
			task.addToDatabase(push_obj);
			task.environmentCleanUp();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}
    //Returns html string response depending on get request
	static String handleDatabaseSearch(String target) throws Exception{ //Tmp until we fix global server connection
		Scanner scanner = new Scanner(new File("etc/mlab_login.txt"));
		String uri_address = "mongodb://" + scanner.nextLine() + ":" +
				scanner.nextLine() + configuration.getMongodbHost() + ":" +
				configuration.getMongodbPort() + "/" + configuration.getMongodbDatabase();

		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri_address));
		MongoDatabase database = mongoClient.getDatabase(configuration.getMongodbDatabase());
		MongoCollection collection = database.getCollection(configuration.getMongodbCollection());

		//To be returned
		JsonObject builds = new JsonObject();


		//Returns all builds as a json
		if(target.equals("/")) {
			return getAllDocuments(database, collection);

			//Returns a single object
		}else if(target.matches("\\/[a-z0-9]+")){
            return getSingleDocument(database, collection, target);

		}else{
			return null;
		}
	}

	//Returns a single document with identifier in target
	static String getSingleDocument(MongoDatabase database, MongoCollection collection, String target){
        StringBuilder html_builder = new StringBuilder();
        Document doc = (Document) collection.find(Filters.eq("identifier", target.substring(1))).first();
        String date = (String) doc.get("date");
		String commmitMessage = (String) doc.get("commitmessage");
		String commmiter = (String) doc.get("commiter");
        int c = (int) ((Document)((Document)doc.get("buildlogs")).get("compilation")).get("exitcode");
        int t = (int) ((Document)((Document)doc.get("buildlogs")).get("testing")).get("exitcode");
        String scout = (String) ((Document)((Document)doc.get("buildlogs")).get("compilation")).get("stdout");
        String stout = (String) ((Document)((Document)doc.get("buildlogs")).get("testing")).get("stdout");
		String scerr = (String) ((Document)((Document)doc.get("buildlogs")).get("compilation")).get("stderr");
		String sterr = (String) ((Document)((Document)doc.get("buildlogs")).get("testing")).get("stderr");

        //Compilation
        html_builder.append("<strong>Date:</strong>");
        html_builder.append("<br> " + date);

		html_builder.append("<br><strong>Commiter:</strong>");
		html_builder.append("<br> " + commmiter);

		html_builder.append("<br><strong>Commit message:</strong>");
		html_builder.append("<br> " + commmitMessage);

        //Compilation
        html_builder.append("<br><strong>Compilation output:</strong><br>");
        html_builder.append(scout);
		html_builder.append("<br><strong>Compilation error:</strong><br>");
		html_builder.append(scerr);
        html_builder.append("<br><strong>Compilation exit code:</strong> " + c);

        //Testing
        html_builder.append("<br><strong>Testing output:</strong><br>");
        html_builder.append(stout);
		html_builder.append("<br><strong>Testing error:</strong><br>");
		html_builder.append(sterr);
        html_builder.append("<br><strong>Testing exit code:</strong> " + t);
        return html_builder.toString();
    }

	//Returns all build documents from Mongodb in a String
	static String getAllDocuments(MongoDatabase database, MongoCollection collection){
        MongoCursor<Document> cursor = collection.find().iterator();
        Document tmp_doc;
        StringBuilder html_string = new StringBuilder("<html>\n<head>\n</head>\n<body>\n");
        while(cursor.hasNext()) {
            tmp_doc = (Document) cursor.next();
            html_string.append("<strong>" + tmp_doc.get("date") + "</strong><br>");
            html_string.append("<a href=\"" + tmp_doc.get("identifier") + "\">" + tmp_doc.get("identifier") + "</a>");
            html_string.append("<br>");
        }
        html_string.append("</body>\n</html>");
        return html_string.toString();
    }
	static String postCommitStatus(Status status, String gitAPIURL, String userRepo, String commitHash, String accessToken){
		HttpClient httpClient = HttpClientBuilder.create().build();
        String res = "";
        try {
			HttpPost request = new HttpPost(gitAPIURL + userRepo + "/statuses/" + commitHash + "?access_token=" + accessToken);
			StringEntity params = new StringEntity(gson.toJson(status));
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
            return response.getStatusLine().toString();
		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			//httpClient.getConnectionManager().shutdown();
		}
		return res;
	}

}
