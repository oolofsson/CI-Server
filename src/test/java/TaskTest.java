import static org.junit.Assert.*;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class TaskTest {

	@Test
	public void test1() throws Exception {
		// Contract: Compile and test shell files should return an exit code = 0
		String configurationFile = "handleallcommits=false\naccesstoken=test\ntcp.ip=0.0.0.0\ntcp.port=1337\nmongodb.host=127.0.0.1\nmongodb.port=27017\nmongodb.database=ci1";
		Configuration configuration = new Configuration(new ByteArrayInputStream(configurationFile.getBytes(StandardCharsets.UTF_8.name())));

		Task task = new Task("git@gits-15.sys.kth.se:dd2480-group-20/test.git", "4b9e0653cb47f93aa0b0d2cdd120c85506146686", configuration);

		task.compile();
		task.test();
		task.environmentCleanUp();

		assertEquals(0, task.getCompilation().getExitCode());
		assertEquals(0, task.getTests().getExitCode());
	}

	@Test
	public void insertToDatabaseTest() throws Exception {
		// Contract: Adds test results to database and checks the inserted values
		//String configurationFile = "handleallcommits=false\naccesstoken=test\ntcp.ip=0.0.0.0\ntcp.port=1337\nmysql.host=127.0.0.1\nmysql.port=3306\nmysql.username=ci1\nmysql.database=ci2";
		//Configuration configuration = new Configuration(new ByteArrayInputStream(configurationFile.getBytes(StandardCharsets.UTF_8.name())));

		Configuration configuration = new Configuration(new FileInputStream("etc/config.properties"));
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/tasktest_insertToDatabase_JSON.txt"));
		Gson gson = new Gson();
		Push push_payload = gson.fromJson(reader, Push.class);

		Task task = new Task("git@gits-15.sys.kth.se:dd2480-group-20/test.git", "32e768e1f2d55ada2ad55514644d42f6a4898053", configuration);

		task.compile();
		task.test();

		task.addToDatabase(push_payload);

		Scanner scanner = new Scanner(new File("etc/mlab_login.txt"));
		String uri_address = "mongodb://" + scanner.nextLine() + ":" +
				scanner.nextLine() + configuration.getMongodbHost() + ":" +
				configuration.getMongodbPort() + "/" + configuration.getMongodbDatabase();

		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri_address));
		MongoDatabase database = mongoClient.getDatabase(configuration.getMongodbDatabase());
		MongoCollection collection = database.getCollection(configuration.getMongodbCollection());
		MongoDatabase db = mongoClient.getDatabase("ci");
		MongoCollection coll = database.getCollection("builds");


		task.environmentCleanUp();

		Document doc = (Document) collection.find(eq("identifier", "32e768e1f2d55ada2ad55514644d42f6a4898053")).first();
		int c = (int) ((Document)((Document)doc.get("buildlogs")).get("compilation")).get("exitcode");
		int t = (int) ((Document)((Document)doc.get("buildlogs")).get("testing")).get("exitcode");
		String sc = (String) ((Document)((Document)doc.get("buildlogs")).get("compilation")).get("stdout");
		String st = (String) ((Document)((Document)doc.get("buildlogs")).get("testing")).get("stdout");


		assertEquals(0, c); //testing
		assertEquals(0, t); //compilation
	}

}
