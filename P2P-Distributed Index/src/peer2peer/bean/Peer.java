package peer2peer.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * The Class Peer.This class represents the each peer that participates in the P2P Application.
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 *
 */
public class Peer implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 771102334840547518L;
	
	/** The host name. */
	String hostName;
	
	/** The cookie. */
	int cookie;
	
	/** The active. */
	boolean active;
	
	/** The ttl. */
	int TTL;
	
	/** The port number. */
	int portNumber;
	
	/** The last registered date. */
	Date lastRegisteredDate;

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

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the ttl.
	 *
	 * @return the ttl
	 */
	public int getTTL() {
		return TTL;
	}

	/**
	 * Sets the ttl.
	 *
	 * @param tTL the new ttl
	 */
	public void setTTL(int tTL) {
		TTL = tTL;
	}

	/**
	 * Gets the port number.
	 *
	 * @return the port number
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * Sets the port number.
	 *
	 * @param portNumber the new port number
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * Gets the last registered date.
	 *
	 * @return the last registered date
	 */
	public Date getLastRegisteredDate() {
		return lastRegisteredDate;
	}

	/**
	 * Instantiates a new peer.
	 *
	 * @param hostName the host name
	 * @param cookie the cookie
	 * @param active the active
	 * @param tTL the t tl
	 * @param RFCServerPort the rFC server port
	 * @param time the time
	 */
	public Peer(String hostName, int cookie, boolean active, int tTL,
			int RFCServerPort, Date time) {
		this.hostName = hostName;
		this.cookie = cookie;
		this.active = active;
		TTL = tTL;
		this.portNumber = RFCServerPort;
		this.lastRegisteredDate = time;
	}
	
	/**
	 * Instantiates a new peer.
	 *
	 * @param hostName the host name
	 * @param RFCServerPort the rFC server port
	 */
	public Peer(String hostName, int RFCServerPort) {
		this.hostName = hostName;
		this.portNumber = RFCServerPort;
	}
	
	/**
	 * Instantiates a new peer.
	 *
	 * @param cookieValue the cookie value
	 */
	public Peer(int cookieValue) {
	this.cookie = cookieValue;
	}

	/**
	 * Sets the last registered date.
	 *
	 * @param lastRegisteredDate the new last registered date
	 */
	public void setLastRegisteredDate(Date lastRegisteredDate) {
		this.lastRegisteredDate = lastRegisteredDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cookie;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Peer other = (Peer) obj;
		if (cookie != other.cookie)
			return false;
		return true;
	}

}