package peer2peer.peer;

import java.io.Serializable;
import java.util.List;

import peer2peer.bean.RFCIndex;

/**
 * The Class PeerMessage.This is used for communication between RS Server and the Peer.
 *@author Bhargava Pejakala
 *@author Greeshma Gopinath 
 *
 */
public class PeerMessage implements Serializable  {
	
	 /** The Constant serialVersionUID. */
 	private static final long serialVersionUID = 1372010956044772621L;
	
	 /** The state. */
 	int state;
	 
 	/** The peer list. */
 	List<RFCIndex> peerList;
     
     /** The request. */
     String request;
     
     /** The response. */
     String response;
     
     /** The data. */
     String data;
     
     /** The format. */
     String format;
	
    /**
     * Gets the format.
     *
     * @return the format
     */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format.
	 *
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
     * Gets the state.
     *
     * @return the state
     */
    public  int getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public  void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Gets the peer list.
	 *
	 * @return the peer list
	 */
	public  List<RFCIndex> getPeerList() {
		return peerList;
	}
	
	/**
	 * Sets the peer list.
	 *
	 * @param peerList the new peer list
	 */
	public  void setPeerList(List<RFCIndex> peerList) {
		this.peerList = peerList;
	}
	
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public  String getRequest() {
		return request;
	}
	
	/**
	 * Instantiates a new peer message.
	 *
	 * @param state the state
	 * @param peerList the peer list
	 * @param request the request
	 * @param response the response
	 * @param data the data
	 */
	public PeerMessage(int state, List<RFCIndex> peerList, String request,
			String response, String data) {
		this.state = state;
		this.peerList = peerList;
		this.request = request;
		this.response = response;
		this.data = data;
	}
	
	/**
	 * Instantiates a new peer message.
	 */
	public PeerMessage() {
	}
	
	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public  void setRequest(String request) {
		this.request = request;
	}
	
	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public  String getResponse() {
		return response;
	}
	
	/**
	 * Sets the response.
	 *
	 * @param response the new response
	 */
	public  void setResponse(String response) {
		this.response = response;
	}
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public  String getData() {
		return data;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public  void setData(String data) {
		this.data = data;
	}
}
