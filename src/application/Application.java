package application;

import application.gui.Window;
import application.net.Client;
import application.piano.Piano;

public class Application {
	public Application() {
		new Window(new Piano(new Client("localhost")));
	}
	
	public static void main(String[] args) {
		new Application();
	}
}
