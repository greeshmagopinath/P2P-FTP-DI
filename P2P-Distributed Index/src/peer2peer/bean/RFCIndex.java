package peer2peer.bean;

import java.io.Serializable;

/**
 * The Class RFCIndex.This class reperesnts the enitiy that is used to hold RFC index values.Since this class
 * is used for object streaming this implements Serializable interface
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 */
public class RFCIndex implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6063802473250510490L;

	/** The RFC number. */
	int RFCNumber;
	
	/** The RFC title. */
	String RFCTitle;
	
	/** The hostname. */
	String hostname;
	
	/** The ttl. */
	int TTL = 7200;

	/**
	 * Gets the rFC number.
	 *
	 * @return the rFC number
	 */
	public int getRFCNumber() {
		return RFCNumber;
	}

	/**
	 * Sets the rFC number.
	 *
	 * @param rFCNumber the new rFC number
	 */
	public void setRFCNumber(int rFCNumber) {
		RFCNumber = rFCNumber;
	}

	/**
	 * Gets the rFC title.
	 *
	 * @return the rFC title
	 */
	public String getRFCTitle() {
		return RFCTitle;
	}

	/**
	 * Sets the rFC title.
	 *
	 * @param rFCTitle the new rFC title
	 */
	public void setRFCTitle(String rFCTitle) {
		RFCTitle = rFCTitle;
	}

	/**
	 * Gets the hostname.
	 *
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets the hostname.
	 *
	 * @param hostname the new hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + RFCNumber;
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
		RFCIndex other = (RFCIndex) obj;
		if (RFCNumber != other.RFCNumber)
			return false;
		return true;
	}

	/**
	 * Instantiates a new rFC index.
	 *
	 * @param rFCNumber the rfc number
	 * @param rFCTitle the rfc title
	 * @param hostname the hostname
	 * @param tTL the ttl
	 */
	public RFCIndex(int rFCNumber, String rFCTitle, String hostname, int tTL) {
		RFCNumber = rFCNumber;
		RFCTitle = rFCTitle;
		this.hostname = hostname;
		TTL = tTL;
	}
	
	/**
	 * Instantiates a new rFC index.
	 */
	public RFCIndex(){
		//Default Constructor
	}
}
