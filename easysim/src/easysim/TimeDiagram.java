package easysim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFrame;

import easysim.config.Configuration;
import easysim.core.Network;

public class TimeDiagram extends JApplet {
    private static final int   INTER_PROCESSES_SPACE = 80;

    private static final int   INTER_CYCLES_SPACE    = 80;

    private static final int   TOP_MARGIN            = 40;

    private static final int   LEFT_MARGIN           = 40;

    // List of arrows
    private static List<int[]> arrows                = new ArrayList<int[]>();

    private static List<int[]> circles                = new ArrayList<int[]>();

    private static List<int[]> acks                = new ArrayList<int[]>();
    private static final long  serialVersionUID      = 1L;

    private static boolean     activated             = false;

    public static int          NB_COLORS             = 12;

    private static int         currentColor          = 0;

    Color[]                    colors                = new Color[]{Color.black,
            Color.blue, Color.cyan, Color.darkGray, Color.green, Color.gray,
            Color.magenta, Color.lightGray, Color.orange, Color.pink, Color.red,
            Color.yellow};

    public void init() {
        setBackground(Color.white);
    }

    public void drawDemo(int w, int h, Graphics2D g2) {
        int  networkSize = Configuration.getInt("network.size");
        g2.setColor(Color.black);

        // Draw process lines
        {
            int x1 = LEFT_MARGIN;
            int x2 = (int) (x1 + INTER_CYCLES_SPACE * Simulator.getCycle());
            for (int i = 0; i < Network.size(); i++)
            {
                int y = INTER_PROCESSES_SPACE * i + TOP_MARGIN;
                g2.drawString("p" + i, 10, y);
                g2.drawLine(LEFT_MARGIN, y, x2, y);
            }
        }

        // Draw cycle lines
        {
            int y1 = TOP_MARGIN;
            int y2 = y1 + INTER_PROCESSES_SPACE * (Network.size() - 1);
            for (int i = 0; i < Simulator.getCycle() + 1; i++)
            {
                int x = INTER_CYCLES_SPACE * i + LEFT_MARGIN;
                g2.drawLine(x, y1, x, y2);
            }
        }

        // Draw arrows
        Iterator<int[]> iter = arrows.iterator();

        while (iter.hasNext()) {
            int[] arrowDef = iter.next();
            // arrowDef[0] = nodeFrom
            // arrowDef[1] = nodeTo
            // arrowDef[2] = sendingRound
            // arrowDef[3] = receivingRound
            // arrowDef[4] = id
            // arrowDef[5] = color
            int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE;
            int x2 = LEFT_MARGIN + arrowDef[3] * INTER_CYCLES_SPACE;
            int y1 = TOP_MARGIN + arrowDef[0] * INTER_PROCESSES_SPACE;
            int y2 = TOP_MARGIN + arrowDef[1] * INTER_PROCESSES_SPACE;
            g2.setColor(Color.black);
            g2.drawString("m" + arrowDef[4], x1 + 3, y1 - 3);
            g2.setColor(colors[arrowDef[5]]);
            if (arrowDef[0] == arrowDef[1]) {
                // g2.drawArc(x1 /* +(INTER_CYCLES_SPACE / 2) */, y1
                // - (INTER_PROCESSES_SPACE / 2), INTER_CYCLES_SPACE,
                // INTER_PROCESSES_SPACE, 0, -180);
                g2.drawLine(x1, y1, (x1 + x2) / 2, y1 + INTER_PROCESSES_SPACE / 20);
                g2.drawLine((x1 + x2) / 2, y1 + INTER_PROCESSES_SPACE / 20, x2, y2);
            } else {
                g2.drawLine(x1, y1, x2, y2);
            }
        }    

        // Draw token circles
        Iterator<int[]> iter2 = circles.iterator();
        while (iter2.hasNext()) {
            int[] arrowDef = iter2.next();
            // arrowDef[0] = nodeFrom
            // arrowDef[1] = nodeTo
            // arrowDef[2] = sendingRound
            // arrowDef[3] = receivingRound
            // arrowDef[4] = id
            // arrowDef[5] = color
            int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE - 4;
            int x2 = x1 + 8;
            int y1 = TOP_MARGIN + arrowDef[0] * INTER_PROCESSES_SPACE - 4;
            int y2 = y1 + 8;
            g2.setColor(Color.blue);
            g2.drawLine(x1, y1, x2, y2);
            g2.drawLine(x2, y1, x1, y2);
            //break;
        }
        // final boolean UNIFORM = true;
        final boolean UNIFORM = Configuration.getBoolean("simulation.uniform");

        if (UNIFORM) {
            // Draw acks
            Iterator<int[]> iter3 = acks.iterator();
            while (iter3.hasNext()) {
                int[] arrowDef = iter3.next();
                // arrowDef[0] = nodeFrom
                // arrowDef[1] = nodeTo
                // arrowDef[2] = sendingRound
                // arrowDef[3] = receivingRound
                // arrowDef[4] = id
                // arrowDef[5] = color

                //System.out.println(arrowDef[4]);
                //int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE + 10 + networkSize*(arrowDef[3] - arrowDef[2]) * INTER_CYCLES_SPACE ;
                int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE + 10 + (arrowDef[3] - arrowDef[2]) * INTER_CYCLES_SPACE ;
                //System.out.println(arrowDef[3] - arrowDef[2]);
                int x2 = x1 + 7;
                int y1 = TOP_MARGIN + arrowDef[0] * INTER_PROCESSES_SPACE - 4;
                int y2 = y1 + 8;


                g2.setColor(colors[arrowDef[5]]);
                g2.drawString("DELIVER m" + arrowDef[4], x1 -2 , y1 + 24);    

                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x1+20, y2-25);
                //break;
            }      
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, d.width, d.height);
        drawDemo(d.width, d.height, g2);
    }

    public static void activate() {
        activated = true;
    }

    public static void addArrow(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            arrows.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                    color});
        }
    }

    public static void addCircle(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            circles.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                    color});
        }
    }  

    public static void addAck(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            acks.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                    color});
        }
    } 

    public static void display() {
        if (activated) {
            TimeDiagram td = new TimeDiagram();
            td.init();
            JFrame f = new JFrame("Time Diagram");
            f.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            f.getContentPane().add(td);
            f.pack();
            f.setSize(new Dimension(LEFT_MARGIN + Simulator.getCycle()
                    * INTER_CYCLES_SPACE + 10, TOP_MARGIN + Network.size()
                    * INTER_PROCESSES_SPACE));
            f.setVisible(true);
        }
    }

    public static int chooseColor() {
        currentColor = (currentColor + 1) % NB_COLORS;
        return currentColor;
    }
}
