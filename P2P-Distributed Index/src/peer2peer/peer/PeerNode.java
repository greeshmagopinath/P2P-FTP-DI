package peer2peer.peer;



import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import peer2peer.bean.Peer;
import peer2peer.bean.RFCIndex;
import peer2peer.constants.P2Pconstants;
import peer2peer.registration.RegistrationMessage;

/**
 * The Class PeerNode.This represnts the each Peer that participates in the P2P application.This class
 * when invoked starts a client as well as a server. Registration server port and the port number for peer
 * server is given as input argument.
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 *
 */
public class PeerNode {

	/** The index list. */
	static List<RFCIndex> indexList;
	
	/** The registration server ip. */
	static String REGISTRATION_SERVER_IP;
	
	/** The rfc port. */
	static int RFC_PORT;
	
	/** The cookie. */
	static int cookie = -1;
	
	/** The jframe. */
	static JFrame jframe;
	
	/** The text area for notification. */
	static JTextArea textAreaForNotif;
	
	/** The listening flag for the Peer server. */
	static AtomicBoolean listening = new AtomicBoolean(true);
		 
	
	/**
	 * Gets the index list.
	 *
	 * @return the index list
	 */
	public static List<RFCIndex> getIndexList() {
		return indexList;
	}

	/**
	 * Sets the index list.
	 *
	 * @param indexList the new index list
	 */
	public static void setIndexList(List<RFCIndex> indexList) {
		PeerNode.indexList = indexList;
	}

	/**
	 * Sets the cookie if exists.
	 */
	private  void setCookieIfExists() {
		String userPath =System.getProperty("user.home");
		File file = new File(userPath + "/cookie.txt");
		try {
		if(file.exists()){
			Scanner in = new Scanner(new FileReader(file));
			if (in.hasNextInt()) {
				cookie = in.nextInt();
				writeToTextArea("\nCookie is stored in the local system.\n");
				System.out.println("Cookie is stored in the local system.");
			}
		}
		} catch (IOException e) {
		  writeToTextArea("Cookie is not present in the local system\n");
		  System.out.println("Cookie is not present in the local system"); 
		}
	}
	
	/**
	 * Save cookie to disk.
	 *
	 * @param cookieValue the cookie value
	 */
	private  void saveCookieToDisk(int cookieValue) {
		BufferedWriter output = null;
		try {
			String userPath = System.getProperty("user.home");
			File file = new File(userPath + "/cookie.txt");
			output = new BufferedWriter(new FileWriter(file));
			output.write(String.valueOf(cookieValue));
			System.out.println("Cookie values are stored in the physical disk.");
			 writeToTextArea("\nCookie values are stored in the physical disk at" + userPath + "/cookie.txt\n");
				
			output.close();
		} catch (IOException e) {
			System.out.println("Cookie values were not saved to the disk");
		} finally {
			try {
				output.close();
			} catch (Exception ex) {
				System.out.println("File output streamnot closed succesfully.");
			}
		}
	}

