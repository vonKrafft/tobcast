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

package easysim;

import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.JFrame;

import easysim.config.Configuration;
import easysim.config.IllegalParameterException;
import easysim.config.MissingParameterException;
import easysim.config.ParsedProperties;
import easysim.core.Control;
import easysim.core.Network;
import easysim.util.ExtendedRandom;

/**
 * This is the main entry point to peersim. This class loads configuration and
 * starts the simulation. The configuration can describe a set of
 * {@link Protocol}s, and their ordering, a set of {@link Control}s and their
 * ordering and a set of initializers and their ordering. See parameters
 * {@value #PAR_INIT}, {@value #PAR_CTRL}. Out of the set of protocols, this
 * engine only executes the ones that implement the {@link Protocol} interface.
 * <p>
 * One experiment run by {@link #nextExperiment} works as follows. First the
 * initializers are run in the specified order, then the following is iterated
 * {@value #PAR_CYCLES} times: If {@value #PAR_NOMAIN} is specified, then simply
 * the controls specified in the configuration are run in the specified order.
 * If {@value #PAR_NOMAIN} is not specified, then the controls in the
 * configuration are run in the specified order, followed by the execution of
 * {@link FullNextCycle}.
 * <p>
 * Finally, any control can interrupt an experiment at any time it is executed
 * by returning true in method {@link Control#execute}.
 * 
 * @see Configuration
 */
public class Simulator
{

  // ========================== static constants ==========================
  // ======================================================================

  /**
   * Configuration parameter used to initialize the random seed. If it is not
   * specified the current time is used.
   * 
   * @config
   */
  public static final String   PAR_SEED           = "random.seed";

  /**
   * If present, this parameter activates the redirection of the standard output
   * to a given PrintStream. This comes useful for processing the output of the
   * simulation from within the simulator.
   * 
   * @config
   */
  public static final String   PAR_REDIRECT       = "simulation.stdout";

  /**
   * Parameter representing the number of times the experiment is run. Defaults
   * to 1.
   * 
   * @config
   */
  public static final String   PAR_EXPS           = "simulation.experiments";

  /**
   * Parameter representing the maximum number of cycles to be performed in each
   * simulation.
   * 
   * @config
   */
  private static final String  PAR_CYCLES         = "simulation.cycles";

  /**
   * This is the prefix for initializers. These have to be of type
   * {@link Control}. They are run at the beginning of each experiment, in the
   * order specified by the configuration.
   * 
   * @see Configuration
   * @config
   */
  private static final String  PAR_INIT           = "init";

  /**
   * This is the prefix for controls. These have to be of type {@link Control}.
   * They are run before each cycle, in the order specified by the
   * configuration.
   * 
   * @see Configuration
   * @config
   */
  private static final String  PAR_CTRL           = "control";

  /**
   * Parameter defining whether a time diagram must be drawn at the end of the
   * experiment.
   * 
   * @config
   */
  private static final String  TIME_DIAGRAM       = "simulation.timeDiagram";

  // ========================== fields ===================================
  // ======================================================================

  /** The maximum number of cycles to be performed in each simulation. */
  private static int           cycles;

  /** Holds the modifiers of each simulation. */
  private static Control[]     controls           = null;

  /**
   * Current cycle.
   */
  private static int           currentCycle       = 0;

  /**
   * This source of randomness should be used by all nodes. This field is public
   * because it doesn't matter if it changes during an experiment (although it
   * shouldn't) until no other sources of randomness are used within the system.
   */
  public static ExtendedRandom r                  = null;

  public static TimeDiagram    timeDiagram;

  public static int            messageIdGenerator = 0;

  // ========================== methods ===================================
  // ======================================================================

