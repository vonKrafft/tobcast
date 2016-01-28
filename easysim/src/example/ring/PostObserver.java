
package example.ring;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import easysim.Simulator;
import easysim.config.Configuration;
import easysim.core.Control;
import easysim.core.Network;
import easysim.util.IncrementalStats;

/**
 * @author Vivien Quema
 */
public class PostObserver implements Control
{

  // /////////////////////////////////////////////////////////////////////
  // Constants
  // /////////////////////////////////////////////////////////////////////

  private static final String NB_RECEIVED_MESSAGES = "nbReceivedMessages";

  // /////////////////////////////////////////////////////////////////////
  // Fields
  // /////////////////////////////////////////////////////////////////////

  private final String        name;

  private boolean             nbReceivedMessages;

  private FileOutputStream    nbReceivedMessagesFos;

  // /////////////////////////////////////////////////////////////////////
  // Constructor
  // /////////////////////////////////////////////////////////////////////

  /**
   * Creates a new observer reading configuration parameters.
   */
  public PostObserver(String name)
  {
    this.name = name;
    nbReceivedMessages = Configuration.contains(name + ".observe."
        + NB_RECEIVED_MESSAGES);

    try
    {
      // initialize output streams
      if (nbReceivedMessages)
      {
        nbReceivedMessagesFos = new FileOutputStream(NB_RECEIVED_MESSAGES
            + ".txt");
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }

  }

  // /////////////////////////////////////////////////////////////////////
  // Methods
  // /////////////////////////////////////////////////////////////////////

  /**
   * Print statistics for a Example round.
   */
  public boolean execute()
  {

    long time = Simulator.getCycle();

    if (nbReceivedMessages)
    {
      nbReceivedMessageObservation(time);
    }

    // Don't terminate
    return false;
  }

  private void nbReceivedMessageObservation(long time)
  {
    IncrementalStats nbReceivedMessagesStats = new IncrementalStats();
    for (int i = 0; i < Network.size(); i++)
    {
      Ring protocol = (Ring) Network.get(i);
      nbReceivedMessagesStats.add(protocol.nbReceivedMessages);
    }
    PrintStream pstr = new PrintStream(nbReceivedMessagesFos);
    pstr.println(time + " : " + NB_RECEIVED_MESSAGES
        + " stats (per process) = " + nbReceivedMessagesStats);
    System.err.println(name + " : " + time + " : " + NB_RECEIVED_MESSAGES
        + " stats (per process) = " + nbReceivedMessagesStats);
  }
}
