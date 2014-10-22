import java.awt.*;
import java.awt.event.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClientGUI extends JFrame {
	public JPanel hauptpane;
	public JPanel obenPane;
	JToggleButton pauseButton;

	ClientApp controller;
	private JTextField text_verbinden;
	private JTextField text_senden;
	private JTextField text_an;
	private JTextField text_von;
	private JTextArea text_empfangen;

	public ClientGUI(ClientApp controller) {
		this.controller = controller;

		/* Frame initialisieren */
		setTitle("VS1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(200, 200);
		// setMinimumSize(new Dimension(800, 200));
		erzeugeMenus();
		erzeugeAnzeige();

		pack();
		/* Hauptfenster sichtbar machen */
		setVisible(true);

	}

	private void erzeugeMenus() {
		/* Menüleiste erzeugen */
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		/* Menü Datei erzeugen */
		JMenu menuDatei = new JMenu("Menü");
		menuBar.add(menuDatei);
		/* Neues Rennen */
		JMenuItem oeffnenEntry = new JMenuItem("Senden!");
		oeffnenEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controller.senden(text_senden.getText(), text_an.getText());
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		menuDatei.add(oeffnenEntry);
		/* Ende */
		JMenuItem endeEntry = new JMenuItem("Beenden");
		endeEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		menuDatei.add(endeEntry);
	}

	public void erzeugeAnzeige() {
		/*
		 * Erzeuge eine neue Pane für das Hauptfenster mit existierender
		 * Symbolleiste und Tabelle (inkl. Scrollpane)
		 */

		/* Pane des Hauptfensters neu erzeugen */
		hauptpane = new JPanel();
		setContentPane(hauptpane);
		hauptpane.setBorder(new EmptyBorder(5, 5, 5, 5));
		hauptpane.setLayout(new BorderLayout());

		obenPane = new JPanel();
		obenPane.setLayout(new BorderLayout());
		hauptpane.add(obenPane, BorderLayout.NORTH);

		/*
		 * JPanel obenButtonPane = new JPanel(); obenButtonPane.setLayout(new
		 * BoxLayout(obenButtonPane, BoxLayout.Y_AXIS));
		 * obenPane.add(obenButtonPane, BorderLayout.WEST);
		 */

		JButton verbindenButton = new JButton();
		verbindenButton.setText("Verbinden");
		verbindenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!text_verbinden.getText().isEmpty()) {
					try {
						controller.verbinden(text_verbinden.getText());
					} catch (RemoteException | NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					System.out.println("Keine Serveradresse angegeben");
				}

			}
		});
		obenPane.add(verbindenButton, BorderLayout.WEST);

		text_verbinden = new JTextField();
		text_verbinden.setText("ServerAdresse");
		obenPane.add(text_verbinden, BorderLayout.EAST);
		text_verbinden.setColumns(10);

		JPanel mittelPane = new JPanel();
		hauptpane.add(mittelPane, BorderLayout.CENTER);
		mittelPane.setLayout(new BorderLayout(0, 0));

		JButton sendenButton = new JButton();
		mittelPane.add(sendenButton, BorderLayout.WEST);
		sendenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controller.senden(text_senden.getText(), text_an.getText());
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		sendenButton.setText("Senden");

		text_senden = new JTextField();
		text_senden.setText("Nachricht");
		mittelPane.add(text_senden, BorderLayout.EAST);
		text_senden.setColumns(10);

		text_an = new JTextField();
		text_an.setText("Empfaenger");
		mittelPane.add(text_an, BorderLayout.CENTER);
		text_an.setColumns(10);

		JPanel untenPane = new JPanel();
		hauptpane.add(untenPane, BorderLayout.SOUTH);
		untenPane.setLayout(new BorderLayout(0, 0));

		JButton empfangenButton = new JButton("Empfangen");
		untenPane.add(empfangenButton, BorderLayout.WEST);
		empfangenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.empfangen(text_von.getText());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		text_empfangen = new JTextArea();
		text_empfangen.setToolTipText("Hier wird die Nachricht angezeigt");
		text_empfangen.setEditable(false);
		untenPane.add(text_empfangen, BorderLayout.SOUTH);

		text_von = new JTextField();
		text_von.setText("Nachrichten f\u00FCr?");
		untenPane.add(text_von, BorderLayout.CENTER);
		text_von.setColumns(10);


	}

	public void textAnzeigen(String text) {
		text_empfangen.append(text);
	}

}
