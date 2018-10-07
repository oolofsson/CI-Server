import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;


public class Command {

	private int exitCode = 0;
	private String output = null;
	private String errors = null;

	public Command(String[] command) {
		run(command, null);
	}
	
	/**
	 * @param command the command or file to be executed
	 * If you want to run a script, let command = PathToScript/scriptName.sh
	 * Class for running commands on the host OS from preconfigured script files and saving the output
	 *
	 */
	public Command(String[] command, File workingDirectory) {
		run(command, workingDirectory);
	}

	/**
	 * This method first sets the working directory to the given one, and then executes the command.
	 * Used if the command requires one to be situated in a certain working directory.
	 * @param command command to execute
	 * @param workingDirectory workingDirectory at the time the command is executed
	 */
	private void run(String[] command, File workingDirectory) {
		LinkedList<String> stdout = new LinkedList<>();
		LinkedList<String> stderr = new LinkedList<>();

		exitCode = 0;
		output = null;
		errors = null;

		Process p = null;
		String s = null;

		try {
			p = Runtime.getRuntime().exec(command, null, workingDirectory);

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = br.readLine()) != null)
				stdout.add(s);

			BufferedReader brerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = brerr.readLine()) != null)
				stderr.add(s);

			output = String.join("\n", stdout);
			errors = String.join("\n", stderr);

			exitCode = p.waitFor();

			p.destroy();
		}

		catch (Exception exception) {
			exitCode = -1;
			StringBuilder sb = new StringBuilder();
			sb.append(exception.getMessage() + "\n");

			for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
				sb.append(stackTraceElement.toString());
			}
			errors = sb.toString();
		}

		finally {
			if (p != null) {
				p.destroy();
			}
		}
	}
	
	/**
	 * @return the output from the last command
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * @return the error output from the last command
	 */
	public String getErrors() {
		return errors;
	}
	
	/**
	 * @return the exit code from the last command
	 */
	public int getExitCode() {
		return exitCode;
	}
}
