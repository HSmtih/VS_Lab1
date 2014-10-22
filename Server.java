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

public class Server extends UnicastRemoteObject implements MessageService {
	private HashMap<String, LinkedList<Message>> client_nachrichten = new HashMap<>();
	private HashMap<String, Timer> client_timer = new HashMap<>();
	private int queue_lenght;
	private int msg_ID = 0;
	private int t = 60000; // delay bis Client vergessen
	private static final Logger log = Logger.getLogger( Server.class.getName() );
	private static Handler handler;	

	public Server() throws RemoteException{
		super();
	}
	
	public static void main(String[] args) {
		try {
			handler = new FileHandler("log.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.addHandler(handler);
		
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "TestServer";
            Server engine = new Server();
       //     Server stub = (Server) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registry = LocateRegistry.getRegistry();
            registry.rebind(name, engine);
            System.out.println("Server bound");
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
		
  
}
	@Override
	public String nextMessage(String clientID) throws RemoteException {
		String erg = null;
		Timer timer;
		if (client_nachrichten.get(clientID) != null) {
			System.out.println("List for "+ clientID +" found");
			// N�chste Nachricht bereitstellen
			
			if (client_nachrichten.get(clientID).isEmpty()) {
				System.out.println("List for "+ clientID +" is empty!");
				return erg;
			}
			
			erg = client_nachrichten.get(clientID).pollFirst().getText();
			System.out.println(erg);
			
			// Timer zur�cksetzen
			resetClientTimer(clientID);
			
//			client_timer.get(clientID).cancel(); 		// alten Timer abbrechen
//			timer = new Timer(); 						// neuen Timer erstellen
//			timer.schedule(new Task(clientID), t);
//			client_timer.put(clientID, timer);
		}		
		
		return erg;
	}
	
	// Timerklasse zum vergessen eines Clients
	class Task extends TimerTask {
		String ID = new String();
		Task(String ID ){
			this.ID=ID;
		}
		public void run() {
			client_nachrichten.remove(ID);
		}
	}
	
	public void newMessage(final String clientID, String message)
			throws RemoteException {
		
		log.info("newMessage() aufgrufen von: " + clientID);
		Message msg = new Message(message, ++msg_ID, new Date());
		//Timer timer;
				
		// Pr�fe ob Client bereits bekannt
		if (client_nachrichten.get(clientID) == null) {
			log.info(clientID + " bis nicht im System");
			
			// Falls neu, neuen Nachrichtenliste erstellen
			LinkedList<Message> temp_list = new LinkedList<Message>();
			temp_list.add(msg);		// Nachricht anf�gen
			client_nachrichten.put(clientID, temp_list);	// Liste mit Client verkn�pfen
			
//			timer = new Timer();							// Neuen Timer erstellen
//			timer.schedule(new Task(clientID), t);					// Timer aufziehen
//			client_timer.put(clientID, timer);				// Timer mit Client verbinden

			resetClientTimer(clientID);
			System.out.println("# of clients: " + client_nachrichten.size());
		} else {
			// Falls bekannt, Nachricht anf�gen
			log.info(clientID + " wieder erkannt");
			
			// Pr�fen on max L�nge erreicht
			if (client_nachrichten.get(clientID).size() == queue_lenght) {
				client_nachrichten.get(clientID).removeFirst();
			}			
			
			client_nachrichten.get(clientID).add(msg);
			resetClientTimer(clientID);
			
//			client_timer.get(clientID).cancel(); 		// alten Timer abbrechen
//			timer = new Timer(); 						// neuen Timer erstellen
//			timer.schedule(new Task(clientID), t);
//			client_timer.put(clientID, timer);
		}

	}
	
	private void resetClientTimer(String clientID) {
		
		Timer timer = client_timer.get(clientID);		// alten Timer abbrechen
		if (timer != null) {
			timer.cancel();
		}
		
		timer = new Timer(); 						// neuen Timer erstellen
		timer.schedule(new Task(clientID), t);
		client_timer.put(clientID, timer);
	}

}
