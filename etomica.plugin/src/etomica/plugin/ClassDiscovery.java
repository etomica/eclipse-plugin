package etomica.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import etomica.potential.PotentialMaster;
import etomica.space.Space;
import etomica.species.Species;

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
    
    /**
     * Utility method to chop the package names off the full
     * Class name (etomica.simulation.Simulation => Simulation)
     */
    public static String chopClassName(String className) {
        int i = className.lastIndexOf(".");
        if (i == -1) {
            return className;
        }
        return className.substring(i+1);
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
        // A container for the path list
        ArrayList paths = new ArrayList();

        // Add items from this plugin's class path
        Bundle bundle = EtomicaPlugin.getDefault().getBundle();
        //System.out.println( "Plugin root: " + bundle.getLocation().toString() );
        Enumeration enums = EtomicaPlugin.getDefault().getBundle().getEntryPaths( "/" );
        while( enums.hasMoreElements() )
        {
		    String entry = (String) enums.nextElement();
		    if (entry.endsWith( "/") && !entry.equals("/bin/")) 
		        continue;

		    //IPath path = plugin_root.append( entry );
		    URL url = bundle.getEntry( entry );
		    try {
		        URL resolved_url = FileLocator.resolve( url );
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
        System.out.println( "From Etomica plugin: java.class.path = " + cpath );
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
        
        if (name.startsWith("etomica.graphics") || name.startsWith("etomica.junit"))
            return;

        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - 6);
        }
        Class thisClass = null;
        try {
            //System.err.println( "Trying class " + name );
            thisClass = Class.forName(name);
        }
        catch (java.lang.ClassFormatError e) {
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
        if ((thisClass.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0) {
            return;
        }

        // Test it against all other classes
        Iterator ikey = map.keySet().iterator();
        while (ikey.hasNext()) {
            Class baseClass = (Class) ikey.next();

            if (baseClass.isAssignableFrom(thisClass)) {
                /*
                 * else if ( !thisclass.isInterface() ) { System.out.println(
                 * "...disconsidering " + thisclass.getName() + " as an
                 * implementation of " + baseclass.getName() + " because it is
                 * an interface"); continue; }
                 */
                ((ArrayList)map.get(baseClass)).add(thisClass);
                //System.err.println( "OK Added class " + thisclass.getName() +
                // " --> " + baseclass.getName() );
            }
        }
        
	}

	public static void main(String[] args) {

		ClassDiscovery cd = new ClassDiscovery();
		cd.addClass(Space.class);
		cd.addClass(Space.class);
		cd.addClass(Species.class);
		cd.addClass(etomica.potential.Potential.class);
		cd.addClass(etomica.potential.Potential1.class);
		cd.addClass(etomica.potential.Potential2.class);
		cd.addClass(etomica.integrator.Integrator.class);
		cd.addClass(etomica.phase.Phase.class);
		cd.addClass(etomica.action.activity.Controller.class);
		cd.addClass(etomica.data.DataSource.class);
		cd.addClass(etomica.graphics.Display.class);
		cd.addClass(etomica.action.Action.class);
		cd.addClass(etomica.action.Activity.class);
		cd.addClass(etomica.simulation.Simulation.class);
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
	private static String	PATHSEP	= System.getProperty("path.separator");
}
