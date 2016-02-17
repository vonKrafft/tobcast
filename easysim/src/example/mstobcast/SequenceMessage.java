package example.mstobcast;

import easysim.core.Message;

public class SequenceMessage extends Message {

    public int hops = 0;
    private int toFindNodeFrom;
    private int toFindRelId;
    private int toAssociateSeqNb;
    
    public SequenceMessage(){
    }

    public SequenceMessage(int toFindNodeFrom, int toFindRelId, int toAssociateSeqNb) {
        this.toFindNodeFrom = toFindNodeFrom;
        this.toFindRelId = toFindRelId;
        this.toAssociateSeqNb = toAssociateSeqNb;
    }

    public int getToFindNodeFrom() {
        return toFindNodeFrom;
    }

    public int getToFindRelId() {
        return toFindRelId;
    }

    public int getToAssociateSeqNb() {
        return toAssociateSeqNb;
    }

    @Override
    public int getType() {
        return Message.TYPE.BBSEQUENCE;
    }
    
}
