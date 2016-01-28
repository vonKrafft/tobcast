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

import easysim.core.Network;

/**
 * Takes a {@link easysim.core.Linkable} protocol and adds edges that define a
 * hypercube. Note that no connections are removed, they are only added. So it
 * can be used in combination with other initializers.
 * 
 * @see GraphFactory#wireRingLattice
 */
public class WireHypercube extends AbstractWire
{

  // --------------------------------------------------------------------------
  // Parameters
  // --------------------------------------------------------------------------

  // --------------------------------------------------------------------------
  // Fields
  // --------------------------------------------------------------------------

  // --------------------------------------------------------------------------
  // Initialization
  // --------------------------------------------------------------------------

  /**
   * Standard constructor that reads the configuration parameters. Invoked by
   * the simulation engine.
   * 
   * @param prefix the configuration prefix for this class
   */
  public WireHypercube(String prefix)
  {
    super(prefix);
  }

  // --------------------------------------------------------------------------
  // Public methods
  // --------------------------------------------------------------------------

  /**
   * A hypercube. Wires a hypercube. For a node i the following links are added:
   * i xor 2^0, i xor 2^1, etc. this define a log(graphsize) dimensional
   * hypercube (if the log is an integer).
   * 
   * @param g the graph to be wired
   * @return returns g for convinience
   */
  public void wire()
  {
    final int n = Network.size();
    if (n <= 1)
      return;
    final int highestone = Integer.highestOneBit(n - 1); // not zero
    for (int i = 0; i < n; ++i)
    {
      int mask = highestone;
      while (mask > 0)
      {
        int j = i ^ mask;
        if (j < n)
          setLink(i, j);
        mask = mask >> 1;
      }

    }
  }

  // --------------------------------------------------------------------------

}
