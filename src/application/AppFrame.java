package application;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class AppFrame extends JFrame {

	public AppFrame() {
		
		setTitle("Simple example");
		setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		mainPanel.setLayout(null);
		
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
		
		JTextField textFieldKeyword = new JTextField();
		textFieldKeyword.setBounds(161, 8, 323, 20);
		mainPanel.add(textFieldKeyword);
		textFieldKeyword.setColumns(10);
		
		JTextField textFieldUrl = new JTextField();
		textFieldUrl.setBounds(161, 33, 323, 20);
		mainPanel.add(textFieldUrl);
		textFieldUrl.setColumns(10);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnStart.setBounds(10, 111, 89, 23);
		mainPanel.add(btnStart);
		
		JButton btnResume = new JButton("Resume");
		btnResume.setBounds(109, 111, 85, 23);
		mainPanel.add(btnResume);
		
		JButton btnPause = new JButton("Pause");
		btnPause.setBounds(204, 111, 89, 23);
		mainPanel.add(btnPause);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(303, 111, 82, 23);
		mainPanel.add(btnStop);
		
		JButton btnSaveResult = new JButton("Save result");
		btnSaveResult.setBounds(395, 111, 89, 23);
		mainPanel.add(btnSaveResult);
		
		JTextPane logOutput = new JTextPane();
		logOutput.setBounds(10, 145, 474, 315);
		mainPanel.add(logOutput);
		
		JSpinner spinnerMaxExcecutionTime = new JSpinner();
		spinnerMaxExcecutionTime.setBounds(161, 58, 50, 20);
		mainPanel.add(spinnerMaxExcecutionTime);
		
		JSpinner spinnerAmountOfThreads = new JSpinner();
		spinnerAmountOfThreads.setBounds(161, 83, 50, 20);
		mainPanel.add(spinnerAmountOfThreads);
	}
}
