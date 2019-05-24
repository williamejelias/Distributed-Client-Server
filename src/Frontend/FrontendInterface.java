package Frontend;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontendInterface extends Remote {
	
	/*
	 * looks up the servers in turn and them to the list
	 */
	void getActiveServerList() throws RemoteException;
		
	/*
	 * quit - only returns a string as quitting is done on the client side - the front end will still remain on the registry until it is terminated
	 */
	String quit() throws RemoteException;

	/*
	 * takes a filename as a string
	 * returns the file with that name stored in server file stores
	 */
	File sendFile(String filename) throws RemoteException;

	/*
	 * delete all instances of the input file from each servers local file store
	 * return true if successful, false if failed
	 */
	boolean deleteFile(String filename) throws RemoteException;

	/*
	 * traverses all server files directories and builds a string containing all filenames without duplicates
	 * returns a string containing all files managed by servers on the front end
	 */
	String getFilesList() throws RemoteException;

	/*
	 * traverses all server file directories and uploads a file
	 * if boolean secure is true, upload to all, otherwise upload to server with fewest files
	 */
	void uploadFile(String filename, File file, boolean secure) throws RemoteException;
	
	/*
	 * return true if given filename exists as a file on any of the servers otherwise return false
	 */
	boolean getIfFileExists(String filename) throws RemoteException;

	/*
	 * get size of a file with given filename on servers
	 */
	int getFileSize(String filename) throws RemoteException;
}
