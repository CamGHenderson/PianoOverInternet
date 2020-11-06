package application.net.packet;

import java.io.DataOutputStream;
import java.io.IOException;

import application.net.Packet;
import application.net.piano.Command;

public class AudioPacket implements Packet{
	private Command command;
	private int channel;
	private int data1;
	private int data2;
	private int instrument;
	
	public AudioPacket(Command command, int channel, int data1, int data2, int instrument) {
		this.command = command;
		this.channel = channel;
		this.data1 = data1;
		this.data2 = data2;
		this.instrument = instrument;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public int getData1() {
		return data1;
	}
	
	public int getData2() {
		return data2;
	}
	
	public int getInstrument() {
		return instrument;
	}

	public static int getPacketID() {
		return 0;
	}

	@Override
	public void send(DataOutputStream out) {
		try {
			out.writeByte(getPacketID());
			out.writeByte((command == Command.NOTE_ON) ? 0 : 1);
			out.writeByte(channel);
			out.writeInt(data1);
			out.writeInt(data2);
			out.writeInt(instrument);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
