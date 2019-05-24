package Frontend;

import Server.TCPServerInterface;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Frontend extends UnicastRemoteObject implements FrontendInterface {

	private static final long serialVersionUID = 1105251041217508921L;
	
	private TCPServerInterface server1;
	private TCPServerInterface server2;
	private TCPServerInterface server3;
	
	private ArrayList<TCPServerInterface> servers;

	public Frontend() throws RemoteException {
		getActiveServerList();
        System.err.println("Servers running...");
	}

	public static void main(String[] args) {
        
        try {
            Frontend skeleton = new Frontend();

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Frontend", skeleton);
            
            // servers are ready
            skeleton.printReady();

            System.err.println("Front end running...\n");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        
    }
	
	public void getActiveServerList() {
		try {
            // Locate the the registry
            Registry registry = LocateRegistry.getRegistry();
            
            // add servers to arrayList in order that they can be iterated through
            servers = new ArrayList<>();
            
            try {
	            // lookup and create stubs for each individual server
		        server1 = (TCPServerInterface) registry.lookup("server1");
	            servers.add(server1);
            } catch (Exception e) {
            		// server wasn't bound and so does not exist.
            }
            try {
	            // lookup and create stubs for each individual server
		        server2 = (TCPServerInterface) registry.lookup("server2");
	            servers.add(server2);
            } catch (Exception e) {
            		// server wasn't bound and so does not exist.
            }
            try {
	            // lookup and create stubs for each individual server
		        server3 = (TCPServerInterface) registry.lookup("server3");
	            servers.add(server3);
            } catch (Exception e) {
            		// server wasn't bound and so does not exist.
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
	}
	
	/*
	 * quit - only returns a string as quitting is done on the client side - the front end will still remain on the registry until it is terminated
	 */
	public String quit() throws RemoteException {
		return "Quit file remote method invocation";
	}
	
	/*
	 * takes a filename as a string
	 * returns the file with that name stored in server file stores
	 */
	public File sendFile(String filename) throws RemoteException {
		getActiveServerList();
		for (TCPServerInterface server : servers) {
			if (server.hasFile(filename)) {
				return server.sendFile(filename);
			}
		}
		return null;
	}

	/*
	 * delete all instances of the input file from each servers local file store
	 * return true if successful, false if failed
	 */
	public boolean deleteFile(String filename) throws RemoteException {
		getActiveServerList();
		boolean completeDelete = true;
		for (TCPServerInterface server : servers) {
			if (server.hasFile(filename) && !server.deleteFile(filename)) {
				completeDelete = false;
			}
		}
		return completeDelete;
	}
	
	/*
	 * get size of a file with given filename on servers
	 */
	public int getFileSize(String filename) throws RemoteException {
		getActiveServerList();
		int size = servers.get(0).getFileSize(filename);
		for (int i = 1; i < servers.size(); i ++) {
			if (servers.get(i).getFileSize(filename) > 0) {
				size = servers.get(i).getFileSize(filename);
				return size;
			}
		}
		return size;
	}

	/*
	 * traverses all server files directories and builds a string containing all filenames without duplicates
	 * returns a string containing all files managed by servers on the front end
	 */
	public String getFilesList() throws RemoteException {
		getActiveServerList();
		
		ArrayList<String> files = new ArrayList<>();
		for (TCPServerInterface server : servers) {
			files.addAll(server.getFilesList());
		}

		// add elements to al, including duplicates
		Set<String> hs = new HashSet<>(files);

		ArrayList<String> filesList = new ArrayList<>(hs);
		
		StringBuilder list = new StringBuilder();
		list.append("Files found: ").append(filesList.size()).append("\n");
		for (String s : filesList) {
			list.append(s).append("\n");
		}
		return list.toString();
	}
	
	/*
	 * traverses all server file directories and uploads a file
	 * if boolean secure is true, upload to all, otherwise upload to server with fewest files
	 */
	public void uploadFile(String filename, File file, boolean secure) throws RemoteException {
		getActiveServerList();
		// secure - upload to all file servers
		if (secure) {
			for (TCPServerInterface server : servers) {
				server.receiveFile(filename, file);
			}
		} else {
			// find server with fewest files on it
			int smallest = server1.getNumberOfFiles();
			int serverNum = 1;
			for (int i = 0; i < servers.size(); i ++) {
				if (servers.get(i).getNumberOfFiles() < smallest) {
					smallest = servers.get(i).getNumberOfFiles();
					serverNum = i + 1;

				}
			}
			// upload to server
			servers.get(serverNum-1).receiveFile(filename, file);
		}
	}
	
	/*
	 * return true if given filename exists as a file on any of the servers otherwise return false
	 */
	public boolean getIfFileExists(String filename) {
		getActiveServerList();
		for (TCPServerInterface server : servers) {
			try {
				if (server.hasFile(filename)) {
					return true;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/*
	 * print that servers are ready
	 */
	public void printReady() {
		try {
			for (TCPServerInterface server : servers) {
				System.out.println(server.readyString());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
