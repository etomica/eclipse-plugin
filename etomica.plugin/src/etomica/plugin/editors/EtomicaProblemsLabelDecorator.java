package etomica.plugin.editors;

import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;

import etomica.plugin.wrappers.EtomicaStatus;
import etomica.plugin.wrappers.PropertySourceWrapper;

public class EtomicaProblemsLabelDecorator extends ProblemsLabelDecorator {

    public EtomicaProblemsLabelDecorator() {
        super();
    }

    protected int computeAdornmentFlags(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            EtomicaStatus status = ((PropertySourceWrapper)obj).getStatus();
            if (status.type == EtomicaStatus.OK) {
                return 0;
            }
            else if (status.type == EtomicaStatus.WARNING) {
                return ERRORTICK_WARNING;
            }
            else { // ERROR
                return ERRORTICK_ERROR;
            }
        }                        
        return 0;
    }

    private static final int ERRORTICK_WARNING = JavaElementImageDescriptor.WARNING;
    private static final int ERRORTICK_ERROR = JavaElementImageDescriptor.ERROR; 
}
