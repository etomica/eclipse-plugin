/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.ide;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Display;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlatformMain extends WorkbenchAdvisor 
implements IPlatformRunnable {
	public String getInitialWindowPerspectiveId() {
		return "etomica.ide.perspective.EtomicaRun";
	}
	public void preWindowOpen( IWorkbenchWindowConfigurer configurer )
	{
		configurer.setShowMenuBar( false );
		configurer.setShowFastViewBars(false); 
		configurer.setShowStatusLine(false);
		configurer.setShowCoolBar( false );
	}
	public Object run(Object args) throws Exception {
		Display d = PlatformUI.createDisplay();
		int ret = PlatformUI.createAndRunWorkbench(d,this);
		if ( ret==PlatformUI.RETURN_RESTART)
			return EXIT_RESTART;
		return EXIT_OK;
	}

}
