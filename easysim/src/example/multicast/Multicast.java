
package example.multicast;

import easysim.Simulator;
import easysim.core.Node;

/**
 * This class provides an implementation for the Example protocol.
 * 
 * @author Vivien Quema
 */
public class Multicast extends Node<MulticastMessage>
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

  public Multicast(String prefix) {
    super(prefix);
  }

  public void cycleHandler() {
    // Node 0 injects a new message in the system
    if (id == 0 && (Simulator.getCycle() % 4 == 0)) {
      send(new MulticastMessage(), neighbors);
    }
    // Handle incoming messages
    MulticastMessage m;
    while ((m = receive()) != null) {
      nbReceivedMessages++;
      if (m.hops == 0) {
        m.hops++;
        if (id != 0) {
          // All nodes forward, but p0
          send(m, neighbors);
        }
      }
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
