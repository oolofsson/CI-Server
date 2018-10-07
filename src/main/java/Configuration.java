import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration
{
	private boolean valid = false;

	private boolean handleAllCommits;
	private String accessToken;
	private String tcpIp;
	private int tcpPort;
	private String mongodbHost;
	private int mongodbPort;
	private String mongodbDatabase;
	private String mongodbCollection;
	private String gitAPIURL;

	/*
		Reads configuration data from a properly formatted configuration file
		An example can be seen in the file etc/config.properties.example

		A new configuration object is created with the path to the config file as an argument
	*/

	Configuration(InputStream input)
	{
		if (input == null) { return; }

		Properties properties = new Properties();

		try
		{
			properties.load(input);

			String handleAllCommitsRaw = properties.getProperty("handleallcommits");

			if (handleAllCommitsRaw.equals("true")) { handleAllCommits = true; }
			else if (handleAllCommitsRaw.equals("false")) { handleAllCommits = false; }
			else { return; }

			accessToken = properties.getProperty("accesstoken");
			tcpIp = properties.getProperty("tcp.ip");
			tcpPort = Integer.parseInt(properties.getProperty("tcp.port"));
			mongodbHost = properties.getProperty("mongodb.host");
			mongodbPort = Integer.parseInt(properties.getProperty("mongodb.port"));
			mongodbDatabase = properties.getProperty("mongodb.database");
			mongodbCollection = properties.getProperty("mongodb.collection");
			gitAPIURL = properties.getProperty("gitapi.url");

			input.close();

			valid = accessToken != null && tcpIp != null && tcpPort > 0 && mongodbHost != null && mongodbCollection!= null && mongodbDatabase!= null && mongodbPort > 0;
		}

		catch (Exception exception)
		{

		}
	}
	/**
	 * Returns value of valid
	 * @return boolean
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * Returns the value of the boolean variable "handleAllCommits"
	 * @return true/false depending if handleAllCommits is set
	 */
	public boolean handleAllCommits()
	{
		return handleAllCommits;
	}

	/**
	 * Returns accesstoken
	 * @return String
	 */
	public String getAccessToken()
	{
		return accessToken;
	}
	/**
	 * Returns Tcp IP
	 * @return String
	 */
	public String getTcpIp()
	{
		return tcpIp;
	}
	/**
	 * Returns Tcp Port
	 * @return int
	 */
	public int getTcpPort()
	{
		return tcpPort;
	}
	/**
	 * Returns mongodb host
	 * @return String
	 */
	public String getMongodbHost()
	{
		return mongodbHost;
	}
	/**
	 * Returns mongodb port
	 * @return int
	 */
	public int getMongodbPort()
	{
		return mongodbPort;
	}
	/**
	 * Returns mongodb database
	 * @return String
	 */
	public String getMongodbDatabase()
	{
		return mongodbDatabase;
	}
	/**
	 * Returns mongodob collection
	 * @return String
	 */
	public String getMongodbCollection(){ return mongodbCollection; }
	/**
	 * Returns API url
	 * @return String
	 */
	public String getGitAPIURL() {
		return gitAPIURL;
	}
}
