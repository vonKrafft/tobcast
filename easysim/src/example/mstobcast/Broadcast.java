package example.mstobcast;

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
    ArrayList<BroadcastMessage> messagesToDeliver = new ArrayList();
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
        if (id != 0 && Math.random() < 0.31) {
            send(new BroadcastMessage(this.nextRelId++), neighbors);
        }
        // Handle incoming messages
        Message m;
        while ((m = receive()) != null) {
            nbReceivedMessages++;
            switch (m.getType()) {
                case Message.TYPE.DATA:
                    if (this.id == 0) {
                        send(new SequenceMessage(m.sendingNode, m.getRelId(), this.nextSequenceId++), neighbors);
                    }
                    receivedMessages.add((BroadcastMessage) m);
                    break;
                case Message.TYPE.BBSEQUENCE:
                    receivedSeqMessages.add((SequenceMessage) m);
                    break;
                default:
                    System.err.println("NODE " + id + ": INCONSISTENT MEESAGE TYPE:" + m.getType());
            }
        }

        int maxMessagesToDeliver = Configuration.getInt("protocol.ubtobcast.maxMessagesToDeliver");
        int nbDeliveredMessages = 0;
        int i = 0;
        while (i < receivedMessages.size()) {
            BroadcastMessage bm = receivedMessages.get(i);
            int seqNb = findSequenceNumber(bm);
            if (seqNb != -1) {
                bm.setSeqNb(seqNb);
                messagesToDeliver.add(bm);
                receivedMessages.remove(bm);
            } else {
                i++;
            }
        }
        messagesToDeliver.sort(
                (BroadcastMessage m1, BroadcastMessage m2)
                -> Integer.compare(m1.getSeqNb(), m2.getSeqNb()));
        while ((maxMessagesToDeliver == -1 || nbDeliveredMessages < maxMessagesToDeliver)
                && deliveredMessages.size() < messagesToDeliver.size()
                && seqNbToDeliver.equals(messagesToDeliver.get(deliveredMessages.size()).getSeqNb())) {
            BroadcastMessage dm = messagesToDeliver.get(deliveredMessages.size());
            deliveredMessages.add(dm);
            seqNbToDeliver++;
            nbDeliveredMessages++;
            dm.setAckedBy(this.id);
            TimeDiagram.addAck(dm, this);
        }

        if (Simulator.getCycle() == Configuration.getInt("simulation.cycles") - 1) {
            printReport();
        }
    }

    private int findSequenceNumber(BroadcastMessage bm) {
        for (SequenceMessage sm : receivedSeqMessages) {
            if (bm.sendingNode == sm.getToFindNodeFrom()
                    && bm.getRelId() == sm.getToFindRelId()) {
                receivedSeqMessages.remove(sm);
                return sm.getToAssociateSeqNb();
            }
        }
        return -1;
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
        System.out.println("Node " + this.id
                + "\n\tNumber of any-type messages received:" + nbReceivedMessages
                + "\n\tNumber of data messages received:" + (this.receivedMessages.size() + messagesToDeliver.size())
                + "\n\tNumber of messages delivered:" + this.deliveredMessages.size()
                + "\n\tOrdered messages: " + idList);
    }
}
