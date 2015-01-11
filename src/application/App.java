package application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App
{
    private static AppFrame appFrame;
    private static AppLogic appLogic;
    private Thread appLogicThread;

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

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new App();
    }
    
    public static AppFrame getFrame()
    {
    	return appFrame;
    }
    
    public static AppLogic getLogic()
    {
    	return appLogic;
    }
}
