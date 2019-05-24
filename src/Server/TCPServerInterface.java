package Server;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TCPServerInterface extends Remote {
	
	/*
	 * gets a list of files in the files directory as a string
	 */
	ArrayList<String> getFilesList() throws RemoteException;
	
	/*
	 * gets the number of files on a given file server as an int
	 */
	int getNumberOfFiles() throws RemoteException;
	
	/*
	 * receive an uploaded file from the client and write to a new file
	 */
	void receiveFile(String filename, File file) throws RemoteException;
	
	/*
	 * receive a filename from the client and delete it if the file exists
	 * returns true if delete successful, false otherwise
	 */
	boolean deleteFile(String filename) throws RemoteException;
	
	/*
	 * receive filename and return whether file exists on server or not
	 */
	boolean hasFile(String filename) throws RemoteException;
	
	/*
	 * receive a filename and return the size of that file if it exists, or 0 otherwise
	 */
	int getFileSize(String filename) throws RemoteException;
	
	/*
	 * send a requested file back to the client
	 */
	File sendFile(String filename) throws RemoteException;
	
	/*
	 * Function to quit the server - does nothing as quitting is done on client side
	 */
	void quit() throws RemoteException;
	
	/*
	 * Return a string saying that server is ready
	 */
	String readyString() throws RemoteException;
	
}
