package application.net;

import java.io.DataOutputStream;

public interface Packet {
	public void send(DataOutputStream out);
}
