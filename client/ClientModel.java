package client;

import general.IConstants;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.MessageService;
import server.Server;

public class ClientModel implements IConstants {

    MessageService server;
    ClientApp controller;

    private String lastMsg;

    public ClientModel(ClientApp controller) {

	if (controller == null) {
	    throw new IllegalArgumentException("Invalid ClientApp object");
	}

	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new SecurityManager());
	}

	this.controller = controller;
	this.lastMsg = "";
    }

    public void verbinden(String addr) throws RemoteException,
	    NotBoundException {
	if (addr == null || addr.isEmpty()) {
	    return;
	}

	Registry registry = LocateRegistry.getRegistry(addr);
	server = (Server) registry.lookup(SERVER_NAME);
    }

    public void get_all_msg(String clientID) throws RemoteException {

	StringBuilder tmp = new StringBuilder();
	while (server.nextMessage(clientID) != null) {
	    System.out.println(server.nextMessage(clientID));
	    tmp.append(server.nextMessage(clientID) + "\n");
	}

	this.lastMsg = tmp.toString();
    }

    public void send_msg(String empf, String inhalt) {

	int triesLeft = MAX_RETRIES;
	do {
	    try {
		server.newMessage(empf, inhalt);

		System.out.println("Message sent!");
		break;
	    } catch (RemoteException ex) {
		triesLeft--;

		System.out.println("Failed to send message.");
		System.out.println("Retries left: " + triesLeft);
	    }

	    try {
		if (triesLeft > 0) {
		    System.out.println("Waiting for " + RETRY_TIMEOUT
			    + "ms before next retry...");
		    wait(RETRY_TIMEOUT);
		}
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	} while (triesLeft > 0);
    }

    public String getLastMessages() {
	return lastMsg;
    }
}
