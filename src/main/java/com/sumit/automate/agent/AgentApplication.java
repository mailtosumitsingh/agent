package com.sumit.automate.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
@SpringBootApplication
public class AgentApplication {
	@Autowired
	SoundRecorder recorder ;

	@Autowired
	AudioInferenceService audioInferenceService;

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(AgentApplication.class, args);
	}

	@Bean
	public CommandLineRunner post() {
		return (args) -> {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			} catch (UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			/* Turn off metal's use of bold fonts */
			UIManager.put("swing.boldMetal", Boolean.FALSE);
			//Schedule a job for the event-dispatching thread:
			//adding TrayIcon.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						createAndShowGUI();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		};
	}
	private  void createAndShowGUI() throws Exception{
		//Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon =
				new TrayIcon(createImage("images/img.png", "tray icon"));
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem("About");
		CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
		CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
		Menu displayMenu = new Menu("Display");
		MenuItem errorItem = new MenuItem("Error");
		MenuItem warningItem = new MenuItem("Warning");
		MenuItem infoItem = new MenuItem("Info");
		MenuItem noneItem = new MenuItem("None");
		MenuItem record = new MenuItem("Record");
		MenuItem stopItem = new MenuItem("Stop");

		MenuItem runItem = new MenuItem("run");
		MenuItem exitItem = new MenuItem("Exit");

		//Add components to popup menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(cb1);
		popup.add(cb2);
		popup.addSeparator();
		popup.add(displayMenu);
		displayMenu.add(errorItem);
		displayMenu.add(warningItem);
		displayMenu.add(infoItem);
		displayMenu.add(noneItem);
		popup.add(exitItem);
		popup.add(record);
		popup.add(stopItem);
		popup.add(runItem);
		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"This dialog box is run from System Tray");
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"This dialog box is run from the About menu item");
			}
		});

		cb1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int cb1Id = e.getStateChange();
				if (cb1Id == ItemEvent.SELECTED){
					trayIcon.setImageAutoSize(true);
				} else {
					trayIcon.setImageAutoSize(false);
				}
			}
		});

		cb2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int cb2Id = e.getStateChange();
				if (cb2Id == ItemEvent.SELECTED){
					trayIcon.setToolTip("Sun TrayIcon");
				} else {
					trayIcon.setToolTip(null);
				}
			}
		});

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuItem item = (MenuItem)e.getSource();
				//TrayIcon.MessageType type = null;
				System.out.println(item.getLabel());
				if ("Error".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.ERROR;
					trayIcon.displayMessage("Sun TrayIcon Demo",
							"This is an error message", TrayIcon.MessageType.ERROR);

				} else if ("Warning".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.WARNING;
					trayIcon.displayMessage("Sun TrayIcon Demo",
							"This is a warning message", TrayIcon.MessageType.WARNING);

				} else if ("Info".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.INFO;
					trayIcon.displayMessage("Sun TrayIcon Demo",
							"This is an info message", TrayIcon.MessageType.INFO);

				} else if ("None".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.NONE;
					trayIcon.displayMessage("Sun TrayIcon Demo",
							"This is an ordinary message", TrayIcon.MessageType.NONE);
				}
			}
		};

		errorItem.addActionListener(listener);
		warningItem.addActionListener(listener);
		infoItem.addActionListener(listener);
		noneItem.addActionListener(listener);

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});
		record.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			recorder.start();
			}
		});
		stopItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = recorder.stop();
			}
		});
		runItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = recorder.stop();
                try {
					long time = System.currentTimeMillis();
                    String text = audioInferenceService.sendAudioFile(file);
					System.out.println("TExt: "+text);
					long time2 = System.currentTimeMillis();
					System.out.println(time2-time);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
		});
	}

	//Obtain the image URL
	protected  Image createImage(String path, String description) throws Exception{

		URL imageURL =  new ClassPathResource(path).getURL();
		if (path == null) {

			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
}
