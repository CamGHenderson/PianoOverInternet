package application.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import application.piano.Piano;

public class Window {
	public Window(Piano piano) {
		JFrame window = new JFrame();
		window.setTitle("Piano over Internet");
		window.setSize(600, 300);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setLayout(null);
		
		String[] instrumentNames = new String[piano.getInstruments().length];
		for(int i = 0; i < instrumentNames.length; i++)
			instrumentNames[i] = piano.getInstruments()[i].getName();
		
		JComboBox<String> instrument = new JComboBox<String>(instrumentNames);
		instrument.setSelectedIndex(0);
		instrument.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				piano.setInstrument(instrument.getSelectedIndex());
			}
		});
		instrument.setBounds(10, 10, 100, 20);
		window.add(instrument);
		
		JLabel volumeLabel = new JLabel("Volume");
		volumeLabel.setBounds(450, 10, 100, 20);
		window.add(volumeLabel);
		
		JLabel localVolumeLabel = new JLabel("Local Volume");
		localVolumeLabel.setBounds(375, 125, 100, 20);
		window.add(localVolumeLabel);
		
		JSlider localVolume = new JSlider(JSlider.VERTICAL, 0, 100, 100);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		for(int i = 0; i < 5; i++)
			labelTable.put(100/4 * i, new JLabel(100/4 * i + "%"));
		localVolume.setLabelTable(labelTable);
		localVolume.setPaintLabels(true);
		localVolume.setBounds(400, 30, 50, 100);
		localVolume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting())
					piano.setVolume((int)source.getValue());
			}
		});
		window.add(localVolume);
		
		JLabel netVolumeLabel = new JLabel("Net Volume");
		netVolumeLabel.setBounds(480, 125, 100, 20);
		window.add(netVolumeLabel);
		
		JSlider netVolume = new JSlider(JSlider.VERTICAL, 0, 100, 100);
		labelTable = new Hashtable<Integer, JLabel>();
		for(int i = 0; i < 5; i++)
			labelTable.put(100/4 * i, new JLabel(100/4 * i + "%"));
		netVolume.setLabelTable(labelTable);
		netVolume.setPaintLabels(true);
		netVolume.setBounds(500, 30, 50, 100);
		netVolume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting())
					piano.getClient().getPiano().setVolume((int)source.getValue());
			}
		});
		window.add(netVolume);
		
		JButton mute = new JButton("Self Mute");
		mute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				piano.toggleMute();
			}
		});
		mute.setBounds(420, 150, 100, 30);
		window.add(mute);

		window.setVisible(true);
	}
}
