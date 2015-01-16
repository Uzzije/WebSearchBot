package application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The main application instance.
 */
public class App
{
    private static AppFrame appFrame;
    private static AppLogic appLogic;
    private Thread appLogicThread;

    /**
     * Class constructor creates two main threads.
     * One for GUI and another for the application logic.
     */
    public App()
    {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                appFrame = new AppFrame();
                appFrame.setVisible(true);
            }
        });

        appLogicThread = new Thread(new Runnable() {
            public void run() {
            	appLogic = new AppLogic();
            }
        });

        appLogicThread.setDaemon(true);
        appLogicThread.start();
    }

    /**
     * Main static method to start the application.
     * 
     * @param args parameters passed to the program
     */
    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new App();
    }
    
    /**
     * Get GUI instance.
     * 
     * @return AppFrame (GUI) instance
     */
    public static AppFrame getFrame()
    {
    	return appFrame;
    }
    
    /**
     * Get application logic instance.
     * 
     * @return application logic instance
     */
    public static AppLogic getLogic()
    {
    	return appLogic;
    }
}
