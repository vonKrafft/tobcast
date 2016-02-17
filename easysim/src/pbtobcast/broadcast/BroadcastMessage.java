package pbtobcast.broadcast;

import easysim.core.Message;

public class BroadcastMessage extends Message {
	public int hops = 0;
    @Override
    public int getType() {
        return Message.TYPE.UNDEFINED;
    }
}
