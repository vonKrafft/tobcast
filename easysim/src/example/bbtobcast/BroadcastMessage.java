package example.bbtobcast;

import easysim.core.Message;

public class BroadcastMessage extends Message {

    public int hops = 0;
    private int relId;
    private int seqNb;
    
    public BroadcastMessage(){
        this.seqNb = -1;
        this.relId = -1;
    }
    public BroadcastMessage(int relId){
        this.relId = relId;
        this.seqNb = -1;
    }
    public BroadcastMessage(int relId, int seqNb){
        this.relId = relId;
        this.seqNb = seqNb;
    }

    @Override
    public int getRelId() {
        return relId;
    }

    public void setRelId(int relId) {
        this.relId = relId;
    }
    
    @Override
    public int getSeqNb() {
        return seqNb;
    }

    public void setSeqNb(int seqNb) {
        this.seqNb = seqNb;
    }

    @Override
    public int getType() {
        return Message.TYPE.DATA;
    }
    
}
