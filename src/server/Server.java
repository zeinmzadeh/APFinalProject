package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import base.User;
import fileHandling.ReadFile;

public class Server extends Thread {
	private int port ;
	private ServerSocket serverSocket = null;
	private boolean serverRunning = false;
	private HashMap<String, User> users = new HashMap<String, User>();
	private ArrayList<Client> clients = new ArrayList<>();

	//---------------------------constructor-------------------------------------//
	public Server(int port) {
		this.port = port;
		setUsers();
	}
	//---------------------------------------------------------------------------//
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			serverRunning = true;

			while (serverRunning) {
				System.out.println("waiting for connection...");
				Socket socket = serverSocket.accept();
				
				System.out.println("connected : " + socket.toString());
				
				Client client = new Client(this, socket);
				clients.add(client);
				client.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//------------------getters And setters-----------------//
	//this is used for serverSide only
	public HashMap<String, User> getUsers() {
		return users;
	}
	// this method sets all users from file to server
	private void setUsers() {
		users = ReadFile.UsersFile.readUsers();
	}
	//this method give online users stored in clients.
	public ArrayList<Client> getClients(){
		return clients;
	}
	// this method gives user access to all other usernames.
	public static Object[] getUsersProperties(ObjectOutputStream objOut, ObjectInputStream objIn) throws IOException, ClassNotFoundException {
		//initializing and sending command...
		String[] command = new String[1];
		command[0] = "getusers";
		objOut.writeObject(command);
		objOut.flush();
		
		//getting the server answer
		Object obj = objIn.readObject();
		Object[] users = (Object[]) obj;
		
		return users;
	}
}