  /**
   * Loads the configuration and executes the experiments. The number of
   * independent experiments is given by config parameter {@value #PAR_EXPS}.
   * In all experiments the configuration is the same, only the random seed is
   * not re-initialized between experiments.
   * <p>
   * Loading the configuration is currently done with the help of constructing
   * an instance of {@link ParsedProperties} using the constructor
   * {@link ParsedProperties#ParsedProperties(String[])}. The parameter
   * <code>args</code> is simply passed to this class. This class is then used
   * to initialize the configuration.
   * <p>
   * After loading the configuration, the experiments are run.
   * 
   * @param args passed on to
   *          {@link ParsedProperties#ParsedProperties(String[])}
   * @see ParsedProperties
   * @see Configuration
   */
  public static void main(String[] args)
  {
    System.err.println("Simulator: loading configuration");
    Configuration.setConfig(new ParsedProperties(args));

    long seed = Configuration.getLong(PAR_SEED, System.currentTimeMillis());
    r = new ExtendedRandom(seed);

    PrintStream newout = (PrintStream) Configuration.getInstance(PAR_REDIRECT,
        System.out);
    if (newout != System.out)
      System.setOut(newout);

    int exps = Configuration.getInt(PAR_EXPS, 1);

    try
    {
      for (int k = 0; k < exps; ++k)
      {
        if (k > 0)
        {
          // Generate a new seed.
          seed = r.nextLong();
          r = new ExtendedRandom(seed);
        }
        System.err.print("Simulator: starting experiment " + k);
        System.err.println("Random seed: " + r.getLastSeed());
        System.out.println("\n\n");

        Simulator.nextExperiment();
      }
    }
    catch (MissingParameterException e)
    {
      System.err.println(e + "");
      System.exit(1);
    }
    catch (IllegalParameterException e)
    {
      System.err.println(e + "");
      System.exit(1);
    }

  }

  // ================ public methods =====================================
  // =====================================================================

  /**
   * Returns current cycle.
   */
  public static int getCycle()
  {
    return currentCycle;
  }

  // =============== private methods =====================================
  // =====================================================================

  /**
   * Load and run initializers.
   */
  private static void runInitializers()
  {

    Object[] inits = Configuration.getInstanceArray(PAR_INIT);
    String names[] = Configuration.getNames(PAR_INIT);

    for (int i = 0; i < inits.length; ++i)
    {
      System.err.println("- Running initializer " + names[i] + ": "
          + inits[i].getClass());
      ((Control) inits[i]).execute();
    }
  }

  // --------------------------------------------------------------------

  private static String[] loadControls()
  {

    String[] names = Configuration.getNames(PAR_CTRL);
    controls = new Control[names.length];

    for (int i = 0; i < names.length; ++i)
    {
      controls[i] = (Control) Configuration.getInstance(names[i]);
    }
    System.err.println("Simulator: loaded controls " + Arrays.asList(names));
    return names;
  }

  // ---------------------------------------------------------------------
  public static JFrame f;

  /**
   * Runs an experiment, resetting everything except the random seed.
   */
  public static final void nextExperiment()
  {
    // Activate or not the time diagram functionality
    if (Configuration.contains(TIME_DIAGRAM))
    {
      TimeDiagram.activate();
    }

    // Reading parameter
    cycles = Configuration.getInt(PAR_CYCLES);

    // initialization
    currentCycle = 0;
    System.err.println("Simulator: resetting");
    controls = null;
    Network.reset();
    System.err.println("Simulator: running initializers");
    runInitializers();

    // main cycle
    loadControls();
    System.err.println("Simulator: starting simulation");
    for (int i = 0; i < cycles; ++i)
    {
      currentCycle = i;

      boolean stop = false;
      for (int j = 0; j < controls.length; ++j)
      {
        stop = stop || controls[j].execute();
      }

      if (stop)
      {
        break;
      }

      for (int j = 0; j < Network.size(); ++j)
      {
        Network.get(j).cycleHandler();
      }

      for (int j = 0; j < Network.size(); j++)
      {
        Network.get(j).outQueueUpdate();
      }

      // System.err.println("Simulator: cycle " + i + " done");
    }

    // analysis after the simulation
    for (int j = 0; j < controls.length; ++j)
    {
      controls[j].execute();
    }

    // Display the TimeDiagram
    TimeDiagram.display();
  }
}
