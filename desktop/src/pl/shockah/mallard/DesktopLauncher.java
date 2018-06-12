package pl.shockah.mallard;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import pl.shockah.godwit.PlatformGodwitAdapter;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Mallard");
		config.setWindowedMode(1334, 750);
		//config.useVsync(false);
		//config.hdpiMode = Lwjgl3ApplicationConfiguration.HdpiMode.Pixels
		new Lwjgl3Application(new PlatformGodwitAdapter(EditorState::new) {
			@Override
			public void create() {
				super.create();
				//PixelMaker.setup();
			}
		}, config);
	}
}