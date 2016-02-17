package example.bbtobcast;

import easysim.Simulator;
import easysim.TimeDiagram;
import easysim.config.Configuration;
import easysim.core.Message;
import easysim.core.Node;
import java.util.ArrayList;

/**
 * This class provides an implementation for the Example protocol.
 *
 * @author Vivien Quema
 */
public class Broadcast extends Node<Message> {

    // ------------------------------------------------------------------------
    // Global configuration fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    
    //Sequencer
    private Integer nextSequenceId = 0;
    
    //All
    private Integer nextRelId = 0;
    private Integer seqNbToDeliver = 0;
    ArrayList<BroadcastMessage> receivedMessages = new ArrayList();
    ArrayList<SequenceMessage> receivedSeqMessages = new ArrayList();
    ArrayList<BroadcastMessage> deliveredMessages = new ArrayList();
    // ------------------------------------------------------------------------
    // Fields for statistics
    // ------------------------------------------------------------------------
    public int nbReceivedMessages = 0;

    public Broadcast(String prefix) {
        super(prefix);
    }

    @Override
    public void cycleHandler() {
        if (Math.random() < 0.35) {
            send(new BroadcastMessage(this.nextRelId++), neighbors);
        }
        // Handle incoming messages
        Message m;
        while ((m = receive()) != null) {
            nbReceivedMessages++;
            switch(m.getType()){
                case Message.TYPE.DATA:
                    if (this.id == 0){
                        send(new SequenceMessage(m.sendingNode, m.getRelId(), this.nextSequenceId++), neighbors);
                    }
                    receivedMessages.add((BroadcastMessage)m);
                    break;
                case Message.TYPE.BBSEQUENCE:
                    receivedSeqMessages.add((SequenceMessage)m);
                    break;
                default:
                    System.err.println("NODE "+ id + ": INCONSISTENT MEESAGE TYPE:" + m.getType()); 
            }
        }

        if (deliveredMessages.size() < receivedMessages.size()) {
            int maxMessagesToDeliver = Configuration.getInt("protocol.ubtobcast.maxMessagesToDeliver");
            int nbDeliveredMessages = 0;
            receivedSeqMessages.sort(
                    (SequenceMessage m1, SequenceMessage m2)
                    -> Integer.compare(m1.getToAssociateSeqNb(), m2.getToAssociateSeqNb()));
            receivedMessages.sort(
                    (BroadcastMessage m1, BroadcastMessage m2)
                    -> Integer.compare(m1.getSeqNb(), m2.getSeqNb()));
            for (BroadcastMessage bm : receivedMessages){
                if (bm.getSeqNb() == -1){
                    
                } else {
                    break;
                }
            }
            while ((maxMessagesToDeliver == -1 || nbDeliveredMessages < maxMessagesToDeliver)
                    && deliveredMessages.size() < receivedMessages.size()
                    && currentId.equals(receivedMessages.get(deliveredMessages.size()).getSeqNb())) {
                BroadcastMessage dm = receivedMessages.get(deliveredMessages.size());
                deliveredMessages.add(dm);
                currentId++;
                nbDeliveredMessages++;
                deliveredMessages.get(deliveredMessages.size() - 1).setAckedBy(this.id);
                TimeDiagram.addAck(dm, this);
            }
        }

        if (Simulator.getCycle() == Configuration.getInt("simulation.cycles") - 1) {
            printReport();
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Example";
    }

    private void printReport() {
        String idList = "";
        idList = this.deliveredMessages.stream().map((m) -> m.getSeqNb() + " ").reduce(idList, String::concat);
        System.out.println("Node " + this.id + ":\n\tNumber of messages received:" + this.receivedMessages.size() + "\n\tNumber of messages delivered:" + this.deliveredMessages.size() + "\n\tOrdered messages: " + idList);
    }
}
