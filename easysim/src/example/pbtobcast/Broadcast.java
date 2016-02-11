package example.pbtobcast;

import easysim.Simulator;
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
    // ------------------------------------------------------------------------
    // Fields for statistics
    // ------------------------------------------------------------------------
    int nbReceivedMessages = 0;
    Integer seq = 0;
    Boolean isWaiting = false;
    ArrayList<String> receivedMessages = new ArrayList<>();  
    LinkedList<Integer> waitingLine = new LinkedList<>(); 



    public Broadcast(String prefix) {
        super(prefix);
    }

    public void cycleHandler() {
        // Decide if you send a request 
        if ( Math.random() < 0.25 ) return;
        
        // Send a request if necessary
        if ( ! this.isWaiting && id != 0 ) {
            send(new BroadcastMessage(this.seq++, BroadcastMessage.TYPE_REQ, id), neighbors[0]);
        }
        
        // Pick the next node in the waiting line
        if ( ! this.waitingLine.isEmpty() ) {
            Integer dest = this.waitingLine.pop();
            send(new BroadcastMessage(this.seq++, BroadcastMessage.TYPE_ACK, id), neighbors[dest]);
        }

        // Handle incoming messages
        BroadcastMessage m;
        while ((m = receive()) != null) {
            // If this is a request
            if ( m.isType(BroadcastMessage.TYPE_REQ) && id == 0 ) {
                // Add it to the waiting line
                this.waitingLine.add(m.id_src);
            }
            // If this is a ack
            if ( m.isType(BroadcastMessage.TYPE_ACK) && id != 0 ) {
                // Send the broadcast
                send(new BroadcastMessage(this.seq++, BroadcastMessage.TYPE_MSG, id), neighbors[0]);
            }
            // If this is a message
            if ( m.isType(BroadcastMessage.TYPE_MSG) ) {
                nbReceivedMessages++;
                // Save it
                this.receivedMessages.add(m.seq);
                // Broadcast it
                if ( id == 0 && ! m.broadcasted ) {
                    m.broadcasted = true;
                    send(m, neighbors);
                }
            }
        }
        
        // Print the trace
        if ( Simulator.getCycle() == Configuration.getInt("simulation.cycles")-1 ) {
            String receivedMessage = "";
            for(String msg: this.receivedMessages) receivedMessage += " "+msg;
            System.out.println("[Node "+id+"]");
            System.out.println("  Number received messages = "+this.nbReceivedMessages);
            System.out.println("  Received messages = "+receivedMessages);
        }
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Example";
    }
}
