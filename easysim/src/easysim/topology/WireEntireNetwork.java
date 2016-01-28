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
 * ring lattice. Note that no connections are removed, they are only added. So
 * it can be used in combination with other initializers.
 * 
 * @see GraphFactory#wireEntireNetwork
 */
public class WireEntireNetwork extends AbstractWire {

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
  public WireEntireNetwork(String prefix) {
    super(prefix);
  }

  // --------------------------------------------------------------------------
  // Public methods
  // --------------------------------------------------------------------------

  /**
   * Wires the entire network. 
   * 
   * @param g the graph to be wired
   * @return returns g for convenience
   */
  public void wire() {
    final int n = Network.size();
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; j++) {
        setLink(i, j);
      }
    }
  }

  // --------------------------------------------------------------------------

}
