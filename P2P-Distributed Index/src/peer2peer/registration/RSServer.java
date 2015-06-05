package peer2peer.registration;
import java.net.*;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

import peer2peer.bean.Peer;
import peer2peer.constants.P2Pconstants;
import peer2peer.registration.RegistrationMessage;
 
/**
 * RS Server: Contains a multi-threaded socket server that takes request from each Peer and does
 * various tasks.
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 
 */
public class RSServer extends Thread
{
	
	/** The Constant RS_PORT. */
	final static int RS_PORT = P2Pconstants.RS_PORT; //Arbitrary port number
	
	/** The peer list. */
	static List<Peer> peerList = Collections.synchronizedList(new LinkedList<Peer>());;
	
	/** The cookie. */
	static AtomicInteger cookie = new AtomicInteger(3);
	
	/**
	 * Gets the cookie value.
	 *
	 * @return the cookie value
	 */
	private static int getCookieValue() {
		return cookie.addAndGet(1);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) 
	{
		try {
			System.out.println("Starting the RS Server.");
			new RSServer().startServer();
		} catch (Exception e) {
			System.out.println("I/O failure: " + e.getMessage());
			e.printStackTrace();
		}
 
	}
 
	/**
	 * Start server.
	 *
	 * @throws Exception the exception
	 */
	public void startServer() throws Exception {
		ServerSocket serverSocket = null;
		boolean listening = true;
 
		try {
			serverSocket = new ServerSocket(RS_PORT);
			new RegistrationServerTTLMaintainance().start();
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + RS_PORT);
			System.exit(-1);
		}
 
		while (listening) {
			handleClientRequest(serverSocket);
		}
 
		serverSocket.close();
	}
 
	/**
	 * Handle client request.
	 *
	 * @param serverSocket the server socket
	 */
	private void handleClientRequest(ServerSocket serverSocket) {
		try {
			new ConnectionRequestHandler(serverSocket.accept()).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * Handles client connection requests. 
	 */
	public class ConnectionRequestHandler implements Runnable{
		
		/** The _socket. */
		private Socket _socket = null;
	
		/**
		 * Instantiates a new connection request handler.
		 *
		 * @param socket the socket
		 */
		public ConnectionRequestHandler(Socket socket) {
			_socket = socket;
		}
		
		/**
		 * Register.
		 *
		 * @param regMessage the reg message
		 */
		private  void register(RegistrationMessage regMessage) {
			//Peer peer = new Peer(hostName, getCookieValue(), true, 7200,
				//	RFCServerPort, new Date().getTime());
	       System.out.println("Received request for Register at RS server.");
			Peer peer = new Peer(regMessage.getCookie()); 
			int index = peerList.indexOf(peer);
			if (index != -1) {
				System.out.println("The host is already present in the peerlist\n");
				peer = peerList.get(index);
				peer.setTTL(P2Pconstants.TTL);
				peer.setPortNumber(regMessage.getRFCServerport());
				peer.setActive(true);
				peer.setLastRegisteredDate(new Date());
			} else {
				System.out.println("Addign the peer into the peerlist");
			    peer.setActive(true);
		        peer.setCookie(getCookieValue());
		        peer.setHostName(regMessage.getHostName());
		        System.out.println(regMessage.getHostName());
		  	    peer.setPortNumber(regMessage.getRFCServerport());
		        peer.setTTL(P2Pconstants.TTL);
		        peer.setLastRegisteredDate(new Date());
		        regMessage.setCookie(peer.getCookie());
				peerList.add(peer);
			}
		}

		/**
		 * Leave.
		 *
		 * @param regMsg the reg msg
		 */
		private  void leave(RegistrationMessage regMsg) {
			System.out.println("Received request for leave at RS server.");
			Peer peer = new Peer(regMsg.getCookie());
			int index = peerList.indexOf(peer);
			if (index != -1) {
				System.out.println("Peer with cookie value" +regMsg.getCookie()+"has left the server.");
				peer = peerList.get(index);
				peer.setActive(false);
				peer.setTTL(0);
			}
		}

		/**
		 * P query.
		 *
		 * @return the list
		 */
		private  List<Peer> PQuery() {
			System.out.println("Received request for fetching active peers at RS server.");
			synchronized (peerList) {
			 List<Peer> localPeerList = new LinkedList<Peer>();
				for (Iterator iterator = peerList.iterator(); iterator.hasNext();) {
					Peer peer = (Peer) iterator.next();
					if (peer.isActive()) {
						localPeerList.add(peer);
					}
				}
				return localPeerList;
			}
		}

		/**
		 * Keep alive.
		 *
		 * @param regMessage the reg message
		 */
		private  void keepAlive(RegistrationMessage regMessage) {
			System.out.println("Received request for keepAlive at RS server.");
			Peer peer = new Peer(regMessage.getCookie());
			int index = peerList.indexOf(peer);

			if (index != -1) {
				peer = peerList.get(index);
				peer.setTTL(7200);
				peer.setActive(true);
			}

		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() 
		{
			try {

			    System.out.println("Just connected to "+ _socket.getRemoteSocketAddress());
				ObjectOutputStream toClient = new ObjectOutputStream(
						new BufferedOutputStream(_socket.getOutputStream()));
				ObjectInputStream fromClient = new ObjectInputStream(
						new BufferedInputStream(_socket.getInputStream()));
				RegistrationMessage msgRequest = (RegistrationMessage) fromClient.readObject();
				List<Peer> activeList = null;
				// 1-REGISTER,2-LEAVE,3-PQUERY,4-KEEPALIVE,5-SUCESS
				switch (msgRequest.getState()) {
				case P2Pconstants.REGISTER:
					register(msgRequest);
					break;
				case P2Pconstants.LEAVE:
					leave(msgRequest);
					break;
				case P2Pconstants.PQUERY:
					activeList = PQuery();
					break;
				case P2Pconstants.KEEPALIVE:
					keepAlive(msgRequest);
					break;
				}
				RegistrationMessage replyToClient = new RegistrationMessage(
						P2Pconstants.SUCESS, msgRequest.getHostName(),
						msgRequest.getRFCServerport(), activeList, msgRequest.getCookie());
				toClient.writeObject(replyToClient);
				toClient.flush();
			} catch (IOException e) {
				System.out.println("IO Exception\n");
			} catch (ClassNotFoundException ce) {
				System.out.println("Class Not Found\n");
			} finally {
				try {
					_socket.close();
				} catch (IOException e) {
				}
			}	
						
		}
		}
	private  class RegistrationServerTTLMaintainance extends Thread {
		public void run() {
			for (;;) {
				System.out.println("updating TTL values for peers\n");
				try {
					Thread.sleep(30000); 
					if (peerList != null &&  !peerList.isEmpty()) {
						synchronized (peerList) {
							for (Iterator<Peer> peer = peerList.iterator(); peer
									.hasNext();) {
								Peer nextPeer = (Peer) peer.next();
								if (nextPeer.isActive()) {
									nextPeer.setTTL(nextPeer.getTTL() - 30);
									if (nextPeer.getTTL() <= 0) {
										nextPeer.setActive(false);
									}
								}
							}
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Failed maintainance service");
				}
			}
		}
	}	
}
 

	