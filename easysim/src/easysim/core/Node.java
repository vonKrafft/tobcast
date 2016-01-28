
package easysim.core;

import java.util.Iterator;
import java.util.LinkedList;

import easysim.Simulator;
import easysim.TimeDiagram;
import easysim.config.Configuration;

/**
 * This class defines an abstract Node.
 * 
 * @author Vivien Quema
 */
public abstract class Node<T extends Message> {

    // ------------------------------------------------------------------------
    // Configuration fields
    // ------------------------------------------------------------------------

    private static final String            LATENCY_RANGE           = "latencyRange";

    private static final String            PER_MESSAGE_LATENCY     = "perMessageLatency";

    private static final String            CONSTANT_LATENCY        = "constantLatency";

    private static final String            MAX_MESSAGES_TO_RECEIVE = "maxMessagesToReceive";

    private static final String            MAX_MESSAGES_TO_SEND    = "maxMessagesToSend";

    // In which range does the latency varies?
    private final int                      latencyRange;

    // Is the latency varying per message (or per destination)?
    private final boolean                  perMessageLatency;

    // Is the latency constant for all messages?
    private final boolean                  constantLatency;

    // How many messages can the node receive at the end of each round?
    private final int                      maxMessagesToReceive;

    // How many messages can the node send at the start of each round?
    // Note that a multicast counts one emission
    private final int                      maxMessagesToSend;

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // inQueue of the node (stores Message objects)
    private LinkedList<T>                  inQueue                 = new LinkedList<T>();

    // outQueue of the node (stores MessageEmission objects)
    private LinkedList<MessageEmission<T>> outQueue                = new LinkedList<MessageEmission<T>>();

    // Array of neighbors
    protected Node<T>[]                    neighbors               = new Node[0];

    // Current number of nodes in the neighbors list
    private int                            nbNeighbors             = 0;

    // Id of this node
    public int                             id                      = -1;


    // Nb of messages received in the current round
    private int                            nbReceivedMessagesInCurrentRound;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new Node
     * 
     * @param prefix the component prefix declared in the configuration file.
     */
    public Node(String prefix)
    {
        latencyRange = Configuration.getInt(prefix + "." + LATENCY_RANGE);

        perMessageLatency = Configuration.contains(prefix + "."
                + PER_MESSAGE_LATENCY);

        constantLatency = Configuration.contains(prefix + "." + CONSTANT_LATENCY);

        maxMessagesToSend = Configuration.getInt(prefix + "."
                + MAX_MESSAGES_TO_SEND);

        maxMessagesToReceive = Configuration.getInt(prefix + "."
                + MAX_MESSAGES_TO_RECEIVE);
    }

    // ------------------------------------------------------------------------
    // Abstract methods to be implemented by sub-classes
    // ------------------------------------------------------------------------

    /**
     * Method to be implemented by classes extending the Node class.
     * <p>
     * The behavior of the cycleHandler should be as follows:
     * <ul>
     * <li>Receive incoming messages calling the receive() function</li>
     * <li>Handle messages (possibly send messages using the send function)</li>
     * </ul>
     * <p>
     * Typical code of a cycleHandler looks as follows:
     * <p>
     * <code>
     * while ((m = receive()) != null){
     * <br> &nbsp;&nbsp; // Handle message m
     * <br> &nbsp;&nbsp; // Possibly send messages using the send function
     * <br>}
     * </code>
     */
    abstract public void cycleHandler();

    // ------------------------------------------------------------------------
    // Methods to be used by sub-classes to send/receive messages
    // ------------------------------------------------------------------------

    /**
     * Sends the given message to the specified destinations with the given
     * latencies.
     * 
     * @param message the message to send
     * @param destinations an array containing the destinations to which the
     *          message must be sent
     * @param latencies an array containing the latency for each destination (note
     *          that if a latency equals -1, then the message is not sent)
     */
    public void send(T message, Node<T>[] destinations, int[] latencies) {
        outQueue.addLast(new MessageEmission<T>(message, destinations, latencies));
    }


    /**
     * Sends the given message to the specified destination with the given
     * latency.
     * 
     * @param message the message to send
     * @param destination the destination to which the message must be sent
     * @param latency the latency of the message transfer (note that if a latency
     *          equals -1, then the message is not sent)
     */
    public void send(T message, Node<T> destination, int latency) {
        outQueue.addLast(new MessageEmission<T>(message, new Node[]{destination}, new int[]{latency}));
    }

    /**
     * Sends the given message to the specified destinations (latencies of message
     * transfer are returned by the getLatency function)
     * 
     * @param message the message to send
     * @param destinations an array containing the destinations to which the
     *          message must be sent
     */
    public void send(T message, Node<T>[] destinations) {
        outQueue.addLast(new MessageEmission<T>(message, destinations,
                getLatency(destinations)));
    }

