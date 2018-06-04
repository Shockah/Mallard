package pl.shockah.mallard;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import pl.shockah.godwit.PlatformGodwitAdapter;
import pl.shockah.godwit.State;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Mallard");
		config.setWindowedMode(750 / 4 * 3, 1334 / 4 * 3);
		//config.useVsync(false);
		//config.hdpiMode = Lwjgl3ApplicationConfiguration.HdpiMode.Pixels
		new Lwjgl3Application(new PlatformGodwitAdapter(new State()) {
			@Override
			public void create() {
				super.create();
				//PixelMaker.setup();
			}
		}, config);
	}
}