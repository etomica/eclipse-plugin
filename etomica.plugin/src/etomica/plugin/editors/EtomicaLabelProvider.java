package etomica.plugin.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import etomica.plugin.EtomicaPlugin;

public class EtomicaLabelProvider extends LabelProvider {

    public EtomicaLabelProvider() {
        super();
        registry = new ImageDescriptorRegistry();
    }
    
    public Image getImage(Object element) {
        return registry.get(simple);
    }

    private final ImageDescriptorRegistry registry;
    private static final ImageDescriptor simple = ImageDescriptor.createFromFile(EtomicaPlugin.class, "icons/public_co.gif");
}
