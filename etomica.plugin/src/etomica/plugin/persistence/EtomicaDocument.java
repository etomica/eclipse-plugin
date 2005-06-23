/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.persistence;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtomicaDocument implements IPersistableElement {

	/**
	 * 
	 */
	public EtomicaDocument() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	public String getFactoryId() {
		// TODO Auto-generated method stub
		return "etomica.plugin.persistence.EtomicaDocument";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistableElement#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub

	}

}
