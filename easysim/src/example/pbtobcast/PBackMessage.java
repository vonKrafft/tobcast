package example.pbtobcast;

import easysim.core.Message;

public class PBackMessage extends Message {
        public int id_src = -1;
        
        public PBackMessage(int id_src) {
            this.id_src = id_src;
        }
}
