package ninja.mspp;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class MsppLauncher {
	public static void main(String[] args) throws Exception {
		MsppApplication.start(args);
		Application.launch(MsppApplication.class, args);
	}
}
