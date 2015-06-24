package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	// Variables to communicate with the server
	private Socket socket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
	public static Client connectClient(String host, int port)
	{
		Client connectedClient = null;
		
		try
		{
			connectedClient = new Client(host, port);
		}
		catch (IOException e)
		{
			System.err.println("Error while connecting a client to " + host + " (port: " + port + ")");
			System.err.println(e.getMessage());
		}
		
		return connectedClient;
	}
	
	private Client(String host, int port) throws IOException
	{
		this.socket = new Socket(host, port);
		this.input = new BufferedInputStream(this.socket.getInputStream());
		this.output = new BufferedOutputStream(this.socket.getOutputStream());
	}
	
	public char[] readInputs() throws IOException
	{
		byte[] inputs = new byte[] {};
		
		if(this.input.available() > 0)
		{
			inputs = new byte[this.input.available()];
			this.input.read(inputs);
		}
		
		char[] inputsChar = new char[inputs.length];
		
		for(int index = 0; index < inputs.length; index++)
		{
			inputsChar[index] = (char) inputs[index];
		}
		
		return inputsChar;
	}
	
	public void sendOutputs(byte[] outputs) throws IOException
	{
		this.output.write(outputs);
		this.output.flush();
	}
}
