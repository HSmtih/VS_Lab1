package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.MessageService;
import server.Server;

public class ClientModel {

	/**
	 * @param args
	 */
	private final long retryTimeout=3000;
	private final int maxRetries=5;
	
	MessageService server;
	private String addr = "sdasd";		// Serveradresse
	int s = 3; // max Serverausfallzeit
	ClientApp controller;

	public ClientModel(ClientApp controller) {
		this.controller = controller;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {			
			//verbinden();

		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}

	}

	public void verbinden(String addr) throws RemoteException, NotBoundException {
		this.addr=addr;
		String name = "Server";
		Registry registry = LocateRegistry.getRegistry(addr);
		server = (Server) registry.lookup(name);
	}

	public void get_all_msg(String clientID) throws RemoteException {
		String tmp = new String();
		while (server.nextMessage(clientID) != null) {
			System.out.println(server.nextMessage(clientID));
			tmp = tmp + server.nextMessage(clientID) +"\n";
		}
		

	}

	public  void send_msg(String empf, String inhalt) {
		// TODO Serverausfälle abfangen
		
		int triesLeft = maxRetries;
		
		do {
			try {
				server.newMessage(empf, inhalt);
				
				System.out.println("Message sent!");
				break;
			}
			catch (RemoteException ex) {
				triesLeft--;
				
				System.out.println("Failed to send message.");
				System.out.println("Retries left: " + triesLeft);
			}
						
			try {
				if (triesLeft > 0)
				{
					System.out.println("Waiting for " + retryTimeout + "ms before next retry...");
					wait(retryTimeout);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} while (triesLeft > 0);

	}

}
