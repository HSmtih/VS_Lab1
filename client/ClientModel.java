package client;

import general.IConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

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
		
		try {
			RMISocketFactory.setSocketFactory( new RMISocketFactory()
			{
			    public Socket createSocket( String host, int port )
			        throws IOException
			    {
			        Socket socket = new Socket();
			        socket.setSoTimeout( INVOCATION_TIMEOUT );
			        socket.setSoLinger( false, 0 );
			        socket.connect( new InetSocketAddress( host, port ), INVOCATION_TIMEOUT );
			        return socket;
			    }

			    public ServerSocket createServerSocket( int port )
			        throws IOException
			    {
			        return new ServerSocket( port );
			    }
			} );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (System.getSecurityManager() == null) {
		// System.setSecurityManager(new SecurityManager());
		// }

		this.controller = controller;
		this.lastMsg = "";
	}

	public void verbinden(String addr) throws RemoteException,
			NotBoundException {
		if (addr == null || addr.isEmpty()) {
			return;
		}

		Registry registry = LocateRegistry.getRegistry(addr);
		server = (MessageService) registry.lookup(SERVER_NAME);
	}

	public void get_all_msg(String clientID) throws RemoteException {

		StringBuilder tmp = new StringBuilder();
		String msg = null;

		do {
			msg = server.nextMessage(clientID);
			if (msg != null) {
				System.out.println(msg);
				tmp.append(msg + "\n");
			}

		} while (msg != null);


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
					Thread.sleep(RETRY_TIMEOUT);     
				}
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		} while (triesLeft > 0);
	}

	public String getLastMessages() {
		return lastMsg;
	}
}
