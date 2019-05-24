package Client;

import Frontend.FrontendInterface;

import java.io.*;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class TCPClient implements TCPClientInterface {
	
	private FrontendInterface stub;
	
	private Scanner selectScanner;
	private Scanner uploadScanner;
	private Scanner deleteScanner;
	
	public TCPClient() {
		
	}
	
	public static void main(String[] args) {

        try {
            // call all methods on stub
            // execute client handling
            TCPClient client = new TCPClient();
            Scanner select = new Scanner(System.in);
	    		System.out.println("Welcome to the Client Application.");
	    		System.out.println("Connect to the server to list, upload, download and delete files.");
	    		client.printCommands();
	    		
	    		menu:
	    		while (true) {
	    			switch (select.next()) {
		    			case "CONN": 
		        				// CONN SELECTED 
		    				client.connect();
		    				select.close();
		    				break menu;
		    			case "QUIT":
		    				// QUIT SELECTED
							System.out.println("Closing Client Application...");
							select.close();
		    				break menu;
		    			default:
		    				System.out.println("Unrecognized command!");
		    				client.printCommands();
	    			}
	    		}      
        } catch (Exception e) {
			e.printStackTrace();
		} 
	}	
	
	/*
	 * Function to lookup registry for frontend remote object and save the stub as an object
	 */
	@Override
	public void connect() {
		try {
			System.out.println("Connecting to server...");
			System.out.println("Locating registry...");			
			Registry registry = LocateRegistry.getRegistry();
			System.out.println("Binding...");
	        stub = (FrontendInterface) registry.lookup("Frontend");
	        System.out.println("Successfully connected to server!");
	        System.out.println();
	        
	        selectScanner = new Scanner(System.in);
			connMenu:
			while (true) {
				printConnCommands();
				switch(selectScanner.next().trim()) {
				case "UPLD":
					uploadFileToFrontend();
					break;
				case "LIST":
					listFiles();
					break;
				case "DWLD":
					downloadFile();
					break;
				case "DELF":
					deleteFile();
					break;
				case "QUIT":
					quit();
					selectScanner.close();
					break connMenu;
				default: 
					System.out.println("\nCommand not recognised.\n");
				}
			} 
	        
		} catch (Exception e) {
            System.err.println("Client exception connecting to registry: " + e.toString());
            e.printStackTrace();
        }
	}
	
	/*
	 * Upload a file to the server
	 */
	public void uploadFileToFrontend() {
		try {
			System.out.println("Enter name of file to be uploaded: ");
			
			// get filename of file to be uploaded
			uploadScanner = new Scanner(System.in);
			String filename = uploadScanner.next().trim();
			

			// check that the file exists - if it does send the encoded filname and length
			File uploadFile = new File("./Client/files/" + filename);
			if (uploadFile.exists() && !uploadFile.isDirectory()) { 
				
				int filesize = (int)uploadFile.length();
				System.out.println("Filesize of upload: " + filesize);
				
				
				// secure/ insecure upload
				boolean secure;
				System.out.println("Do you want to upload the file with high reliability? Yes/No ");
				secureInsecure:
				while (true) {
					switch(uploadScanner.next().trim()) {
					case "Yes":
					case "yes":
					case "Y":
					case "y":
						secure = true;
						System.out.println("Transferring " + filesize + " bytes with high reliability.");
						break secureInsecure;
					case "No":
					case "no":
					case "N":
					case "n":
						secure = false;
						System.out.println("Transferring " + filesize + " bytes.");
						break secureInsecure;
					default: 
						System.out.println("\nCommand not recognised.\n");
					}
				} 

				// transfer 
				long startTime = System.nanoTime();
				
			    // file exists and isn't a directory, so upload
				stub.uploadFile(filename, uploadFile, secure);
				
				long endTime = System.nanoTime();
				long duration = (endTime - startTime) / 1000000;
				System.out.println("Transferred " + filesize + " bytes in " + duration + " ms.");
				
				System.out.println("Upload complete.\n");
				
			} else {
				// file does not exist, so back to listening state
				System.out.println("File does not exist...\n");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Delete selected file from the server
	 */
	public void deleteFile() {
		try {
			System.out.println("Enter name of file to be deleted: ");
			
			// get filename of file to be uploaded
			deleteScanner = new Scanner(System.in);
			String filename = deleteScanner.next().trim();
			
			// get if file exists on any server on the frontend
			boolean response = stub.getIfFileExists(filename);
			
			if (response) {
				// file exists
				yesOrNo:
				while (true) {
					System.out.println("Confirm delete - Yes/No");
					switch (deleteScanner.next().trim()) {
					case "Yes":
					case "yes":
					case "y":
					case "Y":
						
						// confirm to server and get response
						boolean successFail = stub.deleteFile(filename);
						
						if (successFail) {
							// delete success
							System.out.println("File successfully deleted from server!\n");
						} else {
							System.out.println("Server error - delete operation failed...\n");
						}
						break yesOrNo;
					case "No":
					case "no":
					case "n":
					case "N":
						
						// delete abandoned
						System.out.println("Delete abandoned by the user!\n");						
						break yesOrNo;
					default:
						System.out.println("Unrecognized command!\n");
					}
				}
			} else {
				// file does not exist
				System.out.println("The file does not exist on the server...\n");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * List all files on the server
	 */
	public void listFiles() {
		try {
			System.out.println("Receiving files list...");
			String filesList = stub.getFilesList();
			System.out.println(filesList + "\n");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Download the selected file from the server
	 */
	public void downloadFile() {
		try {
			System.out.println("Enter name of file to be downloaded: ");
			
			// get filename of file to be downloaded
			uploadScanner = new Scanner(System.in);
			String filename = uploadScanner.next().trim();
			
			// get if file exists on any server on the frontend
			boolean response = stub.getIfFileExists(filename);
			
			if (response) {
				// file exists
				int filesize = stub.getFileSize(filename);
				System.out.println("File exists with size: " + filesize + " bytes.");
				
				try {
					long startTime = System.nanoTime();

					// receive file from sendFile stub method
					@SuppressWarnings("resource")
					FileChannel download = new FileInputStream(stub.sendFile(filename)).getChannel();
					
					// open new file
					File newFile = new File("./Client/files/" + filename);
					@SuppressWarnings("resource")
					FileChannel destination = new FileOutputStream(newFile).getChannel();
					
					// channel download content into new file
					destination.transferFrom(download, 0, download.size());
					
					long endTime = System.nanoTime();
					long duration = (endTime - startTime) / 1000000;
					System.out.println("Recieved " + filesize + " bytes in " + duration + " ms.");
					
				    System.out.println("Download complete.\n");
					
				} catch (FileNotFoundException e) {
					System.out.println("Could not create fileChannel from received file...\n");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Error writing file to new file...\n");
					e.printStackTrace();
				}
				
			} else {
				// file does not exist
				System.out.println("The file does not exist on the server...\n");
			}
						
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Function to quit the client and terminate the connection to the server
	 */
	public void quit() {
		try {
			String response = stub.quit();
			System.out.println(response + "\n");
			System.out.println("Closing connection...");
			System.out.println("Connection Terminated\n");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * list commands available at client initialization, connect/quit
	 */
	public void printCommands() {
		System.out.println("Commands: ");
		System.out.println("CONN");
		System.out.println("QUIT\n");
	}
	
	/*
	 * list command available after connection, upload, delete, download, list, quit
	 */
	public void printConnCommands() {
		System.out.println("Commands: ");
		System.out.println("UPLD");
		System.out.println("LIST");
		System.out.println("DWLD");
		System.out.println("DELF");
		System.out.println("QUIT\n");
	}

}