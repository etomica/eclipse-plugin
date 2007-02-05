/**
 * 
 */
package etomica.plugin.wrappers;

import etomica.util.EnumeratedType;

public class EtomicaStatus {
    public EtomicaStatus(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    public final String message;
    public final Type type;
    
    public static final Type OK = new Type("OK", 0);
    public static final Type WARNING = new Type("Warning", 1);
    public static final Type ERROR = new Type("Error", 2);

    public static final EtomicaStatus PEACHY = new EtomicaStatus("",OK);

    public static class Type extends EnumeratedType {
        public final int severity;
        
        public Type(String label, int severity) {
            super(label);
            this.severity = severity;
        }
    }

}