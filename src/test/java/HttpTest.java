import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

import static org.junit.Assert.*;

public class HttpTest {
    MongoClient mongoClient;
    MongoDatabase database;
    Http http;
    @Before
    public void setUp() throws UnsupportedEncodingException, FileNotFoundException{
        String configurationFile = "handleallcommits=false\n\ntcp.ip=0.0.0.0\ntcp.port=1337\nmongodb.host=@ds111885.mlab.com\nmongodb.port=11885\nmongodb.database=ci\nmongodb.collection=test";
        Configuration configuration = new Configuration(new ByteArrayInputStream(configurationFile.getBytes(StandardCharsets.UTF_8.name())));
        http = new Http(configuration);

        Scanner scanner = new Scanner(new File("etc/mlab_login.txt"));
        String uri_address = "mongodb://" + scanner.nextLine() + ":" +
                scanner.nextLine() + configuration.getMongodbHost() + ":" +
                configuration.getMongodbPort() + "/" + configuration.getMongodbDatabase();

        mongoClient = new MongoClient(new MongoClientURI(uri_address));
        database = mongoClient.getDatabase("ci");

    }
    @Test
    public void testGetSingleDocument() throws UnsupportedEncodingException, FileNotFoundException{
        //Contract: A get request with path /commit_id should be return a string with information about the build

        setUp();

        MongoCollection collection = database.getCollection("test");

        Document build = new Document("identifier", "testGetSingle1")
                .append("date", "today")
                .append("commiter", "a name")
                .append("commitmessage", "test push")
                .append("buildlogs",
                        new Document("compilation", new Document("stdout", "stdoutResult")
                                .append("stderr", "someText").append("exitcode", 0))
                                .append("testing", new Document("stdout", "someText")
                                        .append("stderr", "someText").append("exitcode", 0)));
        collection.insertOne(build);
        String response = http.getSingleDocument(database, collection, "/testGetSingle1");

        String expected = "<strong>Date:</strong><br> today<br><strong>Commiter:</strong><br> a name<br><strong>Commit message:</strong><br> test push<br>"
                + "<strong>Compilation output:</strong><br>stdoutResult<br><strong>Compilation error:</strong><br>someText<br>"
                + "<strong>Compilation exit code:</strong> 0<br><strong>Testing output:</strong><br>someText<br><strong>Testing error:</strong><br>someText<br><strong>Testing exit code:</strong> 0";
        assertEquals(expected,response);
    }

    @Test
    public void testAllDocuments() throws UnsupportedEncodingException, FileNotFoundException{
        //Contract: A get request without specified path will return a web-page with a list of all commits in database
        setUp();
        MongoCollection collection = database.getCollection("testGetAll3");
        String response = http.getAllDocuments(database, collection);
        //Should respond with simple html string
        assertEquals("<html>\n<head>\n</head>\n<body>\n" +
                "<strong>today</strong><br>" +
                "<a href=\"firstcommit\">firstcommit</a><br>" +
                "<strong>today</strong><br>" +
                "<a href=\"secondcommit\">secondcommit</a><br>" +
                "</body>\n</html>", response);
    }


    @Test
    public void testPostCommitStatus() throws FileNotFoundException {
        //Contract: add commitHash of a commit on repo and tests the postCommitStatus function by checking that
        //http post is succesful by looking at response.
        Configuration configuration = new Configuration(new FileInputStream("etc/config.properties"));
        String repoUrl = "https://gits-15.sys.kth.se/dd2480-group-20/CI-Server/commit/d5dc9664995185f7abb2dbb313eee08b53b39819";
        String commitHash = "a4264f1d248d93ce76340b99985079c1b7dfe098";
        int start = repoUrl.indexOf("/", repoUrl.indexOf("/", repoUrl.indexOf("/") + 1) + 1);
        int end = repoUrl.indexOf("/",repoUrl.indexOf("/", start + 1) + 1);

        Status status = new Status(Status.State.success, "desc");

        String res = Http.postCommitStatus(status, configuration.getGitAPIURL(), repoUrl.substring(start, end), commitHash, configuration.getAccessToken());

        assertEquals("HTTP/1.1 201 Created", res);
    }
}
