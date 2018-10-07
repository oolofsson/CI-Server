import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;


public class CommandTest {

	@Test
	public void test1() {
		// Contract: when a script fails it should be acknowledged by the command class
		Command cmd = new Command(new String[] {"src/test/resources/tests-fail.sh"});

		assertEquals("this should fail", cmd.getOutput());
		assertEquals(1, cmd.getExitCode());
	}

	@Test
	public void test2() {
		// Contract: when a script succeeds it should be acknowledged by the command class
		Command cmd = new Command(new String[] {"src/test/resources/tests-pass.sh"});

		assertEquals("this should pass", cmd.getOutput());
		assertEquals(0, cmd.getExitCode());
	}

	@Test
	public void test3() {
		// Contract: when a non-existent script is provided, the command class acknowledges that
		Command cmd = new Command(new String[] { "src/test/resources/tests-nonexistent.sh"});

		assertEquals(null, cmd.getOutput());
		assertEquals(-1, cmd.getExitCode());
	}

}
