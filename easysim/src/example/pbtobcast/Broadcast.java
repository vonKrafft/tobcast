package example.pbtobcast;

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
        // If random and you are not waiting for a ACK message and you are not the sequencer
        if ( Math.random() < 0.75  && ! this.isWaiting && id != this.ID_SEQUENCER ) {
            // Send a REQ message to the sequencer
            send(new BroadcastMessage(BroadcastMessage.TYPE.REQ, -1, id), neighbors[this.ID_SEQUENCER]);
            // Wait for the ACK message
            this.isWaiting = true;
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
                // You are not waiting for a ACK message anymore
                this.isWaiting = false;
            }
            // If this is a DATA message
            if ( m.getType() == BroadcastMessage.TYPE.DATA ) {
                nbReceivedMessages++;
                this.receivedMessages.add(m.getSeqNb());
                // If you are the sequencer and this message has the correct number sequence
                if ( id == this.ID_SEQUENCER && m.getSeqNb() == this.seqNb ) {
                    // You are not waiting for a DATA message anymore
                    this.isWaiting = false;
                }
                // Deliver it
                TimeDiagram.addAck(m, this);
            }
        }
        
        // If you are not waiting for a DATA message and you are the sequencer and the waiting line is not empty
        if ( ! this.isWaiting && id == this.ID_SEQUENCER && ! this.waitingLine.isEmpty() ) {
            // Pick the next node in the waiting line
            int idDest;
            idDest = this.waitingLine.pop();
            // Increment the sequence number
            this.seqNb++;
            // Send a ACK message to the initial sender
            send(new BroadcastMessage(BroadcastMessage.TYPE.ACK, this.seqNb), neighbors[idDest]);
            // Wait for a DATA message
            this.isWaiting = true;
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
