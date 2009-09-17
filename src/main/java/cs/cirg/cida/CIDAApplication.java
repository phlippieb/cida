/*
 * CIDAApplication.java
 */
package cs.cirg.cida;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CIDAApplication extends SingleFrameApplication {

    private String startupDirectory = "";

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new CIDAView(this));
    }

    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
        String homeDir = System.getenv("HOME");
        if (args.length > 0) {
            startupDirectory = args[0];
        } else {
            startupDirectory = homeDir;
        }
        File file = new File(startupDirectory);
        if (!file.isDirectory()) {
            System.out.println("Usage: CIDA [directory]");
            System.out.println("where diretory is:");
            System.out.println("A valid system directory that CIDA will use as the default directory to load data files from;");
            System.out.println("if no directory is specified, then the user's home directory is used.");
            System.exit(1);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of CIDAApplication
     */
    public static CIDAApplication getApplication() {
        return Application.getInstance(CIDAApplication.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(CIDAApplication.class, args);
    }

    public String getStartupDirectory() {
        return startupDirectory;
    }

    public void setStartupDirectory(String startupDirectory) {
        this.startupDirectory = startupDirectory;
    }
}
