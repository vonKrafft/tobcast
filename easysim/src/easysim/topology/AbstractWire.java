/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package easysim.topology;

import easysim.config.Configuration;
import easysim.core.Control;
import easysim.core.Network;

/**
 * This class is the superclass of classes that define a certain topology. Note
 * that no connections are removed, they are only added. So it can be used in
 * combination with other initializers.
 */
public abstract class AbstractWire implements Control
{

  // --------------------------------------------------------------------------
  // Parameters
  // --------------------------------------------------------------------------
  /**
   * Alias for {@value #PAR_UNDIR}.
   * 
   * @config
   */
  private static final String PAR_UNDIR = "undirected";

  // --------------------------------------------------------------------------
  // Fields
  // --------------------------------------------------------------------------

  /** If true, edges are added in an undirected fashion. */
  public final boolean        undir;

  // --------------------------------------------------------------------------
  // Initialization
  // --------------------------------------------------------------------------

  /**
   * Standard constructor that reads the configuration parameters. Normally
   * invoked by the simulation engine.
   * 
   * @param prefix the configuration prefix for this class
   */
  protected AbstractWire(String prefix) {
    undir = Configuration.contains(prefix + "." + PAR_UNDIR);
  }

  // --------------------------------------------------------------------------
  // Public methods
  // --------------------------------------------------------------------------

  /**
   * Calls method {@link #wire} to create links between nodes in the network.
   */
  public final boolean execute() {
    if (Network.size() == 0)
      return false;
    wire();
    return false;
  }

  /**
   * The method that should wire (add links to) nodes in the networks. Has to be
   * implemented by extending classes
   */
  public abstract void wire();

  // --------------------------------------------------------------------------
  // Utility methods
  // --------------------------------------------------------------------------

  /**
   * Sets link between the specified nodes.
   * <p>
   * The behaviour of this method is affected by parameter {@link #wireDirected}.
   * If it is false, then the opposite link is set too.
   */
  public boolean setLink(int i, int j) {
    if (undir)
      Network.node[j].addNeighbor(Network.node[i]);

    return Network.node[i].addNeighbor(Network.node[j]);
  }

}
