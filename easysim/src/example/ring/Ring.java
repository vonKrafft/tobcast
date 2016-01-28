
package example.ring;

import easysim.core.Node;

/**
 * This class provides an implementation for the Example protocol.
 * 
 * @author Vivien Quema
 */
public class Ring extends Node<RingMessage>
{

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

  public Ring(String prefix)
  {
    super(prefix);
  }

  public void cycleHandler() {
    // Node 0 injects a new message in the system
    if (id == 0) {
      send(new RingMessage(), neighbors);
    }
    // Handle incoming messages
    RingMessage m;
    while ((m = receive()) != null) {
      nbReceivedMessages++;
      if (id != 0) {
        // All nodes forward, but p0
        send(m, neighbors);
      }
    }
  }

  // ------------------------------------------------------------------------
  // Overriden methods
  // ------------------------------------------------------------------------

  @Override
  public String toString()
  {
    return "Example";
  }
}
