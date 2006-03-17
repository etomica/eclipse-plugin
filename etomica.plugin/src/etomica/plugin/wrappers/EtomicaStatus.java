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
    
    public static final Type OK = new Type("OK");
    public static final Type WARNING = new Type("Warning");
    public static final Type ERROR = new Type("Error");

    public static final EtomicaStatus PEACHY = new EtomicaStatus("",OK);

    public static class Type extends EnumeratedType {
        public Type(String label) {
            super(label);
        }

    }

}