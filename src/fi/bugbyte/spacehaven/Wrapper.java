package fi.bugbyte.spacehaven;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import fi.bugbyte.spacehaven.world.Ship;

import java.io.IOException;

public class Wrapper {
    public static Ship ship;
    public static void main(String[] args) throws InterruptedException, IOException {
        // Using this will allow one to run the editing window as another libgdx window.
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        EditorWindow editorWindow = new EditorWindow();
        new Thread(() -> new Lwjgl3Application(editorWindow, config)).start();


        Thread.sleep(3000);
        JavaProcess.exec(MainClass.class);

    }
}
