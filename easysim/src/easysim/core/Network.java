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

package easysim.core;

import easysim.config.Configuration;

/**
 * This class forms the basic framework of all simulations. The network is a set
 * of nodes implemented via an array list for the sake of efficiency. Each node
 * has an array of protocols. The protocols within a node can interact directly
 * as defined by their implementation, and can be imagined as processes running
 * in a common local environment (ie the node). This class is called a "network"
 * because, although it is only a set of nodes, in most simulations there is at
 * least one {@link Linkable} protocol that defines connections between nodes.
 * In fact, such a {@link Linkable} protocol layer can be accessed through a
 * {@link easysim.graph.Graph} view using {@link OverlayGraph}.
 */
public class Network
{

  // ========================= fields =================================
  // ==================================================================

  /**
   * This config property defines the initial size of the overlay network. This
   * property is required.
   * 
   * @config
   */
  private static final String PAR_SIZE  = "network.size";

  /**
   * Prefix of the parameter that defines the protocol implemented by nodes.
   * 
   * @config
   */
  public static final String  PAR_PROT  = "protocol";

  /**
   * The node array. This is not a private array which is not nice but
   * efficiency has the highest priority here. The main purpose is to allow the
   * package quick reading of the contents in a maximally flexible way.
   * Nevertheless, methods of this class should be used instead of the array
   * when modifiying the contents. Because this array is not private, it is
   * necessary to know that the actual node set is only the first
   * {@link #size()} items of the array.
   */
  public static Node[]        node      = null;

  /**
   * Actual size of the network.
   */
  private static int len;

  /**
   * The prototype node which is used to populate the simulation via cloning.
   * After all the nodes have been cloned, {@link Control} components can be
   * applied to perform any further initialization.
   */
  public static Node prototype = null;

  // ====================== initialization ===========================
  // =================================================================

  /**
   * Reads configuration parameters, constructs the prototype node, and
   * populates the network by cloning the prototype.
   */
  public static void reset()
  {

    if (prototype != null)
    {
      // not first experiment
      prototype = null;
      node = null;
    }

    len = Configuration.getInt(PAR_SIZE);
    if (len > 0)
    {
      node = new Node[len];
      String[] names = Configuration.getNames(PAR_PROT);
      if (names.length > 1)
      {
        throw new InternalError("More than one protocol specified.");
      }
      for (int i = 0; i < len; ++i)
      {
        node[i] = (Node) Configuration.getInstance(names[0]);
//        if ( i == 0 ) node[i].setToken(new Token(i));  // TODO give the token to node 0 at start
        node[i].id = i;
      }
    }
  }

  /** Disable instance construction */
  private Network()
  {
  }

  // =============== public methods ===================================
  // ==================================================================

  /** Number of nodes in the network */
  public static int size()
  {
    return len;
  }

  // ------------------------------------------------------------------

  /**
   * Returns node with the given ID.
   */
  public static Node get(int ID)
  {
    return node[ID];
  }
}
