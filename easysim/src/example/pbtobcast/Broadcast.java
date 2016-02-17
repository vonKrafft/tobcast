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
    public final int ID_SEQUENCER = 0;
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private int                 seqNb       = 0;
    private boolean             isWaiting   = false;
    private LinkedList<Integer> waitingLine = new LinkedList<>(); 

    // ------------------------------------------------------------------------
    // Fields for statistics
    // ------------------------------------------------------------------------
    public int                 nbReceivedMessages = 0;
    public ArrayList<Integer>  receivedMessages   = new ArrayList<>();  


    public Broadcast(String prefix) {
        super(prefix);
    }

    public void cycleHandler() {
        // Decide if you send a request 
        if ( Math.random() < 0.75 ) return;
        
        // If you are not waiting for a ACK message and you are not the sequencer
        if ( ! this.isWaiting && id != this.ID_SEQUENCER ) {
            // Send a REQ message to the sequencer
            send(new BroadcastMessage(BroadcastMessage.TYPE.REQ, -1, id), neighbors[this.ID_SEQUENCER]);
            // Wait for the ACK message
            this.isWaiting = true;
        }
        
        // If you are the sequencer and the waitinf line is not empty
        if ( id == this.ID_SEQUENCER && ! this.waitingLine.isEmpty() ) {
            // Pick the next node in the waiting line
            Integer idDest = this.waitingLine.pop();
            // Increment the sequence number
            this.seqNb++;
            // Send a ACK message to the initial sender
            send(new BroadcastMessage(BroadcastMessage.TYPE.ACK, this.seqNb), neighbors[idDest]);
        }

        // Handle incoming messages
        BroadcastMessage m;
        while ((m = receive()) != null) {
            // If this is a REQ message and you are the sequencer
            if ( m.getType() == BroadcastMessage.TYPE.REQ && id == this.ID_SEQUENCER ) {
                // Add it to the waiting line
                this.waitingLine.add(m.getIdSrc());
            }
            // If this is a ACK message and you are not the sequencer
            if ( m.getType() == BroadcastMessage.TYPE.ACK && id != this.ID_SEQUENCER ) {
                // Send the broadcast
                send(new BroadcastMessage(BroadcastMessage.TYPE.DATA, m.getSeqNb()), neighbors);
            }
            // If this is a DATA message
            if ( m.getType() == BroadcastMessage.TYPE.DATA ) {
                nbReceivedMessages++;
                this.receivedMessages.add(m.getSeqNb());
            }
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

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Example";
    }
}