    /**
     * Sends the given message to the specified destination (the latency of the
     * transfer is returned by the getLatency function).
     * 
     * @param message the message to send
     * @param destination the destination to which the message must be sent
     */
    public void send(T message, Node<T> destination) {
        Node[] destinations = new Node[]{destination};
        outQueue.addLast(new MessageEmission<T>(message, destinations, getLatency(destinations)));
    }

    public LinkedList<MessageEmission<T>> getOutQueue (){
        return outQueue;
    }

    /**
     * Returns one message from the inQueue, and <code>null</code> if no message
     * is available.
     * 
     * @return one message from the inQueue, and <code>null</code> if no message
     *         is available.
     */
    public T receive() {
        if (inQueue.size() > 0
                && (maxMessagesToReceive == -1 || nbReceivedMessagesInCurrentRound < maxMessagesToReceive)) {
            Iterator<T> iter = inQueue.iterator();
            while (iter.hasNext()) {
                T message = iter.next();
                if (message.sendingCycle + message.latency <= Simulator.getCycle()) {
                    iter.remove();
                    if ( !message.getIsEmpty() ) {
                        TimeDiagram.addArrow(message.sendingNode, id, message.sendingCycle,          
                                Simulator.getCycle(), message.id, message.color);

                        TimeDiagram.addCircle(message.sendingNode, id, message.sendingCycle,
                                Simulator.getCycle(), message.id, 1);

                        message.setAckedBy(id);

                        boolean[] ackedBy = message.getAckedBy();
                        if (ackedByAll(ackedBy)) {
                            TimeDiagram.addAck(message.sendingNode, id, message.sendingCycle,
                                    Simulator.getCycle(), message.id, message.color);
                        }

                        nbReceivedMessagesInCurrentRound++;        
                    }
                    else {
                        TimeDiagram.addCircle(message.sendingNode, id, message.sendingCycle,
                                Simulator.getCycle(), message.id, message.color);

                    }
                    return message;
                }
            }
            // All messages have a latency such that they cannot be delivered
            return null;
        }
        else {
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Private methods
    // ------------------------------------------------------------------------

    private boolean ackedByAll(boolean[] ackedBy) {
        for (int i = 0; i < ackedBy.length; i++) {
            if (!ackedBy[i]) return false;		  
        }
        return true;
    }

    private int[] getLatency(Node[] destinations) {
        int[] latencies = new int[destinations.length];
        int latency = -1;

        if (constantLatency) {
            latency = 1;
        } else if (perMessageLatency) {
            latency = Simulator.r.nextInt(latencyRange);
        }

        for (int i = 0; i < latencies.length; i++) {
            latencies[i] = (latency != -1) ? latency : Simulator.r
                    .nextInt(latencyRange) + 1;
        }
        return latencies;
    }

    // ------------------------------------------------------------------------
    // Utility methods, used by the simulator
    // ------------------------------------------------------------------------

    public int getNbMessagesToHandle() {  
        // Send messages that need to be sent
        return  (maxMessagesToSend == -1) ? outQueue.size() : Math.min(maxMessagesToSend, outQueue.size());
    }

    public void outQueueUpdate() {
        // Reset nbReceivedMessagesInCurrentRound
        nbReceivedMessagesInCurrentRound = 0;

        // Send messages that need to be sent
        int nbMessagesToHandle = getNbMessagesToHandle();
        Iterator<MessageEmission<T>> iter = outQueue.iterator();

        int nbSentMessages = 0;
        while ((nbSentMessages < nbMessagesToHandle) && (iter.hasNext())) {
            MessageEmission<T> message = iter.next();
            // Should the message be removed?
            for (int j = 0; j < message.destinations.length; j++) {
                if (message.latencies[j] != -1) {
                    // message.latencies[j] == -1 --> the message is not sent (simulate
                    // lost message)
                    T toSend = (T) message.message.clone();
                    toSend.sendingCycle = Simulator.getCycle();
                    toSend.sendingNode = id;
                    toSend.latency = message.latencies[j];
                    if (message.message.color == -1) {
                        message.message.color = TimeDiagram.chooseColor();
                        message.message.id = Simulator.messageIdGenerator++;
                    }
                    toSend.color = message.message.color;
                    toSend.id = message.message.id;
                    message.destinations[j].inQueue.addLast(toSend);
                }
            }
            iter.remove();
            nbSentMessages++;
        }
    }

    /**
     * Adds given node if it is not already in the neighbor list. There is no
     * limit to the number of nodes that can be added.
     */
    public boolean addNeighbor(Node<T> n) {
        for (int i = 0; i < nbNeighbors; i++) {
            if (neighbors[i] == n)
                return false;
        }

        if (nbNeighbors == neighbors.length) {
            Node<T>[] temp = new Node[neighbors.length + 1];
            System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
            neighbors = temp;
        }
        neighbors[nbNeighbors] = n;
        nbNeighbors++;
        System.out.println(id + " linked to " + n.id);
        return true;
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Node ID: " + id + "\n");
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

}
