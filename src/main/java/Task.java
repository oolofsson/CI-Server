import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Date;

class Task
{
	private String repoUrl, commitHash;
	private Configuration configuration;
	private File workingDirectory;
	private Command compilation = null;
	private Command tests = null;
	private Status status = new Status();

	Task(String repoUrl, String commitHash, Configuration configuration) {
		this.repoUrl = repoUrl;
		this.commitHash = commitHash;
		this.configuration = configuration;

		Status status = new Status();

		environmentSetup();
		fetch();
	}

	void setCompilationStatus(Status.State state, String description) {
		// parse repoURL
		int start = repoUrl.indexOf("/", repoUrl.indexOf("/", repoUrl.indexOf("/") + 1) + 1);
		int end = repoUrl.indexOf("/",repoUrl.indexOf("/", start + 1) + 1);

		String userRepo = repoUrl.substring(start, end);

		Http.postCommitStatus(new Status(state, description), configuration.getGitAPIURL(), userRepo, commitHash, configuration.getAccessToken());
	}

	private void environmentSetup() {
		RandomString randomString = new RandomString(32, ThreadLocalRandom.current());
		workingDirectory = new File(randomString.nextString());
	}

	private void fetch() {
		Command fetch = new Command(new String[] { "git", "clone", repoUrl, workingDirectory.getName() });

		if (fetch.getExitCode() != 0) {
			System.err.println("error: couldn't clone repository '" + repoUrl + "' into " + workingDirectory);
			System.err.println(fetch.getOutput());
			System.err.println(fetch.getErrors());

			return;
		}

		Command checkout = new Command(new String[] { "git", "checkout", commitHash }, workingDirectory);

		if (fetch.getExitCode() != 0) {
			System.err.println("error: couldn't checkout commit " + commitHash+ " in " + workingDirectory);
			System.err.println(fetch.getOutput());
			System.err.println(fetch.getErrors());

			return;
		}
	}
	
	/**
	 * Cleans up the environment from left-over folders.
	 */
	public void environmentCleanUp() {
		try {
			FileUtils.deleteDirectory(workingDirectory);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Compiles the project
	 */
	public void compile() {
		compilation = new Command(new String[] { "ci/compilation.sh" }, workingDirectory);
	}

	/**
	 * Runs the tests of the project
	 */
	public void test() {
		tests = new Command(new String[] { "ci/tests.sh" }, workingDirectory);
	}

	/**
	 * Gets compilation
	 * @return the result of the compilation
	 */
	public Command getCompilation() {
		return compilation;
	}

	/**
	 * Gets tests
	 * @return Command the result of the tests
	 */
	public Command getTests() {
		return tests;
	}

	// creates connection to mongodb and inserts test build as document.
	void addToDatabase(Push push) throws FileNotFoundException{ //tmp
		//change to configuration

		Scanner scanner = new Scanner(new File("etc/mlab_login.txt"));
		String uri_address = "mongodb://" + scanner.nextLine() + ":" +
				scanner.nextLine() + configuration.getMongodbHost() + ":" +
				configuration.getMongodbPort() + "/" + configuration.getMongodbDatabase();

		MongoClient mongoClient = new MongoClient(new MongoClientURI(uri_address));
		MongoDatabase database = mongoClient.getDatabase(configuration.getMongodbDatabase());
		MongoCollection collection = database.getCollection(configuration.getMongodbCollection());

		Document build = new Document("identifier", this.commitHash)
				.append("date", new Date().toString())
				.append("commiter", push.getHead_commit().getComitter().getName())
				.append("commitmessage", push.getHead_commit().getMessage())
				.append("buildlogs",
						new Document("compilation", new Document("stdout", compilation.getOutput())
								.append("stderr", compilation.getErrors()).append("exitcode", compilation.getExitCode()))
								.append("testing", new Document("stdout", tests.getOutput())
										.append("stderr", tests.getErrors()).append("exitcode", tests.getExitCode())));
		collection.insertOne(build);

	}
}
