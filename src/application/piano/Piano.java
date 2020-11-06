package application.piano;

import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import application.net.Client;
import application.net.packet.AudioPacket;
import application.net.piano.Command;

public class Piano {
	private Synthesizer synthesizer;
	private MidiChannel[] channels;
	private Instrument[] instruments;
	private int instrument;
	private boolean mute = false;
	private int volume = 100;
	private Client client;
	
	public Piano(Client client) {
		this.client = client;
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for(MidiDevice.Info info : infos)
			try {
				device = MidiSystem.getMidiDevice(info);
				List<Transmitter> transmitters = device.getTransmitters();
				for(Transmitter transmitter : transmitters)
					transmitter.setReceiver(new MidiReceiver(client));
				Transmitter transmitter = device.getTransmitter();
				transmitter.setReceiver(new MidiReceiver(client));
				device.open();
				System.out.println(device.getDeviceInfo().toString() + " was opened.");
			} catch (MidiUnavailableException e) {}
		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		channels = synthesizer.getChannels();
		instruments = synthesizer.getDefaultSoundbank().getInstruments();
		instrument = instruments[0].getPatch().getProgram();
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public void toggleMute() {
		mute = !mute;
	}
	
	public void setInstrument(int instrument) {
		channels[0].programChange(instruments[instrument].getPatch().getProgram());
		this.instrument = instrument;
	}
	
	public Instrument[] getInstruments(){
		return instruments;
	}
	
	public Client getClient() {
		return client;
	}
	
	private class MidiReceiver implements Receiver{	
		private Client client;
		
		public MidiReceiver(Client client) {
			this.client = client;
		}
		
		@Override
		public void send(MidiMessage message, long timeStamp) {
			 if (message instanceof ShortMessage) {
                 ShortMessage shortMessage = (ShortMessage)message;
                 if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                	 if(!mute)
                		 channels[shortMessage.getChannel()].noteOn(shortMessage.getData1(), (int)(shortMessage.getData2() * (float)volume/(float)100));
                	 client.sendAudio(new AudioPacket(Command.NOTE_ON, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2(), instrument));                
                 }else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                	 if(!mute)
                		 channels[shortMessage.getChannel()].noteOff(shortMessage.getData1(), shortMessage.getData2());
                	 client.sendAudio(new AudioPacket(Command.NOTE_OFF, shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2(), instrument));
                 }
			 }
		}

		@Override
		public void close() {
			synthesizer.close();
		}
	}
}
