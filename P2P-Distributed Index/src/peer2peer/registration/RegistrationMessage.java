package peer2peer.registration;

import java.io.Serializable;
import java.util.List;

import peer2peer.bean.*;

/**
 * The Class RegistrationMessage.This is used for all the communication between registration Server and the
 * Peer nodes.
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 *
 */
public class RegistrationMessage implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3033136116273436757L;
	 
 	/** The state. */
 	int state;
	 
 	/** The host name. */
 	String hostName;
	 
 	/** The RFC serverport. */
 	int RFCServerport;
	 
 	/** The cookie. */
 	int cookie;
	 
 	/** The peer list. */
 	List<Peer> peerList;

	/**
	 * Instantiates a new registration message.
	 *
	 * @param state the state
	 * @param hostName the host name
	 * @param RFCServerport the rFC serverport
	 * @param peerList the peer list
	 * @param cookieValue the cookie value
	 */
	public RegistrationMessage(int state, String hostName,
			int RFCServerport, List<Peer> peerList, int cookieValue) {
		this.state = state;
		this.hostName = hostName;
		this.RFCServerport = RFCServerport;
		this.peerList = peerList;
		this.cookie = cookieValue;
	}

	/**
	 * Gets the peer list.
	 *
	 * @return the peer list
	 */
	public  List<Peer> getPeerList() {
		return peerList;
	}

	/**
	 * Sets the peer list.
	 *
	 * @param peerList the new peer list
	 */
	public  void setPeerList(List<Peer> peerList) {
		this.peerList = peerList;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the host name.
	 *
	 * @param hostName the new host name
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Gets the rFC serverport.
	 *
	 * @return the rFC serverport
	 */
	public int getRFCServerport() {
		return RFCServerport;
	}

	/**
	 * Sets the rFC serverport.
	 *
	 * @param RFCServerport the new rFC serverport
	 */
	public void setRFCServerport(int RFCServerport) {
		this.RFCServerport = RFCServerport;
	}
	
	/**
	 * Gets the cookie.
	 *
	 * @return the cookie
	 */
	public int getCookie() {
		return cookie;
	}

	/**
	 * Sets the cookie.
	 *
	 * @param cookie the new cookie
	 */
	public void setCookie(int cookie) {
		this.cookie = cookie;
	}
}
