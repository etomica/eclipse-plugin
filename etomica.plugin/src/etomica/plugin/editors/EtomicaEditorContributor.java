/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.editors;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

import etomica.plugin.actions.ResumeSimulationAction;
import etomica.plugin.actions.RunSimulationAction;
import etomica.plugin.actions.SuspendSimulationAction;
import etomica.plugin.actions.TerminateSimulationAction;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtomicaEditorContributor extends EditorActionBarContributor {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToCoolBar(org.eclipse.jface.action.ICoolBarManager)
	 */
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		// TODO Auto-generated method stub
		super.contributeToCoolBar(coolBarManager);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menuManager) {
		// TODO Auto-generated method stub
		super.contributeToMenu(menuManager);
		//menuManager.add( new RunSimulationAction() );
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		// TODO Auto-generated method stub
		super.contributeToStatusLine(statusLineManager);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		// TODO Auto-generated method stub
		super.contributeToToolBar(toolBarManager);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionBarContributor#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#getActionBars()
	 */
	public IActionBars getActionBars() {
		// TODO Auto-generated method stub
		return super.getActionBars();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#getPage()
	 */
	public IWorkbenchPage getPage() {
		// TODO Auto-generated method stub
		return super.getPage();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionBarContributor#init(org.eclipse.ui.IActionBars, org.eclipse.ui.IWorkbenchPage)
	 */
	public void init(IActionBars bars, IWorkbenchPage page) {
		// TODO Auto-generated method stub
		super.init(bars, page);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#init(org.eclipse.ui.IActionBars)
	 */
	public void init(IActionBars bars) {
		// TODO Auto-generated method stub
		super.init(bars);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IEditorPart targetEditor) {
		editor = (EtomicaEditor) targetEditor;
		if ( editor!=null)
			super.setActiveEditor(targetEditor);
		else
			super.setActiveEditor( null );
	}
	/**
	 * 
	 */
	public EtomicaEditorContributor() {
		super();
		// TODO Auto-generated constructor stub
	}
	EtomicaEditor editor;
	private RunSimulationAction run;
	private ResumeSimulationAction resume;
	private SuspendSimulationAction suspend;
	private TerminateSimulationAction terminate;

}
