/**
 * Copyright (C) 2009
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cs.cirg.cida;

import java.io.File;
import javax.swing.UIManager;
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

        boolean nimbusAvailable = false;
        UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        for (int i = 0; !nimbusAvailable && i < lookAndFeelInfos.length; i++) {
            UIManager.LookAndFeelInfo lookAndFeelInfo = lookAndFeelInfos[i];
            if (lookAndFeelInfo.getName().equalsIgnoreCase("nimbus")) {
                try {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    nimbusAvailable = true;
                } catch (Exception ex) {
                }
            }
        }

        if (args.length > 0) {
            startupDirectory = args[0];
            File file = new File(startupDirectory);
            if (!(file.exists() && file.isDirectory())) {
                System.out.println("Usage: CIDA [directory]");
                System.out.println("where diretory is:");
                System.out.println("A valid system directory that CIDA will use as the default directory to load data files from;");
                System.out.println("if no directory is specified, then the user's home directory is used.");
                System.exit(1);
            }
        } else {
            String homeDir = System.getenv("HOME");
            startupDirectory = homeDir;
            File file = new File(startupDirectory);
            if (!(file.exists() && file.isDirectory())) {
                startupDirectory = ".";
            }
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
