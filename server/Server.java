package server;

import general.IConstants;
import general.Message;

import java.awt.List;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

//-Djava.security.policy=policy.policy

public class Server extends UnicastRemoteObject implements MessageService,
		IConstants {

	private static final Logger log = Logger.getLogger(Server.class.getName());

	private HashMap<String, LinkedList<Message>> client_nachrichten = new HashMap<>();
	private HashMap<String, Timer> client_timer = new HashMap<>();

	private int msg_ID = 0;

	public Server() throws RemoteException {
		super();
	}

	public static void main(String[] args) {

		Handler handler = null;
		try {
			handler = new FileHandler(LOG_FILE_PATH);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		log.addHandler(handler);

		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }

		try {
			Server engine = new Server();
			// Server stub = (Server) UnicastRemoteObject.exportObject(engine,
			// 0);
			Registry registry = LocateRegistry
					.createRegistry(Registry.REGISTRY_PORT);
			registry = LocateRegistry.getRegistry();
			registry.rebind(SERVER_NAME, engine);
			log.info("Server bound successfully");
		} catch (Exception e) {
			System.err.println("Server exception:");
			e.printStackTrace();
		}
	}

	@Override
	public String nextMessage(String clientID) throws RemoteException {

		String erg;

		LinkedList<Message> clientList = client_nachrichten.get(clientID);
		if (clientList == null) {
			log.warning("No list for (" + clientID + ") found!");
			return null;
		}

		log.info("List for (" + clientID + ") found.\nList contains: "
				+ clientList.size() + " messages");

		if (client_nachrichten.get(clientID).isEmpty()) {
			System.out.println("No more message, returning NULL \n");
			return null;
		}

		erg = client_nachrichten.get(clientID).pollFirst().getText();
		log.info("Message retrieved for (" + clientID + "): " + erg);

		// Timer zur�cksetzen
		resetClientTimer(clientID);
		System.out.println("Message returned: " + erg +"\n");
		return erg;
	}

	public void newMessage(final String clientID, String message)
			throws RemoteException {

		log.info("newMessage() aufgrufen von: " + clientID);
		System.out.println("Client: "+ clientID +"  sent Message: "+ message);
		Message msg = new Message(message, ++msg_ID, new Date());

		// Pr�fe ob Client bereits bekannt
		if (client_nachrichten.get(clientID) == null) {
			log.info(clientID + " bis nicht im System");

			// Falls neu, neuen Nachrichtenliste erstellen
			LinkedList<Message> temp_list = new LinkedList<Message>();
			temp_list.add(msg); // Nachricht anf�gen
			client_nachrichten.put(clientID, temp_list); // Liste mit Client
															// verkn�pfen

			resetClientTimer(clientID);
		} else {
			// Falls bekannt, Nachricht anf�gen
			log.info(clientID + " wieder erkannt");

			// Pr�fen on max L�nge erreicht
			if (client_nachrichten.get(clientID).size() == MAX_QUEUE_LEN) {
				client_nachrichten.get(clientID).removeFirst();
			}

			client_nachrichten.get(clientID).add(msg);
			resetClientTimer(clientID);
		}


	}

	private void resetClientTimer(String clientID) {

//		if (!client_timer.containsKey(clientID)) {
//			return;
//		}

		Timer timer = client_timer.get(clientID); // alten Timer abbrechen
		if (timer != null) {
			timer.cancel();
			System.out.println("Timer canceled: "+ clientID);
		}

		timer = new Timer(); // neuen Timer erstellen
		timer.schedule(new Task(clientID), CLIENT_TIMEOUT);
		System.out.println("Timer created: "+ clientID);
		client_timer.put(clientID, timer);
	}

	// Timerklasse zum vergessen eines Clients
	class Task extends TimerTask {
		String ID = new String();

		Task(String ID) {
			this.ID = ID;
		}

		public void run() {
			client_nachrichten.remove(ID);
			log.info("Client+Messages Deleted: "+ ID);
		}
	}
}
