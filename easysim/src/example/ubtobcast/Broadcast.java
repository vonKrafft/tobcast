package example.ubtobcast;

import easysim.Simulator;
import easysim.TimeDiagram;
import easysim.config.Configuration;
import easysim.core.Node;
import java.util.ArrayList;

/**
 * This class provides an implementation for the Example protocol.
 *
 * @author Vivien Quema
 */
public class Broadcast extends Node<BroadcastMessage> {

    // ------------------------------------------------------------------------
    // Global configuration fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private Integer currentId = 0;
    private Integer currentSequenceId = 0;
    ArrayList<BroadcastMessage> receivedMessages = new ArrayList();
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
        if (Math.random() < 0.5) {
            send(new BroadcastMessage(), neighbors[0]);
        }
        // Handle incoming messages
        BroadcastMessage m;
        while ((m = receive()) != null) {
            nbReceivedMessages++;
            // Node 0 is the sequencer
            if (id == 0 && m.getSeqNb() == -1) {
                m.setSeqNb(currentSequenceId++);
                m.hops++;
                send(m, neighbors);
            } else {
                receivedMessages.add(m);
            }
        }

        if (deliveredMessages.size() < receivedMessages.size()) {
            int maxMessagesToDeliver = Configuration.getInt("protocol.ubtobcast.maxMessagesToDeliver");
            int nbDeliveredMessages = 0;
            receivedMessages.sort(
                    (BroadcastMessage m1, BroadcastMessage m2)
                    -> Integer.compare(m1.getSeqNb(), m2.getSeqNb()));
            while ((maxMessagesToDeliver == -1 || nbDeliveredMessages < maxMessagesToDeliver)
                    && deliveredMessages.size() < receivedMessages.size()
                    && currentId.equals(receivedMessages.get(deliveredMessages.size()).getSeqNb())) {
                BroadcastMessage dm = receivedMessages.get(deliveredMessages.size());
                deliveredMessages.add(dm);
                currentId++;
                nbDeliveredMessages++;
                deliveredMessages.get(deliveredMessages.size() - 1).setAckedBy(this.id);
                TimeDiagram.addAck(dm.sendingNode, id, dm.sendingCycle,
                        Simulator.getCycle(), dm.id, dm.color, dm.getSeqNb());
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
