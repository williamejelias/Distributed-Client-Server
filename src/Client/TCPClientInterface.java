package Client;

public interface TCPClientInterface {
	
	/*
	 * Function to make connection to server localhost at port 5000
	 */
	void connect();
	
	/*
	 * Upload a file to the server
	 */
	void uploadFileToFrontend();
	
	/*
	 * Delete selected file from the server
	 */
	void deleteFile();
	
	/*
	 * List all files on the server
	 */
	void listFiles();
	
	/*
	 * Download the selected file from the server
	 */
	void downloadFile();
	
	/*
	 * Function to quit the client and terminate the connection to the server
	 */
	void quit();

	/*
	 * list commands available at client initialization, connect/quit
	 */
	void printCommands();
		
	/*
	 * list command available after connection, upload, delete, download, list, quit
	 */
	void printConnCommands();
}
