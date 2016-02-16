package example.ubtobcast;

import easysim.core.Message;

public class BroadcastMessage extends Message {

    public int hops = 0;
    private int seqNb;
    
    public BroadcastMessage(){
        this.seqNb = -1;
    }
    public BroadcastMessage(int seqNb){
        this.seqNb = seqNb;
    }

    @Override
    public int getSeqNb() {
        return seqNb;
    }

    public void setSeqNb(int seqNb) {
        this.seqNb = seqNb;
    }
    
}
