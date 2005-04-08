/*
 * Created on Apr 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.ide;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

/**
 * @author Henrique Bucher
 * @date 04/06/2005
 */
public class PerspectiveFactory implements IPerspectiveFactory {

    private static final String SIMULATION_VIEW_ID = 
    	"etomica.ide.ui.simulationview.SimulationView";
    private static final String SPECIES_VIEW_ID = 
    	"etomica.ide.ui.speciesview.SpeciesView";
    private static final String CONFIGURATION_VIEW_ID = 
    	"etomica.ide.ui.configurationview.ConfigurationView";
    

    /* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) 
	{
		String editorArea = layout.getEditorArea();
		
		layout.addView( SIMULATION_VIEW_ID, IPageLayout.LEFT, 0.25f, editorArea );
		IFolderLayout bottom = layout.createFolder( "bottom", IPageLayout.BOTTOM, 0.66f, editorArea );
		bottom.addView( SPECIES_VIEW_ID );
		
		layout.addView( CONFIGURATION_VIEW_ID, IPageLayout.RIGHT, 0.75f, editorArea ); //, IPageLayout.TOP, 0.75f, editorArea );
		//center.addView( CONFIGURATION_VIEW_ID );
		//bottom.addView( IPageLayout.ID_TASK_LIST );
		//bottom.addPlaceholder( IPageLayout.ID_PROBLEM_VIEW );
		
		//layout.addActionSet( "")
		
		//layout.addActionSet( )
        
	}

}
