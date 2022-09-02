package pri;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class FolderWalker {
	
	public class FileItem {
		
		public String name; // name with extension
		public String basename; // name without extension
		public String ext;
		public String folder;
		public String fullpath;		
	}
	
	public class WalkResult {
				
		public FolderWalker backref = null;		
		public List<FileItem> files = new ArrayList<FileItem>();
	}	
	
	// walk recursively
    public void listSource( String path, WalkResult r ) {
    	
        File root = new File( path );
        File[] list = root.listFiles();

        if( list == null ) {
        	return;
        }

        for( File f : list ) {
        	
        	String name = f.getName();
        	
            if( f.isDirectory() ) {
            	
            	// perform custom exclusions about folders .....
            	
            	// excluding these folders
            	if(( 
        			   name.equalsIgnoreCase("msvc10")
        			|| name.equalsIgnoreCase("gmake3")
        			|| name.equalsIgnoreCase("codebase")
        			|| name.equalsIgnoreCase("expreval")        			
        			|| name.equalsIgnoreCase("obj") // this folder contained linux compilated code
           		)) {
            		
            		continue;
            	}
            	
                System.out.println( "considering folder : " + f.getAbsoluteFile() );                
                listSource( f.getAbsolutePath(), r );

            }
            else {
            	
            	// excluding these files 

            	if(( 
         			   name.equalsIgnoreCase("cJSON.c")
            		)) {
             		
             		continue;
             	}            	
                
            	String ext = FilenameUtils.getExtension(f.getName());
            	
            	// only some type
            	if( ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase("cpp")) {
            		// ok for these type
            	}
            	else if ( ext.equalsIgnoreCase("pc") ) {            		
            		// ok for these type
            	}
            	else {

            		continue;
            	}
            	
            	FileItem fi = new FileItem();
            	fi.ext = ext; 
            	fi.name = name;
            	fi.basename = FilenameUtils.getBaseName(f.getName());
            	fi.folder = path;
            	fi.fullpath = f.getAbsolutePath();
            	
            	// System.out.format("%s : %s \n", f.getAbsoluteFile(), ext );

            	r.files.add( fi );
            }
        }
        
        return;
    }
    
    public void listProject( String path, WalkResult r ) {
    	
        File root = new File( path );
        File[] list = root.listFiles();

        if( list == null ) {
        	return;
        }

        for( File f : list ) {
        	
        	String name = f.getName();
        	
            if( f.isDirectory() ) {

                // System.out.println( "considering folder : " + f.getAbsoluteFile() );                
            	listProject( f.getAbsolutePath(), r );
            }
            else {

            	// System.out.println( "considering file : " + f.getAbsoluteFile() );
            	
            	String ext = FilenameUtils.getExtension(f.getName());
            	
            	// only some type            	
            	
            	if( ext.equalsIgnoreCase("vcxproj") ) {
            		// ok for these type
            	}
            	else if ( name.equalsIgnoreCase("makefile") ) {            		
            		// ok for these type
            	}
            	else {
            		continue;
            	}
            	
            	FileItem fi = new FileItem();
            	fi.ext = ext; 
            	fi.name = name;
            	fi.basename = FilenameUtils.getBaseName(f.getName());
            	fi.folder = path;
            	fi.fullpath = f.getAbsolutePath();
            	
            	System.out.format("%s\n", fi.fullpath);

            	r.files.add( fi );
            }
        }
        
        return;
    }    
    
    public void listVCXPROJ( String path, WalkResult r ) {
    	
        File root = new File( path );
        File[] list = root.listFiles();

        if( list == null ) {
        	return;
        }

        for( File f : list ) {
        	
        	String name = f.getName();
        	
            if( f.isDirectory() ) {
            	            	
                // System.out.println( "considering folder : " + f.getAbsoluteFile() );                
            	listVCXPROJ( f.getAbsolutePath(), r );
            }
            else {

            	// System.out.println( "considering file : " + f.getAbsoluteFile() );
            	
            	String ext = FilenameUtils.getExtension(f.getName());
            	
            	// only some type            	
            	
            	if( ext.equalsIgnoreCase("vcxproj") ) {
            		// ok for these type
            	}
            	else {
            		continue;
            	}
            	
            	FileItem fi = new FileItem();
            	fi.ext = ext; 
            	fi.name = name;
            	fi.basename = FilenameUtils.getBaseName(f.getName());
            	fi.folder = path;
            	fi.fullpath = f.getAbsolutePath();
            	
            	System.out.format("%s\n", fi.fullpath);

            	r.files.add( fi );
            }
        }
        
        return;
    }
    
}	
