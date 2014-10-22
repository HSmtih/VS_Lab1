package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

// -Djava.security.policy=policy.policy

public class ClientApp {
	private ClientModel model;
	private ClientGUI gui;

	public ClientApp() {
		gui = new ClientGUI(this);
		model = new ClientModel(this);
	}

	public static void main(String[] args) {
		new ClientApp();

	}

	public void senden(String text, String empf) throws RemoteException {
		model.send_msg(empf, text);
	}

	public void empfangen(String ID) throws RemoteException {
		model.get_all_msg(ID);
	}

	public void verbinden(String ID) throws RemoteException, NotBoundException{
		model.verbinden(ID);		
	}
	public void textAnzeigen(String ID) throws RemoteException, NotBoundException{
		//model.verbinden(ID);		
	}
	
}
