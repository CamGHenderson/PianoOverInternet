package application.net.piano;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import application.net.packet.AudioPacket;

public class NetPiano {
	private Synthesizer synthesizer;
	private MidiChannel[] channels;
	private int volume = 100;
	
	public NetPiano() {
		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		channels = synthesizer.getChannels();
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public void updateAudio(AudioPacket packet) {
		if(channels[packet.getChannel()].getProgram() != packet.getInstrument())
			channels[packet.getChannel()].programChange(synthesizer.getDefaultSoundbank().getInstruments()[packet.getInstrument()].getPatch().getProgram());
		
		if (packet.getCommand() == Command.NOTE_ON) 
			channels[packet.getChannel()].noteOn(packet.getData1(), (int)(packet.getData2() * (float)volume/(float)100));
        else if (packet.getCommand() == Command.NOTE_OFF)
        	channels[packet.getChannel()].noteOff(packet.getData1(), packet.getData2());
	}
	
	public void close() {
		synthesizer.close();
	}
}
