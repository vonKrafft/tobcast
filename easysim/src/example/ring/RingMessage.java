package example.ring;

import easysim.core.Message;

public class RingMessage extends Message {

    @Override
    public int getType() {
        return Message.TYPE.UNDEFINED;
    }
}
