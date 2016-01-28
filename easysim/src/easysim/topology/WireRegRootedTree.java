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
import easysim.core.Network;

/**
 * Takes a {@link easysim.core.Linkable} protocol and adds connections that
 * define a regular rooted tree. Note that no connections are removed, they are
 * only added. So it can be used in combination with other initializers.
 * 
 * @see #wire
 * @see GraphFactory#wireRegRootedTree
 */
public class WireRegRootedTree extends AbstractWire
{

  // --------------------------------------------------------------------------
  // Parameters
  // --------------------------------------------------------------------------

  /**
   * The parameter of the tree wiring method. It is passed to
   * {@link GraphFactory#wireRegRootedTree}.
   * 
   * @config
   */
  private static final String PAR_DEGREE = "k";

  // --------------------------------------------------------------------------
  // Fields
  // --------------------------------------------------------------------------

  private final int           k;

  // --------------------------------------------------------------------------
  // Initialization
  // --------------------------------------------------------------------------

  /**
   * Standard constructor that reads the configuration parameters. Invoked by
   * the simulation engine.
   * 
   * @param prefix the configuration prefix for this class
   */
  public WireRegRootedTree(String prefix)
  {
    super(prefix);
    k = Configuration.getInt(prefix + "." + PAR_DEGREE);
  }

  // --------------------------------------------------------------------------
  // Methods
  // --------------------------------------------------------------------------

  /**
   * A regular rooted tree. Wires a regular rooted tree. The root is 0, it has
   * links to 1,...,k. In general, node i has links to i*k+1,...,i*k+k.
   * 
   * @param g the graph to be wired
   * @param k the number of outgoing links of nodes in the tree (except leaves
   *          that have zero out-links, and exactly one node that might have
   *          less than k).
   * @return returns g for convinience
   */
  public void wire()
  {
    if (k == 0)
      return;
    final int n = Network.size();
    int i = 0; // node we wire
    int j = 1; // next free node to link to
    while (j < n)
    {
      for (int l = 0; l < k && j < n; ++l, ++j)
        setLink(i, j);
      ++i;
    }
  }

}
