package example.pbtobcast;

import easysim.core.Message;

public class PBreqMessage extends Message {
        public int id_src = -1;
        
        public PBreqMessage(int id_src) {
            this.id_src = id_src;
        }
}
