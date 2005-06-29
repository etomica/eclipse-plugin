package etomica.plugin;

import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.net.URL;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import etomica.PotentialMaster;
import etomica.Space;
import etomica.Species;

/**
 * @author Henrique
 * 
 * Searches for a class in the classpath environment. This class is useful for
 * searching duplicate classes in the classpath which might cause conflicts in
 * the ClassLoader. It can also be used to search an entire directory system. To
 * do so, add the directory to be searched to the classpath.
 */
public class ClassDiscovery {

	public ClassDiscovery() {
	}

	public void addClass(Class aclass) {
		if (map.get(aclass) == null) {
			map.put(aclass, new ArrayList());
			dirty = true;
		}
	}

	public ArrayList queryWhoExtends(Class aclass) {
		if (dirty)
			searchClassPath();
		return (ArrayList) map.get(aclass);
	}

	public Map getMap() {
		return map;
	}
	/**
	 * Builds the internal table of classnames. names should contain a list of
	 * classes like "etomica.Simulation". It returns a map classname->list of
	 * extenders (string)
	 */
public void searchClassPath() {

		// Get our CLASSPATH
		org.eclipse.ui.PlatformUI ui;
		
		// A container for the path list
		ArrayList paths = new ArrayList();
		
		// Add items from this plugin's class path
		Bundle bundle = EtomicaPlugin.getDefault().getBundle();
		String plugin_root = bundle.getLocation();
		//System.out.println( "Plugin root: " + plugin_root.toString() );
		Enumeration enum = EtomicaPlugin.getDefault().getBundle().getEntryPaths( "/" );
		while( enum.hasMoreElements() )
		{
			String entry = (String) enum.nextElement();
			if ( entry.endsWith( "/") ) 
				continue;
			
			//IPath path = plugin_root.append( entry );
			URL url = bundle.getEntry( entry );
			try {
				URL resolved_url = Platform.resolve( url );
				if ( resolved_url.getProtocol()=="file" )
				{
					String result = resolved_url.getPath();//.toString();
					//if ( result.startsWith( "/") )
					//	result = result.substring( 1 );
					paths.add( result ); 					
				}
			} catch ( IOException e )
			{
				System.out.println( "URL not resolved: " + url.toString() ); 
			}
		}
		
		// Add items from Java class path
		String cpath = System.getProperty("java.class.path");	      
	    StringTokenizer tz = new StringTokenizer(cpath, PATHSEP);
	    while(tz.hasMoreTokens()) {
	      		paths.add(tz.nextToken());
	    }
	      
	    for(int i = 0; i < paths.size(); i++) {
	    	File file = new File((String) paths.get(i));
	    	if(file.isDirectory()) {
	    		processDirectory(file,file.getAbsolutePath());
	    	}
	    	else {
	    		processJar(file,file.getAbsolutePath());
	    	}
	    }
	    dirty = false;
	}
	/**
	 * Recursively store all class names found in this directory and its
	 * subdirectories
	 */
	private void processDirectory(File directory, String basepath) {
		//System.out.println("Processing " + directory.getPath());
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File currentFile = files[i];
			String name = currentFile.getName();

			if (currentFile.isDirectory()) {
				processDirectory(currentFile, basepath);
			} else if (name.endsWith(".class")) {
				add(name, currentFile.getPath(), basepath);
			} else if (name.endsWith(".jar")) {
				processJar(currentFile, basepath);
			}
		}
	}

	/**
	 * Store all class names found in this jar file
	 */
	private void processJar(File jarfile, String basepath) {
		//System.out.println("Processing JAR " + jarfile.getPath());
		try {
			JarInputStream jarIS = new JarInputStream(new FileInputStream(
					jarfile));

			JarEntry entry = null;
			while ((entry = jarIS.getNextJarEntry()) != null) {
				String name = entry.getName();
				if (name.endsWith(".class")) {
					//System.out.println( entry.getAttributes().toString() );
					add(name, jarfile.getPath(), basepath);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Adds a key to the table
	 */
	private void add(String origname, String path, String basepath) {

		String name = origname;
		if (path.startsWith(basepath)) {
			try {
				name = path.substring(basepath.length() + 1);
			} catch (Exception e) {
			}
		}

		if (!name.startsWith("etomica") || name.indexOf('$') >= 0)
			return;

		// Get class object
		name = name.replace('\\', '.');
		name = name.replace('/', '.');
		if (name.endsWith(".class")) {
			name = name.substring(0, name.length() - 6);
		}
		Class thisclass = null;
		try {
			//System.err.println( "Trying class " + name );
			thisclass = Class.forName(name);
		} catch (java.lang.ClassFormatError e) {
			System.out.println("Could not access class " + name + ": "
					+ e.getLocalizedMessage());
			return;
		} catch (java.lang.VerifyError e) {
			System.out.println("Could not access class " + name + ": "
					+ e.getLocalizedMessage());
			return;
		} catch (java.lang.NoClassDefFoundError e) {
			System.out.println("Could not access class " + name + ": "
					+ e.getLocalizedMessage());
			return;
		} catch (Exception e) {
			System.out.println("Could not access class " + name + ": "
					+ e.getLocalizedMessage());
			return;
		} catch (ExceptionInInitializerError e) {
			System.out.println("Could not access class " + name + ": "
					+ e.getLocalizedMessage());
			return;
		}

		// Test it against all other classes
		Iterator ikey = map.keySet().iterator();
		boolean found = false;
		while (ikey.hasNext()) {
			Class baseclass = (Class) ikey.next();

			if (baseclass.isAssignableFrom(thisclass)) {
				if (baseclass.equals(thisclass)) {
					//System.out.println("...disconsidering "+ thisclass.getName() + " as an implementation of "+ baseclass.getName()+ " because they are the same");
					continue;
				}
				/*
				 * else if ( !thisclass.isInterface() ) { System.out.println(
				 * "...disconsidering " + thisclass.getName() + " as an
				 * implementation of " + baseclass.getName() + " because it is
				 * an interface"); continue; }
				 */
				ArrayList list = (ArrayList) map.get(baseclass);
				list.add(thisclass);
				//System.err.println( "OK Added class " + thisclass.getName() +
				// " --> " + baseclass.getName() );
				found = true;
			}
		}
		//if (!found)
			//System.err.println("No matches found for class "
				//	+ thisclass.getName());

	}

	public static void main(String[] args) {

		ClassDiscovery cd = new ClassDiscovery();
		cd.addClass(Space.class);
		cd.addClass(Space.class);
		cd.addClass(Species.class);
		cd.addClass(etomica.Potential.class);
		cd.addClass(etomica.potential.Potential1.class);
		cd.addClass(etomica.potential.Potential2.class);
		cd.addClass(etomica.Integrator.class);
		cd.addClass(etomica.Phase.class);
		cd.addClass(etomica.Controller.class);
		cd.addClass(etomica.DataSource.class);
		cd.addClass(etomica.graphics.Display.class);
		cd.addClass(etomica.Action.class);
		cd.addClass(etomica.Activity.class);
		cd.addClass(etomica.Simulation.class);
		cd.addClass(PotentialMaster.class);
		cd.addClass(etomica.graphics.Device.class);
		cd.searchClassPath();
		Map map = cd.getMap(); //.getImplementations( Space.class );
		Iterator ikey = map.keySet().iterator();
		while (ikey.hasNext()) {
			Class key = (Class) ikey.next();
			System.out.println("============== Class " + key.getName());
			Iterator ilist = ((Collection) map.get(key)).iterator();
			while (ilist.hasNext()) {
				String value = ((Class) ilist.next()).getName();
				System.out.println("     " + value);
			}
		}
	}

	private HashMap			map		= new HashMap();						// String
																				  // ->
																				  // ArrayList
	private boolean			dirty	= false;
	private static String	FILESEP	= System.getProperty("file.separator");
	private static String	PATHSEP	= System.getProperty("path.separator");
}