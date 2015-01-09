package application;

import java.awt.*;

import javax.swing.UIManager;

public class App
{
    private AppFrame appFrame;
    private Thread appLogicThread;

    public App() {
        EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                appFrame = new AppFrame();
                appFrame.setVisible(true);
            }
        });

        appLogicThread = new Thread(new Runnable() {
            public void run() {
                new AppLogic();
            }
        });

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
}
