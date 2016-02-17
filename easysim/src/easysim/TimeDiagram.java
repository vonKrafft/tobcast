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
import easysim.core.Message;
import easysim.core.Network;
import easysim.core.Node;

public class TimeDiagram extends JApplet {

    private static final int INTER_PROCESSES_SPACE = 80;

    private static final int INTER_CYCLES_SPACE = 80;

    private static final int TOP_MARGIN = 40;

    private static final int LEFT_MARGIN = 40;

    // List of arrows
    private static List<int[]> arrows = new ArrayList<int[]>();

    private static List<int[]> circles = new ArrayList<int[]>();

    private static List<int[]> acks = new ArrayList<int[]>();
    private static final long serialVersionUID = 1L;

    private static boolean activated = false;

    public static int NB_COLORS = 12;

    private static int currentColor = 0;

    Color[] colors = new Color[]{Color.black,
        Color.blue, Color.cyan, Color.darkGray, Color.green, Color.gray,
        Color.magenta, Color.lightGray, Color.orange, Color.pink, Color.red,
        Color.yellow};

    @Override
    public void init() {
        setBackground(Color.white);
    }

    public void drawDemo(int w, int h, Graphics2D g2) {
        int networkSize = Configuration.getInt("network.size");
        g2.setColor(Color.black);

        // Draw process lines
        {
            int x1 = LEFT_MARGIN;
            int x2 = (int) (x1 + INTER_CYCLES_SPACE * Simulator.getCycle());
            for (int i = 0; i < Network.size(); i++) {
                int y = INTER_PROCESSES_SPACE * i + TOP_MARGIN;
                g2.drawString("p" + i, 10, y);
                g2.drawLine(LEFT_MARGIN, y, x2, y);
            }
        }

        // Draw cycle lines
        {
            int y1 = TOP_MARGIN;
            int y2 = y1 + INTER_PROCESSES_SPACE * (Network.size() - 1);
            for (int i = 0; i < Simulator.getCycle() + 1; i++) {
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
            // arrowDef[4] = (relative) id
            // arrowDef[5] = color
            // arrowDef[6] = Message type
            // Rest depends on message type
            int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE;
            int x2 = LEFT_MARGIN + arrowDef[3] * INTER_CYCLES_SPACE;
            int y1 = TOP_MARGIN + arrowDef[0] * INTER_PROCESSES_SPACE;
            int y2 = TOP_MARGIN + arrowDef[1] * INTER_PROCESSES_SPACE;
            //g2.setColor(Color.black);
            g2.setColor(colors[arrowDef[5]]);
            
            // FORMAT : TYPE(SENDINGNODE, (RELATIVE)ID, SEQUENCENUMBER)
            // EXEMPLE: D(0,3,4) (Troisième message envoyé par node 0 avec seqNb = 4)
            switch (arrowDef[6]) {
                case Message.TYPE.BBSEQUENCE:
                    g2.drawString(messageTypeToString(arrowDef[6]) + "(" + arrowDef[7] + "," + arrowDef[8] + "," + arrowDef[9] + ")", x1 - 40, y1 - 5);
                    break;
                case Message.TYPE.ACK:
                case Message.TYPE.DATA:
                    g2.drawString(messageTypeToString(arrowDef[6]) + "(" + arrowDef[0] + "," + arrowDef[4] + "," + (arrowDef[7] == -1 ? "-" : arrowDef[7]) + ")", x1 - 40, y1 - 5);
                    break;
                case Message.TYPE.REQ:
                case Message.TYPE.UNDEFINED:
                default:
                    g2.drawString(messageTypeToString(arrowDef[6]) + "(" + arrowDef[0] + "," + arrowDef[4] + ",-)", x1 - 40, y1 - 5);
            }
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
            // arrowDef[6] = Message type
            // Rest depends on message type
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
                // arrowDef[6] = Message type
                // Rest depends on message type

                //System.out.println(arrowDef[4]);
                //int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE + 10 + networkSize*(arrowDef[3] - arrowDef[2]) * INTER_CYCLES_SPACE ;
                int x1 = LEFT_MARGIN + arrowDef[2] * INTER_CYCLES_SPACE + 10 + (arrowDef[3] - arrowDef[2]) * INTER_CYCLES_SPACE;
                //System.out.println(arrowDef[3] - arrowDef[2]);
                int x2 = x1 + 7;
                int y1 = TOP_MARGIN + arrowDef[1] * INTER_PROCESSES_SPACE - 4;
                int y2 = y1 + 8;

                g2.setColor(colors[arrowDef[5]]);
                switch (arrowDef[6]) {
                    case Message.TYPE.DATA:
                        g2.drawString("DLV (" + arrowDef[0] + "," + arrowDef[4] + "," + arrowDef[7] + ")", x1 - 2, y1 + 17);
                        break;
                    case Message.TYPE.ACK:
                    case Message.TYPE.UNDEFINED:
                    default:
                        y1 = TOP_MARGIN + arrowDef[0] * INTER_PROCESSES_SPACE - 4;
                        y2 = y1 + 8;
                        g2.drawString("DLV m" + arrowDef[4], x1 - 2, y1 +17);
                }

                /*
                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x1+20, y2-25);
                 */
                //break;
            }
        }
    }

