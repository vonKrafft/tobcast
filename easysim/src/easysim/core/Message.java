
package easysim.core;

import easysim.config.Configuration;

public class Message implements Cloneable
{
	               
	int networkSize = Configuration.getInt("network.size");	               

	private boolean[] ackedBy = new boolean[networkSize];
	
	private boolean isEmpty = false;
	
	public void setAckedBy(int id) {
		ackedBy[id] = true;
	}
	
	public boolean[] getAckedBy() {
		return ackedBy;
	}
	
	public boolean getIsEmpty() {
		return isEmpty;		
	}
	
	public void setIsEmpty(boolean flag) {
		isEmpty = flag;		
	}	
	
	// The id of the node that sent the message
  public int sendingNode;

  // The cycle in which the message was put in the inQueue
  public int sendingCycle = -1;

  // The latency of this message
  public int latency      = -1;

  // The color to be used in the TimeDiagram
  public int color        = -1;

  // A unique id generated when a message is created (a message keeps its id
  // when it is forwarded)
  public int id           = -1;

  /**
   * Returns a clone of the message.
   * 
   * @return a clone of the message.
   */
  public Object clone()
  {
    Message m = null;
    try
    {
      m = (Message) super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new InternalError("Clone not supported");
    }
    return m;
  }
}
