package Server;

import java.io.*;
import java.nio.channels.FileChannel;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class TCPServer extends UnicastRemoteObject implements TCPServerInterface{
	
	private static final long serialVersionUID = 4219484256833795322L;
	private String serverName;
	
	public TCPServer(String name) throws RemoteException{
		this.serverName = name;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Creating " + args[0] + " skeleton object...");

			// create skeleton object
			TCPServer serverSkeleton = new TCPServer(args[0]);
			
			System.out.println("Locating the registry...");

			// locate the registry
            Registry registry = LocateRegistry.getRegistry();
            
			System.out.println("Binding the skeleton object to the registry...");

            // bind the remote object to the registry
            registry.bind(args[0], serverSkeleton);
            
			System.out.println(args[0] + " skeleton object running...");
			
		} catch (RemoteException e) {
			// should not happen
			System.out.println(e.getMessage());
			System.out.println("Remote exception occurred...");
			System.out.println("Server Failure!");

		} catch (AlreadyBoundException e) {
			// object already bound
			System.out.println("Remote Object with that name is already bound");
		}
	}
	
	/*
	 * gets a list of files in the files directory as a string
	 */
	public ArrayList<String> getFilesList()  throws RemoteException {
		File folder = new File("./Server/files/" + serverName);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> list = new ArrayList<String>();
		if (listOfFiles != null) {
			for (File listOfFile : listOfFiles) {
				list.add(listOfFile.getName());
			}
		}
		return list;
	}
	
	/*
	 * gets the number of files on a given file server as an int
	 */
	public int getNumberOfFiles()  throws RemoteException {
		File folder = new File("./Server/files/" + serverName);
		File[] listOfFiles = folder.listFiles();
		assert listOfFiles != null;
		return listOfFiles.length;
	}
	
	/*
	 * receive an uploaded file from the client and write to a new file
	 */
	public void receiveFile(String filename, File file) throws RemoteException  {
		System.out.println(serverName + " receiving file...");
		System.out.println("Filename received: " + filename);
		
		int file_size = (int)file.length();
		System.out.println("Size of file to be received: " + file_size + " bytes");
			
		try {
			// receive file
			System.out.println("Receiving file...");
			@SuppressWarnings("resource")
			FileChannel download = new FileInputStream(file).getChannel();
			
			// open new file
			System.out.println("Creating new file...");
			File newFile = new File("./Server/files/" + serverName + "/" + filename);
			@SuppressWarnings("resource")
			FileChannel destination = new FileOutputStream(newFile).getChannel();
			
			// channel download content into new file
			System.out.println("Writing uploaded file to new file...");
			destination.transferFrom(download, 0, download.size());

			System.out.println("Upload complete.\n");
			
			// receive the file from the client
			// display upload stats
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find write-to file...\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error writing to new file...\n");
			e.printStackTrace();
		}
	}
	
	/*
	 * receive filename and return whether file exists on server or not
	 */
	public boolean hasFile(String filename) throws RemoteException {
		File file = new File("./Server/files/" + serverName + "/" + filename);
		return file.exists();
	}
	
	/*
	 * receive a filename and return the size of that file if it exists, or 0 otherwise
	 */
	public int getFileSize(String filename) {
		try {
			if (hasFile(filename)) {
				File file = new File("./Server/files/" + serverName + "/" + filename);
				return (int)file.length();
			}
		} catch (RemoteException e) {
			// do nothing
		}
		return 0;
	}

	/*
	 * receive a filename from the client and delete it if the file exists
	 * returns true if delete successful, false otherwise
	 */
	public boolean deleteFile(String filename) throws RemoteException {
		System.out.println(serverName + " deleting file...");
		System.out.println("Filename received: " + filename);
		
		File fileToDelete = new File("./Server/files/" + serverName + "/" + filename);
		if (fileToDelete.delete()){
				System.out.println(serverName + " successfully deleted " + fileToDelete.getName() +  "!\n");
				return true;
		} else {
			System.out.println(serverName + " delete operation failed...\n");
			return false;
		}	
	}
	
	/*
	 * send a requested file back to the client
	 */
	public File sendFile(String filename) throws RemoteException {
		System.out.println(serverName + " sending file...");
		System.out.println("Filename received: " + filename);
		
		File sendFile = new File("./Server/files/" + serverName + "/" + filename);
		System.out.println("File sent.\n");
		return sendFile;
	}
	
	/*
	 * Function to quit the server - does nothing as quitting is done on client side
	 */
	public void quit() throws RemoteException { 
		
	}
	
	/*
	 * return a readystring for server object
	 */
	public String readyString() throws RemoteException {
		return "" + serverName + " ready.";
	}
}