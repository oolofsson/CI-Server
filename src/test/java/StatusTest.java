import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class StatusTest {

	@Test
	public void test() throws IOException, InterruptedException {
		// Contract: Test the status class

		Status status = new Status(Status.State.success, "hell9");

		assertEquals("success", status.getState());
	}

}
