package pl.shockah.mallard;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import pl.shockah.godwit.PlatformGodwitAdapter;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Mallard";
		config.fullscreen = false;
		config.width = 1334;
		config.height = 750;
		//config.useVsync(false);
		//config.hdpiMode = Lwjgl3ApplicationConfiguration.HdpiMode.Pixels
		new LwjglApplication(new PlatformGodwitAdapter(EditorState::new) {
			@Override
			public void create() {
				super.create();
				//PixelMaker.setup();
			}
		}, config);
	}
}