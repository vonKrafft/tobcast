package example.pbtobcast;

import easysim.core.Message;

public class BroadcastMessage extends Message {

    public        int hops = 0;
    private final int type;       // The type of the message
    private       int seqNb;      // The sequence number of the message
    private final int idSrc;      // The node's id of the sender

    /*
    Constructors
    */
    public BroadcastMessage(int type) {
        this.type  = type;
        this.seqNb = -1;
        this.idSrc = -1;
    }
    public BroadcastMessage(int type, int seqNb){
        this.type  = type;
        this.seqNb = seqNb;
        this.idSrc = -1;
    }
    public BroadcastMessage(int type, int seqNb, int idSrc){
        this.type  = type;
        this.seqNb = seqNb;
        this.idSrc = idSrc;
    }
    
    /*
    Methods
    */
    @Override
    public int getType() {
        return this.type;
    }
    
    @Override
    public int getSeqNb() {
        return this.seqNb;
    }

    public void setSeqNb(int seqNb) {
        this.seqNb = seqNb;
    }
    
    public int getIdSrc() {
        return this.idSrc;
    }
}
