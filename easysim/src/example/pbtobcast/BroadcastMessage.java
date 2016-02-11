package example.pbtobcast;

import easysim.core.Message;

public class BroadcastMessage extends Message {
    
        public static final String TYPE_REQ = "req"; 
        public static final String TYPE_ACK = "ack";
        public static final String TYPE_MSG = "msg";
    
	public Boolean broadcasted = false;
        public Integer id_src      = -1;
        public String  seq         = "";
        public String  type        = null;
        
        public BroadcastMessage(Integer seq_num, String type, Integer id_src) {
            this.seq    = id_src+"-"+seq_num;
            this.type   = type;
            this.id_src = id_src;
        }
        
        public Boolean isType(String type) {
            return (this.type == null ? type == null : this.type.equals(type));
        }
}
