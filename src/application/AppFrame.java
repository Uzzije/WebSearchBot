package application;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

import javax.swing.JTextPane;

/**
 * Application graphical user interface logic.
 */
public class AppFrame extends JFrame
{
	/*
	 * Constants for available GUI buttons.
	 */
	public static final String BUTTON_START = "start";
	public static final String BUTTON_RESUME = "resume";
	public static final String BUTTON_PAUSE = "pause";
	public static final String BUTTON_STOP = "stop";
	public static final String BUTTON_SAVE_RESULT = "save_result";
	
	/*
	 * Text field inputs.
	 */
	private JTextField textFieldKeyword;
	private JTextField textFieldUrl;
	
	/*
	 * Buttons instances.
	 */
	private JButton btnStart;
	private JButton btnResume;
	private JButton btnPause;
	private JButton btnStop;
	private JButton btnSaveResult;
	
	/*
	 * Number spinner instances.
	 */
	private JSpinner spinnerMaxExecutionTime;
	private JSpinner spinnerAmountOfThreads;
	
	/*
	 * Output area instances.
	 */
	private JTextPane logOutput;
	
	/*
	 * Status label instance. 
	 */
	private JLabel lblStatus;
	
	/**
	 * Constructor to build the GUI.
	 */
	public AppFrame()
	{
		setTitle("Web Search Bot");
		setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(null);
		
		// Labels
		JLabel lblKeyword = new JLabel("Keyword");
		lblKeyword.setBounds(109, 11, 42, 14);
		mainPanel.add(lblKeyword);
		
		JLabel lblStartPointUrl = new JLabel("Start point URL");
		lblStartPointUrl.setBounds(81, 36, 73, 14);
		mainPanel.add(lblStartPointUrl);
		
		JLabel lblMaxExcecutionTime = new JLabel("Max execution time in seconds");
		lblMaxExcecutionTime.setBounds(10, 61, 146, 14);
		mainPanel.add(lblMaxExcecutionTime);
		
		JLabel lblAmountOfThreads = new JLabel("Threads amount to use");
		lblAmountOfThreads.setBounds(44, 86, 112, 14);
		mainPanel.add(lblAmountOfThreads);
		
		// Inputs
		textFieldKeyword = new JTextField();
		textFieldKeyword.setBounds(161, 8, 323, 20);
		mainPanel.add(textFieldKeyword);
		textFieldKeyword.setColumns(10);
		
		textFieldUrl = new JTextField("http://");
		textFieldUrl.setBounds(161, 33, 323, 20);
		mainPanel.add(textFieldUrl);
		textFieldUrl.setColumns(10);
		
		spinnerMaxExecutionTime = new JSpinner();
		spinnerMaxExecutionTime.setBounds(161, 58, 50, 20);
		spinnerMaxExecutionTime.setValue(20);
		mainPanel.add(spinnerMaxExecutionTime);
		
		spinnerAmountOfThreads = new JSpinner();
		spinnerAmountOfThreads.setBounds(161, 83, 50, 20);
		spinnerAmountOfThreads.setValue(2);
		mainPanel.add(spinnerAmountOfThreads);

		// Action buttons
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getKeyword().isEmpty()) {
					if (Helpers.isUrlValid(getUrl())) {
						if (1 <= getThreadsNumber()) {
							btnStart.setEnabled(false);
							btnResume.setEnabled(false);
							btnPause.setEnabled(true);
							btnStop.setEnabled(true);
							btnSaveResult.setEnabled(false);
							
							log("Info: Search was started.");
							lblStatus.setText("Searching...");
							
							new Thread(new Runnable() {
					            public void run() {
					            	App.getLogic().start();
					            }
					        }).start();
						} else {
							log("Validation: It must have at least one thread to execute the bot!");
						}
					} else {
						log("Validation: URL must be valid http protocol link!");
					}
				} else {
					log("Validation: Keyword cannot be empty!");
				}
			}
		});
		btnStart.setBounds(10, 111, 89, 23);
		mainPanel.add(btnStart);
		
		btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(false);
				btnResume.setEnabled(false);
				btnPause.setEnabled(true);
				btnStop.setEnabled(true);
				btnSaveResult.setEnabled(false);
				
				log("Info: Bot was resumed.");
				setStatus("Searching...");
				
				new Thread(new Runnable() {
		            public void run() {
		            	App.getLogic().resume();
		            }
		        }).start();
			}
		});
		btnResume.setBounds(109, 111, 85, 23);
		btnResume.setEnabled(false);
		mainPanel.add(btnResume);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(false);
				btnResume.setEnabled(true);
				btnPause.setEnabled(false);
				btnStop.setEnabled(true);
				btnSaveResult.setEnabled(true);
				
				log("Info: Search was paused.");
				
				new Thread(new Runnable() {
		            public void run() {
		            	App.getLogic().pause();
		            }
		        }).start();
			}
		});
		btnPause.setBounds(204, 111, 89, 23);
		btnPause.setEnabled(false);
		mainPanel.add(btnPause);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(true);
				btnResume.setEnabled(false);
				btnPause.setEnabled(false);
				btnStop.setEnabled(false);
				btnSaveResult.setEnabled(true);
				
				log("Info: Search was stopped.");
				setStatus("Stopped");
				
				new Thread(new Runnable() {
		            public void run() {
		            	App.getLogic().stop();
		            }
		        }).start();
			}
		});
		btnStop.setBounds(303, 111, 82, 23);
		btnStop.setEnabled(false);
		mainPanel.add(btnStop);
		
		btnSaveResult = new JButton("Save result");
		btnSaveResult.addActionListener(new ActionListener() {
			private File file;

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				
				if (fileChooser.showSaveDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					
					new Thread(new Runnable() {
			            public void run() {
			            	App.getLogic().save(file);
			            }
			        }).start();
				}
			}
		});
		btnSaveResult.setBounds(395, 111, 89, 23);
		btnSaveResult.setEnabled(false);
		mainPanel.add(btnSaveResult);
		
		// Log output
		logOutput = new JTextPane();
		logOutput.setEditable(false);
		logOutput.setText("Info: Application loaded successfully and ready to start.");
		JScrollPane consoleScroll = new JScrollPane(logOutput);
		consoleScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
			public void adjustmentValueChanged(AdjustmentEvent e) {  
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
			}}); 
		consoleScroll.setBounds(10, 145, 474, 300);
		mainPanel.add(consoleScroll);
		
		// Status
		lblStatus = new JLabel("Status: Ready");
		lblStatus.setBounds(10, 450, 474, 14);
		mainPanel.add(lblStatus);
	}
	
	/**
	 * Main log action to put the the in the output area.
	 * 
	 * @param message to output
	 */
	public void log(String message)
	{
		logOutput.setText(logOutput.getText() + "\n" + message);
	}
	
	/**
	 * Get the primary URL address provided by user.
	 * 
	 * @return URL
	 */
	public String getUrl()
	{
		return textFieldUrl.getText();
	}
	
	/**
	 * Get the keyword provided by user.
	 * 
	 * @return keyword
	 */
	public String getKeyword()
	{
		return textFieldKeyword.getText();
	}
	
	/**
	 * Get maximum execution time.
	 * 
	 * @return max execution time
	 */
	public int getMaxExecutionTime()
	{
		return (int) spinnerMaxExecutionTime.getValue();
	}
	
	/**
	 * Get an amount of threads used for accessing links.
	 * 
	 * @return threads number
	 */
	public int getThreadsNumber()
	{
		return (int) spinnerAmountOfThreads.getValue();
	}
	
	/**
	 * Set button state.
	 * 
	 * @param button constant
	 * @param enabled sets is it button state
	 */
	public void setButtonEnabled(String button, boolean enabled)
	{
		switch (button) {
			case BUTTON_START:
				btnStart.setEnabled(enabled);
				break;
			case BUTTON_PAUSE:
				btnPause.setEnabled(enabled);
				break;
			case BUTTON_RESUME:
				btnResume.setEnabled(enabled);
				break;
			case BUTTON_STOP:
				btnStop.setEnabled(enabled);
				break;
			case BUTTON_SAVE_RESULT:
				btnSaveResult.setEnabled(enabled);
				break;
		}
	}
	
	/**
	 * Set status label.
	 * 
	 * @param status message
	 */
	public void setStatus(String status)
	{
		lblStatus.setText(status);
	}
}