	/**
	 * Sets the up data.
	 */
	public static void setUpData() {
		indexList = Collections.synchronizedList(new LinkedList<RFCIndex>());
		String userPath =System.getProperty("user.home");
		
		File folder = new File(userPath + "/RFC");
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
		System.out.println("No of files:" + listOfFiles.length);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String str = listOfFiles[i].getName().replaceAll("\\D+", "");
				int index = Integer.parseInt(str);
				String IP = null;
				try {
					IP = Inet4Address.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
				  continue;
				}catch (NumberFormatException ex) {
					continue;
				}
				indexList.add(new RFCIndex(index, listOfFiles[i].getName(), IP,
						7200));
			}
		}
		System.out.println("No of RFC Found Locally are" + indexList.size());
		}
	}

	/**
	 * Register server.
	 *
	 * @param RFCPort the rFC port
	 */
	private  void registerServer(int RFCPort) {
		writeToTextArea("Peer Server Registering at RS server\n");
				int serverPort = P2Pconstants.RS_PORT;
		Socket socket = null;
		ObjectOutputStream toServer = null;
		ObjectInputStream fromServer = null;
		try {
			String localHostName = InetAddress.getLocalHost().getHostAddress();
		   System.out.println("Connecting to server on port " + serverPort);
			socket = new Socket(REGISTRATION_SERVER_IP, serverPort);
			System.out.println("Just connected to "
					+ socket.getRemoteSocketAddress());
			toServer = new ObjectOutputStream(new BufferedOutputStream(
					socket.getOutputStream()));
			RegistrationMessage msgToSend = new RegistrationMessage(1,localHostName,RFCPort, null , cookie);
			toServer.writeObject(msgToSend);
			toServer.flush();

			// This will block until the corresponding ObjectOutputStream
			// in the server has written an object and flushed the header
			fromServer = new ObjectInputStream(new BufferedInputStream(
					socket.getInputStream()));
			RegistrationMessage msgFromReply = (RegistrationMessage) fromServer
					.readObject();
			if (msgFromReply.getState() == 5) {
				writeToTextArea("Peer Server Succesfully registered\n");
				System.out.println("Peer Server Succesfully registered.");
				if(msgFromReply.getCookie() != cookie) {
					System.out.println("Saving the cookie value to disk");
					writeToTextArea("Saving the cookie value to disk\n");
					saveCookieToDisk(msgFromReply.getCookie());
				}
			} else {
				writeToTextArea("Peer Server  registeration failed\n");
				System.out.println("Peer Server  registeration failed.");
			}
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Run client.
	 */
	private void runClient(){
		
		System.out.println("Client Started");
		writeToTextArea("Client Started");
		try {
			new PeerServer(RFC_PORT).start();
		} catch (Exception e) {
			System.out.println("I/O failure: " + e.getMessage());
			e.printStackTrace();
		}
 
		writeToTextArea("\nPeer Server is starting at port:" + RFC_PORT);
		setCookieIfExists();
		registerServer(RFC_PORT);
		writeToTextArea("\nStarting Peer Server..\n");
		writeToTextArea("\nStarting keepAlive maintainance service..\n");
		writeToTextArea("Starting RFC TTL  maintainance service.\n");
		new PeerServerTTLMaintainance().start();
		new KeepAliveRequestGenerator().start();

		while(true) {
	    Object[] options1 = { "Submit","Close the application"};
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 30));
		panel.add(new JLabel("Enter a RFC number"));
		JTextField textField = new JTextField(10);
		JScrollPane thePane = new JScrollPane(textField);
		panel.add(thePane);
		
		String Ip = null;
		RFCIndex desiredRFCIndex = null;
		Peer desiredPeer = null;
		  
		try {
			Ip = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}

		int result = 0;
			result = JOptionPane.showOptionDialog(panel, panel,
					"Peer2Peer Application@" + Ip,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options1, null);
		if (result == JOptionPane.YES_OPTION) {
			desiredRFCIndex = null;
			textAreaForNotif.setText("");
			writeToTextArea("Peer Client started\n");
			if(textField.getText() == null){
				JOptionPane.showMessageDialog(null,"Invalid input.Please enter a valid number.");
				continue;
			}
			int rfcNumber;
			try
			{
			rfcNumber = Integer.parseInt(textField.getText());
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null,"Invalid input.Please enter a valid number.");
				continue;
			}
			textAreaForNotif.append("Downloading the RFC" + rfcNumber +"\n");
			textAreaForNotif.append("Contacting the the RS Server for getting peer list....\n");
			List<Peer> peerList = null;
			RegistrationMessage requestMessage = new RegistrationMessage(P2Pconstants.PQUERY, Ip,0,peerList,cookie);
			Socket socket = null;
			long startTime = System.currentTimeMillis();
			try
			{
				socket = new Socket(REGISTRATION_SERVER_IP,P2Pconstants.RS_PORT); 
			System.out.println("Just connected to " + socket.getRemoteSocketAddress()); 
			ObjectOutputStream toServer = new ObjectOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
			toServer.writeObject(requestMessage);
			toServer.flush();
			System.out.println("Waiting from server to receive active Peer List");
			textAreaForNotif.append("Waiting to receive active Peer List from RS server ....\n");
			// This will block until the corresponding ObjectOutputStream 
			// in the server has written an object and flushed the header
			ObjectInputStream fromServer = new ObjectInputStream(
					new BufferedInputStream(socket.getInputStream()));
			RegistrationMessage msgFromReply = (RegistrationMessage)fromServer.readObject();
			List<Peer> peerlist = msgFromReply.getPeerList();
			textAreaForNotif.append("Received active peer list from the RS server ....\n");
			textAreaForNotif.append("Checking for the peer with the active RFC....\n");
			
			//Find if there is any peer in active list already.
			desiredRFCIndex = checkIfPeerConsistsRFC(rfcNumber);
			if (desiredRFCIndex != null) {
				for (Iterator iterator = peerlist.iterator(); iterator
						.hasNext();) {
					Peer peer = (Peer) iterator.next();
					if(peer.getHostName().equals(desiredRFCIndex.getHostname())) {
					    desiredPeer = peer;
					}
				}
			} else 
			{
		    for (Iterator iterator = msgFromReply.getPeerList().iterator(); iterator
					.hasNext();) {
				Peer peer = (Peer) iterator.next();
				System.out.println("host:" + peer.getHostName());
				PeerMessage peerMessage = getRFCIndex(peer, textAreaForNotif);
				if (peerMessage != null) {
					desiredRFCIndex = checkIfRFCIndexExists(rfcNumber);
					if(desiredRFCIndex != null) {
						writeToTextArea("DesiredPeer is:" + peer.getHostName()+":"+peer.getPortNumber()+"\n");
						System.out.println("desiredPeer:" + peer.getHostName()+":"+peer.getPortNumber());
						desiredPeer = peer;
						break;
					}
				}
		    }
			}
		   } catch (IOException e) {
			System.out.println("IO Exception\n");
		} catch (ClassNotFoundException ce) {
			System.out.println("Class Not Found\n");
		} finally {
			try {
				if (socket != null){
				socket.close();}
			} catch (IOException e) {
			}
		}
			 //make connection to first active list to get the RFC
		    if(desiredRFCIndex == null) {
		        JOptionPane.showMessageDialog(null,"OOOps...None of the Servers have this file\n");
		         	
		    } else {
		    		textAreaForNotif.append("\n\nFound a peer having the file.Trying to download from" + desiredRFCIndex.getHostname() + "at port"
			     		+ desiredPeer.getPortNumber()+"\n\n");
				
		    	Socket newSocket = null;
		     	try{
		     		writeToTextArea("Connecting to Peer:"+desiredPeer.getHostName()+":"+desiredPeer.getPortNumber());
		     		System.out.println("Connecting to Peer:"+desiredPeer.getHostName()+":"+desiredPeer.getPortNumber());
		     		if (desiredPeer.getHostName().equals(InetAddress.getLocalHost().getHostAddress())){
		     			newSocket = new Socket("127.0.0.1",desiredPeer.getPortNumber()); 
					} else 
					{
						newSocket = new Socket(desiredPeer.getHostName(),desiredPeer.getPortNumber()); 
					}
		     		System.out.println("\nJust connected to " + newSocket.getRemoteSocketAddress()); 
					writeToTextArea("\nJust connected to " + newSocket.getRemoteSocketAddress() + "to download the required RFC");
					ObjectOutputStream toServer = new ObjectOutputStream(
							new BufferedOutputStream(newSocket.getOutputStream()));
					String OS = System.getProperty("os.name");
					String ipAddress = InetAddress.getLocalHost().getHostAddress();
					StringBuilder requestHeader = new StringBuilder("GET RFC ");
					requestHeader.append(rfcNumber);
					requestHeader.append(" P2P-DI/1.0");
					requestHeader.append("\nhost: ");
					requestHeader.append(ipAddress);
					requestHeader.append("\n");
					requestHeader.append("OS:");
					requestHeader.append(OS);
			    	textAreaForNotif.append("\n\nRequest Header is:\n" + requestHeader.toString());
			    	System.out.println("\n\nRequest Header is:\n" + requestHeader.toString());
			    	PeerMessage requestMsg = new PeerMessage();
					requestMsg.setState(P2Pconstants.GETRFC);
					requestMsg.setRequest(requestHeader.toString());
					requestMsg.setData(String.valueOf(rfcNumber));
					toServer.writeObject(requestMsg);
					toServer.flush();
					writeToTextArea("\n\nWaiting to get RFC  from the peer server...\n");
					System.out.println("Waiting from server to recieve RFC.");
					// This will block until the corresponding ObjectOutputStream 
					// in the server has written an object and flushed the header
					ObjectInputStream fromServer = new ObjectInputStream(
							new BufferedInputStream(newSocket.getInputStream()));
					System.out.println("Received RFC from server");
					
					PeerMessage msgFromReply = (PeerMessage)fromServer.readObject();
					if (msgFromReply != null && msgFromReply.getState() == P2Pconstants.SUCESS &&
							msgFromReply.getResponse().equals(P2Pconstants.OK)) {
						writeToTextArea("Received the RFC file as response from the peer server " + desiredPeer.getHostName());
						 JTextArea jta = new JTextArea();
						 jta.append(msgFromReply.getData());
						BufferedWriter output = null;

						String userPath = System.getProperty("user.home");
						String folder = userPath + "/RFC";
						File dir = new File(folder);
						if(dir != null && dir.mkdir()){
							System.out.println("\nFolder" +dir + " created for download.");
							writeToTextArea("\nFolder " +dir + " created for downlo");
							
						}
						
						String filePathString = userPath + "/RFC/" + "rfc"+rfcNumber+ "."+msgFromReply.getFormat();	
						try {
								File file = new File(filePathString);
								output = new BufferedWriter(new FileWriter(file));
								output.write(String.valueOf(msgFromReply.getData()));
								writeToTextArea("\nFile Saved to disk.");
							} catch (IOException e) {
								System.out.println("File not Saved to disk.");
							} finally {
								try {
									output.close();
								} catch (Exception ex) {
									System.out.println("File output streamnot closed succesfully.");
								}
							}

						 textAreaForNotif.append("\nRFC is downloaded to location:" +filePathString);
						 JScrollPane scroller = new JScrollPane(jta){
				                @Override
				                public Dimension getPreferredSize() {
				                    return new Dimension(580, 420);
				                }
				            };
				            updateRFCIndexForLatestDownload(String.valueOf(rfcNumber));
				            long endTime = System.currentTimeMillis();
							System.out.println("That took " + (endTime - startTime) + " milliseconds");
							textAreaForNotif.append("\n\n\nTotal time taken for download:" + (endTime - startTime) + " milliseconds.\n");
							JOptionPane.showMessageDialog(null, scroller, "Content of RFC" + rfcNumber, JOptionPane.PLAIN_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,"File could not be downloaded.try again.");
					}
		    	} catch (IOException e) {
						System.out.println("IO Exception\n");
						writeToTextArea("RFC not downloaded due to technical issue..Please try again.");
					} catch (ClassNotFoundException ce) {
						System.out.println("Class Not Found\n");
					} finally {
						try {
							if (socket != null) {
							socket.close();}
						} catch (IOException e) {
						}
					}
		    }
		} else if (result == JOptionPane.CLOSED_OPTION || result == JOptionPane.NO_OPTION){
			  //Once the operation is over , send leave message to the RS server.
				RegistrationMessage requestMessageForLeave = new RegistrationMessage(P2Pconstants.LEAVE, null,0,null,cookie);
				Socket socketForLeave = null;
				try {
				 	socketForLeave = new Socket(REGISTRATION_SERVER_IP,P2Pconstants.RS_PORT); 
					System.out.println("Just connected to " + socketForLeave.getRemoteSocketAddress() + "to leave the RS server"); 
					writeToTextArea("\nJust connected to RS server to unregister\n");
					ObjectOutputStream toServer = new ObjectOutputStream(
							new BufferedOutputStream(socketForLeave.getOutputStream()));
					toServer.writeObject(requestMessageForLeave);
					toServer.flush();
					System.out.println("Waiting from server to acknowledge RS Leave");
					writeToTextArea("\nWaiting from server to acknowledge RS Leave\n");
					
					// This will block until the corresponding ObjectOutputStream 
					 //in the server has written an object and flushed the header
					ObjectInputStream fromServer = new ObjectInputStream(
						new BufferedInputStream(socketForLeave.getInputStream()));
					System.out.println("Received acknowledgement  from RS server for leaving the system.");
					RegistrationMessage msgFromReplyForLeave = (RegistrationMessage)fromServer.readObject();
				    if(msgFromReplyForLeave.getState() == P2Pconstants.SUCESS) {
				    System.out.println("The peer is removed from the active list");	
				    writeToTextArea("\nThe peer is removed from the active list at RS server\n");
					
				   } else {
				    	System.out.println("The peer could not be removed from the active list");	
				        writeToTextArea("\nThe peer could not be removed from the active list at RS server\n");
				    }
				    listening.set(false);
					writeToTextArea("\nShutting down server at Peer");
					System.out.println("\nShutting down server at Peer");
				} catch (Exception e) {
				} finally {
			    	try {
						if(socketForLeave != null) {
							socketForLeave.close();
						}
					} catch (IOException e) {
					}
			    }
			System.exit(0);
		}
		}
	}
	
	/**
	 * Write to text area.
	 *
	 * @param message the message
	 */
	private static void  writeToTextArea(String message){
		if (textAreaForNotif != null) {
		textAreaForNotif.append(message);
		}
	}	
	
	private RFCIndex checkIfPeerConsistsRFC(int index) {
		RFCIndex reqRFC = new RFCIndex();
		reqRFC.setRFCNumber(index);
		int pos = indexList.indexOf(reqRFC);
		if(pos == -1) {
			return null;
		}
		return indexList.get(pos);
	}
	/**
	 * Gets the rFC index.
	 *
	 * @param peer the peer
	 * @param textAreaForNotif the text area for notif
	 * @return the rFC index
	 */
	private  PeerMessage getRFCIndex(Peer peer, JTextArea textAreaForNotif) {
		Socket socket = null;
		try
		{
		System.out.println("Connecting to:"+peer.getHostName()+":"+peer.getPortNumber()+"to get RFC list");
		if (peer.getHostName().equals(InetAddress.getLocalHost().getHostAddress())){
		     return null;
		} else 
		{
			socket = new Socket(peer.getHostName(),peer.getPortNumber()); 
		}
 	
		System.out.println("Just connected to " + socket.getRemoteSocketAddress() + "in getRFCIndex to get RFC list"); 
		ObjectOutputStream toServer = new ObjectOutputStream(
				new BufferedOutputStream(socket.getOutputStream()));
		String OS = System.getProperty("os.name");
		String ipAddress = InetAddress.getLocalHost().getHostAddress();
		StringBuilder requestHeader = new StringBuilder("GET RFC-Index P2P-DI/1.0");
		requestHeader.append("\nhost: ");
		requestHeader.append(ipAddress);
		requestHeader.append("\n");
		requestHeader.append("OS:");
		requestHeader.append(OS);
		textAreaForNotif.append("\n\nRequest Header is:\n" + requestHeader.toString());
		System.out.println("\n\nRequest Header is:\n" + requestHeader.toString());
		//JOptionPane.showMessageDialog(null, "Request Header is:\n" + requestHeader.toString());
		PeerMessage requestMessage = new PeerMessage();
		requestMessage.setState(P2Pconstants.RFCQUERY);
		requestMessage.setRequest(requestHeader.toString());
		toServer.writeObject(requestMessage);
		toServer.flush();
		System.out.println("Waiting from peer server to get RFC list.");
		// This will block until the corresponding ObjectOutputStream 
		// in the server has written an object and flushed the header
		ObjectInputStream fromServer = new ObjectInputStream(
				new BufferedInputStream(socket.getInputStream()));
		System.out.println("Received RFC List from peer server");
		PeerMessage msgFromReply = (PeerMessage)fromServer.readObject();
	    if (msgFromReply != null && msgFromReply.getState() == P2Pconstants.SUCESS &&
	    		msgFromReply.getResponse().equals(P2Pconstants.OK)) {
	    	if (msgFromReply.getPeerList() != null) {
	    		synchronized (indexList) {
	    			indexList.addAll(msgFromReply.getPeerList());
		    	}
	    		return msgFromReply;
	    	}
	    }
		} catch(Exception e) {
			return null;
		} finally {
			try {
				if (socket != null) {
				socket.close();}
			
			} catch (IOException e) {
			 return null;
			}
		}
		return null;
	}

	
	
	/**
	 * Update rfc index for latest download.
	 *
	 * @param RFCIndex the rFC index
	 */
	private  void updateRFCIndexForLatestDownload(String RFCIndex) {
		int rfcNo= 0;
		String rfcTitle = null;
		try
		{
		 rfcNo = Integer.parseInt(RFCIndex);
		 rfcTitle = "RFC" + rfcNo ; 
		} catch(NumberFormatException e) {
			System.out.println("RFC number conversion failed.");
		}
		String hostname;
		try {
			hostname = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		 System.out.println("Inet conversion failed.");
		 hostname = "localhost";
		}
		RFCIndex rfcIndex = new RFCIndex(rfcNo, rfcTitle, hostname, 7200);
		synchronized (indexList) {
			indexList.add(rfcIndex);
		}
		System.out.println("Added RFC"+RFCIndex.toString()+"to the RFC index list:");
		writeToTextArea("\nAdded RFC"+RFCIndex.toString()+"to the RFC index list.\n");
	}

	
	/**
	 * Check if rfc index exists.
	 *
	 * @param RFCindex the rF cindex
	 * @return the rFC index
	 */
	private RFCIndex checkIfRFCIndexExists(int RFCindex) {
		synchronized (indexList) {
			for (Iterator iterator = indexList.iterator(); iterator.hasNext();) {
				RFCIndex index = (RFCIndex) iterator.next();
				if (index.getRFCNumber() == RFCindex) {
					return index;
				}
			}
			return null;
		}
	}
	
	/**
	 * The Class PeerServer.
	 */
	private class PeerServer extends Thread {
		
		/** The _server port. */
		int _serverPort;

		/**
		 * Instantiates a new peer server.
		 *
		 * @param serverPort the server port
		 */
		public PeerServer(int serverPort) {
			_serverPort = serverPort;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			ServerSocket serverSocket = null;
			try {
				listening.set(true);
				System.out.println("Created socket at" + _serverPort);
				serverSocket = new ServerSocket(_serverPort);
			} catch (IOException e) {
				System.err.println("Could not listen on port: " + _serverPort);
				System.exit(-1);
			}
	 
			while (listening.get()) {
				handleClientRequest(serverSocket);
			}
	 
			try {
				System.out.println("Closing peer server socket");
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		 * The Class ConnectionRequestHandler.
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
			
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() 
			{
				try {
					System.out.println("Just connected to peer server"
							+ _socket.getRemoteSocketAddress() +"at peer server.");
					ObjectOutputStream toClient = new ObjectOutputStream(
							new BufferedOutputStream(_socket.getOutputStream()));
					ObjectInputStream fromClient = new ObjectInputStream(
							new BufferedInputStream(_socket.getInputStream()));
					PeerMessage msgRequest = (PeerMessage) fromClient.readObject();
					PeerMessage replyToClient = new PeerMessage();
					replyToClient.setResponse(P2Pconstants.NOTOK);
					if (msgRequest == null) {

					} else {
						switch (msgRequest.getState()) {
						case P2Pconstants.GETRFC:
							// Check if the RFC file exists
							boolean fileExits = checkIfFileExists(msgRequest
									.getData());
							System.out.println("File exists:" + fileExits);
							if (!fileExits) {
								System.out.println("File doesnt exist at Peer Server for RFC "+msgRequest.getData());
								replyToClient.setResponse(P2Pconstants.NOTOK);
								replyToClient.setState(P2Pconstants.SUCESS);
							} else {
								String[] FileContents = fetchFileContent(msgRequest
										.getData());
								if (FileContents == null) {
									replyToClient.setResponse(P2Pconstants.NOTOK);
									replyToClient.setState(P2Pconstants.SUCESS);
								} else {
									replyToClient.setResponse(P2Pconstants.OK);
									replyToClient.setState(P2Pconstants.SUCESS);
									replyToClient.setData(FileContents[0]);
									replyToClient.setFormat(FileContents[1]);
								}
							}
							break;
						case P2Pconstants.RFCQUERY:
							System.out.println("Received request at peer server for RFC Query.");
							if (indexList == null || indexList.isEmpty()) {
								replyToClient.setResponse(P2Pconstants.NOTOK);
								replyToClient.setState(P2Pconstants.SUCESS);
							} else {
								replyToClient.setResponse(P2Pconstants.OK);
								replyToClient.setState(P2Pconstants.SUCESS);
								replyToClient.setPeerList(indexList);
							}
							break;
						}
						toClient.writeObject(replyToClient);
						toClient.flush();
					}
				} catch (Exception e) {
					System.out.println("Exception Occured.\n");
				} finally {
					try {
						if (_socket != null) {
							System.out.println("Socket closed");
						_socket.close();}
					} catch (IOException e) {
						System.out.println("Exception Occured.\n");
					}
				}						
			}
		}

		/**
		 * Fetch file content.
		 *
		 * @param RFCIndex the rFC index
		 * @return the string
		 */
		private  String[] fetchFileContent(String RFCIndex) {
			if (RFCIndex == null) {
				return null;
			}
			String userPath =System.getProperty("user.home");
			String filePathString = null;
			String extension = null; 
			File folder = new File( userPath + "/RFC/");
	        File[] listOfFiles = folder.listFiles();
	        for (File file : listOfFiles)
	        {
	        if (file.isFile())
	        {
	            String[] filename = file.getName().split("\\.(?=[^\\.]+$)"); //split filename from it's extension
	            if(filename[0] != null && 
	            		filename[0].equalsIgnoreCase("rfc" + RFCIndex)){ //matching defined filename
	                System.out.println("File exist: "+filename[0]+"."+filename[1]); // match occures.Apply any condition what you need
	            filePathString = file.getPath();
	            extension = filename[1];
	            break;}
	        }
	        }
			FileReader reader;
			try {
				reader = new FileReader(filePathString);
			} catch (FileNotFoundException e) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			char buffer[] = new char[32468]; // read 16k blocks
			int len; // how much content was read?
			try {
				while ((len = reader.read(buffer)) > 0) {
					sb.append(buffer, 0, len);
				}
			} catch (IOException e) {
				return null;
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("Faled to close file reader");
				}
			}
			String file[] = new String[2];
			file[0]= sb.toString();
			file[1] = extension;
			return file;
		}

		/**
		 * Check if file exists.
		 *
		 * @param RFCIndex the rFC index
		 * @return true, if successful
		 */
		private boolean checkIfFileExists(String RFCIndex) {
			if (RFCIndex == null) {
				return false;
			}
			String userPath =System.getProperty("user.home");
			File folder = new File( userPath + "/RFC/");
	        File[] listOfFiles = folder.listFiles();
	        for (File file : listOfFiles)
	        {
	        if (file.isFile())
	        {
	            String[] filename = file.getName().split("\\.(?=[^\\.]+$)"); //split filename from it's extension
	            if(filename[0] != null && 
	            		filename[0].equalsIgnoreCase("rfc" + RFCIndex)){ //matching defined filename
	                System.out.println("File exist: "+filename[0]+"."+filename[1]); // match occures.Apply any condition what you need
	            return true;}
	        }
	        }
	        return false;
		}
	}
	
		
	/**
	 * The Class PeerServerTTLMaintainance.
	 */
	private class PeerServerTTLMaintainance extends Thread {
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			for (;;) {
				//System.out.println("updating TTL values for RFC\n");
				try {
				Thread.sleep(60000); 
				if (getIndexList() != null && !getIndexList().isEmpty()) {
				  synchronized (indexList) {
						Iterator<RFCIndex> peer  = getIndexList().iterator(); 
						  while (peer.hasNext()) {
								String localIp = Inet4Address.getLocalHost().getHostAddress();
									RFCIndex index = (RFCIndex) peer.next();
									if(index != null && !localIp.equals(index.getHostname())) {
										index.setTTL(index.getTTL() - 60);
										if(index.getTTL() <=0) {
											peer.remove();
										}
									}
							}
					}
					}
				} catch ( UnknownHostException e) {
					System.out.println("Failed maintainance service");
				} catch (InterruptedException e) {
					// TODO: handle exception
				}// Will not happen
			}
		}
	}
	
	/**
	 * The Class KeepAliveRequestGenerator.
	 */
	private static class KeepAliveRequestGenerator extends Thread {
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			RegistrationMessage requestMessageForLeave = new RegistrationMessage(P2Pconstants.KEEPALIVE, null,0,null,cookie);
			Socket socketForKeepAlive = null;
			for (;;) {
					try {
						Thread.sleep(180000); // Sleep for 3 minutes and send a keep alive
						//InetAddress serverHost = InetAddress.getByName("localhost"); 
						socketForKeepAlive = new Socket(REGISTRATION_SERVER_IP,P2Pconstants.RS_PORT); 
						System.out.println("Just connected to " + socketForKeepAlive.getRemoteSocketAddress() + "for sending keep alive"); 
						ObjectOutputStream toServer = new ObjectOutputStream(
								new BufferedOutputStream(socketForKeepAlive.getOutputStream()));
						toServer.writeObject(requestMessageForLeave);
						toServer.flush();
						System.out.println("Waiting from server to acknowledge RS KeepAlive");
						ObjectInputStream fromServer = new ObjectInputStream(
								new BufferedInputStream(socketForKeepAlive.getInputStream()));
						RegistrationMessage msgFromReplyForLeave = (RegistrationMessage)fromServer.readObject();
					    if(msgFromReplyForLeave.getState() == P2Pconstants.SUCESS) {
					    System.out.println("The peer's TTL is reset at RS server.");	
					    } else {
					    	System.out.println("The peer's TTL could not be reset");	
						}
					 } catch (Exception e) {
					} finally {
				    	try {
				    		if(socketForKeepAlive != null){
							socketForKeepAlive.close();}
						} catch (IOException e) {
						}
				    }
			}
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			jframe = new JFrame();
			textAreaForNotif = new JTextArea("RFC Application");
			textAreaForNotif.setFont(new Font("Dialog", Font.BOLD, 12)); 
			 JScrollPane scrollerForNotif = new JScrollPane(textAreaForNotif){
		         @Override
		         public Dimension getPreferredSize() {
		             return new Dimension(530, 420);
		         }
		     };
		    textAreaForNotif.getCaret().setVisible(false);
			jframe.add(scrollerForNotif);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setTitle("RFC Application");
			jframe.setSize(600,800);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//jframe.setLocationRelativeTo(null); 
			jframe.setVisible(true);

			REGISTRATION_SERVER_IP = P2Pconstants.RS_ADDRESS;
			RFC_PORT = P2Pconstants.PEER_DEFAULT_PORT;
			 if (args != null && args.length > 0) {
				REGISTRATION_SERVER_IP = args[0]; 
				RFC_PORT = Integer.parseInt(args[1]);
			}
			if (InetAddress.getLocalHost().getHostAddress().equals(REGISTRATION_SERVER_IP)) {
				REGISTRATION_SERVER_IP = "127.0.0.1";
			}
			setUpData();
			new PeerNode().runClient();
			System.out.println(indexList.size());
		} catch (Exception e) {
		}
	}
	
}
