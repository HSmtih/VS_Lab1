package client;

import java.awt.*;
import java.awt.event.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClientGUI extends JFrame implements Observer {
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

		if (controller == null) {
			throw new IllegalArgumentException("Invalid ClientApp object");
		}

		this.controller = controller;

		/* Frame initialisieren */
		setTitle("VS1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(200, 200);
		// setMinimumSize(new Dimension(800, 200));
		erzeugeMenus();
		erzeugeAnzeige();

		pack();
		this.controller.addObserver(this);

		/* Hauptfenster sichtbar machen */
		setVisible(true);
	}

	private void erzeugeMenus() {
		/* Men�leiste erzeugen */
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		/* Men� Datei erzeugen */
		JMenu menuDatei = new JMenu("Men�");
		menuBar.add(menuDatei);

		/* Neues Rennen */
		JMenuItem oeffnenEntry = new JMenuItem("Senden!");
		oeffnenEntry.addActionListener(e -> {
			try {
				controller.senden(text_senden.getText(), text_an.getText());
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		});
		menuDatei.add(oeffnenEntry);

		/* Ende */
		JMenuItem endeEntry = new JMenuItem("Beenden");
		endeEntry.addActionListener(e -> {
			setVisible(false);
			dispose();
		});
		menuDatei.add(endeEntry);
	}

	public void erzeugeAnzeige() {
		/*
		 * Erzeuge eine neue Pane f�r das Hauptfenster mit existierender
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

		JPanel mittelPane = new JPanel();
		hauptpane.add(mittelPane, BorderLayout.CENTER);
		mittelPane.setLayout(new BorderLayout(0, 0));

		JPanel untenPane = new JPanel();
		hauptpane.add(untenPane, BorderLayout.SOUTH);
		untenPane.setLayout(new BorderLayout(0, 0));

		/*
		 * JPanel obenButtonPane = new JPanel(); obenButtonPane.setLayout(new
		 * BoxLayout(obenButtonPane, BoxLayout.Y_AXIS));
		 * obenPane.add(obenButtonPane, BorderLayout.WEST);
		 */

		/* Buttons */
		JButton verbindenButton = new JButton("Verbinden");
		verbindenButton.addActionListener(e -> {
			bindServer();
		});
		obenPane.add(verbindenButton, BorderLayout.WEST);

		JButton sendenButton = new JButton("Senden");
		sendenButton.addActionListener(e -> {
			try {
				controller.senden(text_senden.getText(), text_an.getText());
			} catch (RemoteException ex) {
				ex.printStackTrace();
			}
		});
		mittelPane.add(sendenButton, BorderLayout.WEST);

		JButton empfangenButton = new JButton("Empfangen");
		empfangenButton.addActionListener(e -> {
			try {
				controller.empfangen(text_von.getText());
			} catch (RemoteException ex) {
				ex.printStackTrace();
			}
		});
		untenPane.add(empfangenButton, BorderLayout.WEST);

		/* Text */
		text_verbinden = new JTextField();
		text_verbinden.setText("ServerAdresse");
		obenPane.add(text_verbinden, BorderLayout.EAST);
		text_verbinden.setColumns(10);

		text_senden = new JTextField();
		text_senden.setText("Nachricht");
		mittelPane.add(text_senden, BorderLayout.EAST);
		text_senden.setColumns(10);

		text_an = new JTextField();
		text_an.setText("Empfaenger");
		mittelPane.add(text_an, BorderLayout.CENTER);
		text_an.setColumns(10);

		text_empfangen = new JTextArea();
		text_empfangen.setToolTipText("Hier wird die Nachricht angezeigt");
		text_empfangen.setEditable(false);
		untenPane.add(text_empfangen, BorderLayout.SOUTH);

		text_von = new JTextField();
		text_von.setText("Nachrichten f\u00FCr?");
		untenPane.add(text_von, BorderLayout.CENTER);
		text_von.setColumns(10);

	}

	private void bindServer() {
		if (!text_verbinden.getText().isEmpty()) {
			try {
				controller.verbinden(text_verbinden.getText());
			} catch (RemoteException | NotBoundException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("Keine Serveradresse angegeben");
		}
	}

	public void textAnzeigen(String text) {
		text_empfangen.append(text);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// text_empfangen.setText(this.controller.getLastMessages());

		String msg = this.controller.getLastMessages();
		if (msg != null && !msg.isEmpty())
			textAnzeigen(this.controller.getLastMessages());
	}

}
