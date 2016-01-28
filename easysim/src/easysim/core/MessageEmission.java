
package easysim.core;


/**
 * @author Vivien Quema
 */
public class MessageEmission<T extends Message>
{

  /**
   * The message to be transfered.
   */
  public T         message;

  public Node<T>[] destinations;

  /**
   * latencies[i] is the number of cycle the message will spend in the inQueue
   * of the receiving process before being candidate for being received.
   * <p>
   * latencies[i] == -1 implies that the message will not be sent to
   * destinations[i]
   */
  public int[]     latencies;

  /**
   * Constructor.
   * 
   * @param message the message to be transfered.
   * @param destinations the destinations to which this message must be sent.
   * @param latencies an array containing the latencies for all destinations.
   */
  public MessageEmission(T message, Node<T>[] destinations, int latencies[])
  {
    this.message = message;
    this.destinations = destinations;
    this.latencies = latencies;
  }
  
}
