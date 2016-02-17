package example.mttobcast;

import easysim.Simulator;
import easysim.TimeDiagram;
import easysim.config.Configuration;
import easysim.core.Node;
import java.util.ArrayList;
import java.util.LinkedList;

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
    private int cntAck = 0;
    private int seqNb  = 0;

    // ------------------------------------------------------------------------
    // Fields for statistics
    // ------------------------------------------------------------------------
    public int                 nbReceivedMessages = 0;
    public ArrayList<Integer>  receivedMessages   = new ArrayList<>();  


    public Broadcast(String prefix) {
        super(prefix);
    }

    public void cycleHandler() {  
        // Initialize with a first broadcast
        if ( Simulator.getCycle() == 0 && id == 0 ) {
            // Broadcast a message
            send(new BroadcastMessage(BroadcastMessage.TYPE.DATA, 0, id), neighbors);
        }

        // Handle incoming messages
        BroadcastMessage m;
        while ((m = receive()) != null) {
            // If this is a DATA message from the previous node or a ACK message
            if ( (m.getType() == BroadcastMessage.TYPE.DATA && isPrevious(m.getIdSrc())) || (m.getType() == BroadcastMessage.TYPE.ACK)) {
                // Increment the ACK counter
                this.cntAck++;
                // Get the sequence number
                this.seqNb = m.getSeqNb();
            }
            // If this is a DATA message
            if ( m.getType() == BroadcastMessage.TYPE.DATA ) {
                nbReceivedMessages++;
                this.receivedMessages.add(m.getSeqNb());
                // Deliver it
                TimeDiagram.addAck(m, this);
                // If necessary, send a ACK message
                if ( ! isPrevious(m.getIdSrc()) ) {
                    send(new BroadcastMessage(BroadcastMessage.TYPE.ACK, m.getSeqNb(), id), neighbors[getNext(m.getIdSrc())]);
                }
            }
        }
        
        // If every node have received the message
        if ( this.cntAck >= neighbors.length ) {
            // Broadcast a message
            send(new BroadcastMessage(BroadcastMessage.TYPE.DATA, this.seqNb+1, id), neighbors);
            // Reset the ACK counter
            this.cntAck = 0;
        }
        
        // Print the trace
        if ( Simulator.getCycle() == Configuration.getInt("simulation.cycles")-1 ) {
            String receivedMessage = "";
            for(Integer msg: this.receivedMessages) receivedMessage += " "+msg;
            System.out.println("[Node "+id+"]");
            System.out.println("  Number received messages = "+this.nbReceivedMessages);
            System.out.println("  Received messages = "+receivedMessage);
        }
    }
    
    private boolean isPrevious(int idSrc) {
        return (idSrc+1)%neighbors.length == id;
    }

    private int getNext(int idSrc) {
        return (idSrc+1)%neighbors.length;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Example";
    }
}
