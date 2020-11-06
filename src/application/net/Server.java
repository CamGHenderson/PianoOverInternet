package application.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import application.net.packet.AudioPacket;

public class Server {
	private static int port = 1040;
	private ServerSocket serverSocket;
	private Socket socket;
	private boolean running = false;
	private ArrayList<DataOutputStream> clients;
	
	public Server() {
		try {
			serverSocket = new ServerSocket(port);
			clients = new ArrayList<DataOutputStream>();
			new Thread(() -> run()).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void run() {
		System.out.println("Server Open");
		running = true;
		while(running) {
			try {
				socket = serverSocket.accept();
				new Thread(() -> handleClient(socket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private void handleClient(Socket socket) {
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client Joined");
		clients.add(out);
		while(running) {
			try {
				byte type = in.readByte();
				if(type == AudioPacket.getPacketID()) {
					byte command = in.readByte();
					byte channel = in.readByte();
					int data1 = in.readInt();
					int data2 = in.readInt();
					int instrument = in.readInt();
					for(DataOutputStream client : clients)
						if(client != out) {
							client.writeByte(AudioPacket.getPacketID());
							client.writeByte(command);
							client.writeByte(channel);
							client.writeInt(data1);
							client.writeInt(data2);
							client.writeInt(instrument);
						}
				}
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			clients.remove(out);
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Client Left");
	}
	
	public static int getPort() {
		return port;
	}
	
	public static void main(String[] args) {
		new Server();
	}
}
