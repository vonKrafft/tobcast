package example.multicast;

import easysim.core.Message;

public class MulticastMessage extends Message {
	public int hops = 0;

    @Override
    public int getType() {
        return Message.TYPE.UNDEFINED;
    }
}