    private String messageTypeToString(int mt) {
        switch (mt) {
            case Message.TYPE.ACK:
                return "Ack";
            case Message.TYPE.BBSEQUENCE:
                return "S";
            case Message.TYPE.DATA:
                return "D";
            case Message.TYPE.REQ:
                return "Req";
            case Message.TYPE.UNDEFINED:
            default:
                return "U";
        }
    }

    @Override
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

    private static int[] constructArrayDef(Message m, Node receiver) {
        int[] arrayDef;
        switch (m.getType()) {
            case Message.TYPE.ACK:
            case Message.TYPE.DATA:
                arrayDef = new int[]{m.sendingNode, receiver.id, m.sendingCycle,
                    Simulator.getCycle(), m.getRelId(), m.color, m.getType(), m.getSeqNb()};
                break;
            case Message.TYPE.BBSEQUENCE:
                example.bbtobcast.SequenceMessage sm = (example.bbtobcast.SequenceMessage) m;
                arrayDef = new int[]{sm.sendingNode, receiver.id, sm.sendingCycle,
                    Simulator.getCycle(), sm.getRelId(), 5, sm.getType(), sm.getToFindNodeFrom(),
                    sm.getToFindRelId(), sm.getToAssociateSeqNb()}; // 5 = Grey (GG guys)
                break;
            case Message.TYPE.UNDEFINED:
            case Message.TYPE.REQ:
            default:
                arrayDef = new int[]{m.sendingNode, receiver.id, m.sendingCycle,
                    Simulator.getCycle(), m.getRelId(), m.color, m.getType()};
        }
        return arrayDef;
    }

    public static void addArrow(Message m, Node receiver) {
        if (activated) {

            arrows.add(constructArrayDef(m, receiver));
        }
    }

    @Deprecated
    public static void addArrow(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            arrows.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                color, Message.TYPE.UNDEFINED});
        }
    }

    public static void addCircle(Message m, Node receiver) {
        if (activated) {
            circles.add(constructArrayDef(m, receiver));
        }
    }

    @Deprecated
    public static void addCircle(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            circles.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                color, Message.TYPE.UNDEFINED});
        }
    }

    public static void addAck(Message m, Node receiver) {
        if (activated) {
            acks.add(constructArrayDef(m, receiver));
        }
    }

    @Deprecated
    public static void addAck(int nodeFrom, int nodeTo, int sendingRound,
            int receivingRound, int id, int color) {
        if (activated) {
            acks.add(new int[]{nodeFrom, nodeTo, sendingRound, receivingRound, id,
                color, Message.TYPE.UNDEFINED});
        }
    }

    public static void display() {
        if (activated) {
            TimeDiagram td = new TimeDiagram();
            td.init();
            JFrame f = new JFrame("Time Diagram");
            f.addWindowListener(new WindowAdapter() {
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
