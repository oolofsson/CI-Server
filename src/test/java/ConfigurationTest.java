import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class ConfigurationTest {

	@Test
	public void test1() throws Exception {
		// Contract: load a valid configuration file
		String configurationFile = "handleallcommits=false\naccesstoken=test\ntcp.ip=0.0.0.0\ntcp.port=1337\nmongodb.host=127.0.0.1\nmongodb.port=3306\nmongodb.database=ci1\nmongodb.collection=ci2";
		Configuration configuration = new Configuration(new ByteArrayInputStream(configurationFile.getBytes(StandardCharsets.UTF_8.name())));

		assertTrue(configuration.isValid());
		assertFalse(configuration.handleAllCommits());
		assertEquals("test", configuration.getAccessToken());
		assertEquals("0.0.0.0", configuration.getTcpIp());
		assertEquals(1337, configuration.getTcpPort());
		assertEquals("ci1", configuration.getMongodbDatabase());
		assertEquals("ci2", configuration.getMongodbCollection());
	}

	@Test
	public void test2() throws Exception {
		// Contract: load an invalid configuration file
		String configurationFile = "handleallcommits=false\ntcp.iername=ci1\nmongodb.database=ci2";
		Configuration configuration = new Configuration(new ByteArrayInputStream(configurationFile.getBytes(StandardCharsets.UTF_8.name())));

		assertFalse(configuration.isValid());
	}

}
