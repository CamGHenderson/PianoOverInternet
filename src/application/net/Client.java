package application.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import application.net.packet.AudioPacket;
import application.net.piano.Command;
import application.net.piano.NetPiano;

public class Client {
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private NetPiano piano;
	private boolean running = false;
	
	public Client(String address) {
		piano = new NetPiano();
		try {
			socket = new Socket(address, Server.getPort());
			System.out.println("Connected");
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			new Thread(() -> listen()).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendAudio(AudioPacket packet) {
		packet.send(out);
	}
	
	public NetPiano getPiano() {
		return piano;
	}
	
	private void listen() {
		running = true;
		while(running) {
			try {
				int type = in.readByte();
				if(type == AudioPacket.getPacketID()) {
					byte command = in.readByte();
					byte channel = in.readByte();
					int data1 = in.readInt();
					int data2 = in.readInt();
					int instrument = in.readInt();
					piano.updateAudio(new AudioPacket((command == 0) ? Command.NOTE_ON : Command.NOTE_OFF, channel, data1, data2, instrument));
				}
			} catch (SocketException e) {
				e.printStackTrace();
				running = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
