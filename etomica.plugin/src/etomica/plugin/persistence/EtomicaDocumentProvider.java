/*
 * Created on Apr 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.persistence;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtomicaDocumentProvider implements IDocumentProvider {

	/**
	 * 
	 */
	public EtomicaDocumentProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#connect(java.lang.Object)
	 */
	public void connect(Object element) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#disconnect(java.lang.Object)
	 */
	public void disconnect(Object element) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getDocument(java.lang.Object)
	 */
	public IDocument getDocument(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#resetDocument(java.lang.Object)
	 */
	public void resetDocument(Object element) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#saveDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.Object, org.eclipse.jface.text.IDocument, boolean)
	 */
	public void saveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getModificationStamp(java.lang.Object)
	 */
	public long getModificationStamp(Object element) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getSynchronizationStamp(java.lang.Object)
	 */
	public long getSynchronizationStamp(Object element) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#isDeleted(java.lang.Object)
	 */
	public boolean isDeleted(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#mustSaveDocument(java.lang.Object)
	 */
	public boolean mustSaveDocument(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#canSaveDocument(java.lang.Object)
	 */
	public boolean canSaveDocument(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#getAnnotationModel(java.lang.Object)
	 */
	public IAnnotationModel getAnnotationModel(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#aboutToChange(java.lang.Object)
	 */
	public void aboutToChange(Object element) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#changed(java.lang.Object)
	 */
	public void changed(Object element) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#addElementStateListener(org.eclipse.ui.texteditor.IElementStateListener)
	 */
	public void addElementStateListener(IElementStateListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IDocumentProvider#removeElementStateListener(org.eclipse.ui.texteditor.IElementStateListener)
	 */
	public void removeElementStateListener(IElementStateListener listener) {
		// TODO Auto-generated method stub
		
	}

}
