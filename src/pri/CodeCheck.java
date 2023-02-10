package pri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import pri.FileParser.UseOfFunction;
import pri.FolderWalker.FileItem;

public class CodeCheck {

	static FolderWalker.WalkResult compilingSourcesPath( String srcFolder, BufferedWriter writer ) {
		
		FolderWalker fw = new FolderWalker();
		FolderWalker.WalkResult fwr = fw.new WalkResult();
		fwr.backref = fw;
		fw.listSource( srcFolder, fwr );
		
		// epurate results 
		
		List<FolderWalker.FileItem> fileItemsFiltered = new ArrayList<>();

		for( FolderWalker.FileItem fileItem1 : fwr.files ) {

			File file1 = new File( fileItem1.fullpath );
			String ext1 = FilenameUtils.getExtension(file1.getName());
			String basename1 = FilenameUtils.getBaseName(file1.getName());
			String path1 = FilenameUtils.getPath(file1.getPath());	
			
			boolean skip = false;
			
			for( FolderWalker.FileItem fileItem2 : fwr.files ) {

				File file2 = new File( fileItem2.fullpath );
				String ext2 = FilenameUtils.getExtension(file2.getName());
				String basename2 = FilenameUtils.getBaseName(file2.getName());
				String path2 = FilenameUtils.getPath(file2.getPath());
				
				// if, into the same folder
				// a file <name>.pc exist along with a <name>.cpp or <name>.c file then skip the .c or .cpp record (which is a PRO*C generated file)
				
				if( basename1.equals(basename2) && path1.equals( path2 ) && ext1.equals("pc") == false && ext2.equals("pc") ) {
					skip = true;
					break;
				}				
			}
			
			if( skip == false ) {
				fileItemsFiltered.add( fileItem1 );
			} else {				
				System.out.format("remove PRO*C generated %s%s.%s\n", path1, basename1, ext1 );				
			}
		}

		fwr.files = fileItemsFiltered;

		return fwr;
	}
	
	static FolderWalker.WalkResult compilingProjectPath( String baseFolder, BufferedWriter writer ) {
		
		FolderWalker fw = new FolderWalker();
		FolderWalker.WalkResult fwr = fw.new WalkResult();
		fwr.backref = fw;
				
		fw.listProject( baseFolder + "/msvc10" , fwr );
		fw.listProject( baseFolder + "/gmake3" , fwr );		

		return fwr;
	}	
		
	// ------- find unreferenced function ------------
		
	static boolean isUnrefForgiven( UseOfFunction uof ) {
		
		// tell if this function name is forgiven to be unreferenced
				
		if( uof.functionName.equals( "ConnectXR_compact" ))    { return true; }  // used from a DLL
		if( uof.functionName.equals( "daDonneArchT_compact" )) { return true; }  // used from a DLL
		if( uof.functionName.equals( "daDonneJourT_compact" )) { return true; }  // used from a DLL
		if( uof.functionName.equals( "GetLastQualityCode"   )) { return true; }  // used into unscanned expreval code
		if( uof.functionName.equals( "trt_erreur"           )) { return true; }  // used but where ???
				
		if( uof.functionType.equals("WINAPI") )   { return true; }  // used from a DLL
		if( uof.functionType.equals("DECLTYPE") ) { return true; }
		
		return false;
	}	
	
	static void analysingSourcesContent( String srcFolder, FolderWalker.WalkResult fwr, BufferedWriter writer ) throws IOException {
		
		String log;
		
		FileParser fp = new FileParser();
		FileParser.Result fpr = fp.new Result();
		
		fp.censusFunctionDeclaration( fwr.files, fpr, writer );
		
		Integer cppFilesCount = 0;
		Integer pcFilesCount = 0;
		Integer totalfilesCount = 0;
						
		for( FolderWalker.FileItem fileItem : fwr.files ) {
			
        	// only some type
        	if( fileItem.ext.equalsIgnoreCase("c") || fileItem.ext.equalsIgnoreCase("cpp")) {
        		cppFilesCount++;
        	}
        	else if ( fileItem.ext.equalsIgnoreCase("pc") ) {            		
        		pcFilesCount++;
        	}        	
        	totalfilesCount++;        	
		}		
		
		// add some sources to do the census of use only
		
		// statis.h content use of function pointers
    	FileItem fi = fwr.backref.new FileItem();
    	fi.ext = "h"; 
    	fi.name = "name";
    	fi.folder = srcFolder + "\\C\\calculs";
    	fi.fullpath = srcFolder + "\\C\\calculs\\statis.h";		
		fwr.files.add( fi );
				
		fp.censusFunctionUse( fwr.files, fpr, writer);
		
		Set<String> functionNames = fpr.useOfFunctions.keySet();
		
//		for( String functionName : functionNames ) {
//			
//			UseOfFunction uof = fpr.useOfFunctions.get( functionName );
//			System.out.format("function uses : %s {%d}\n", functionName, uof.useCount );
//		}		
				
		int unrefFxCount = 0;
		List<UseOfFunction> unrefs = new ArrayList<UseOfFunction>();
		
		for( String functionName : functionNames ) {
			
			UseOfFunction uof = fpr.useOfFunctions.get( functionName );
			if( uof.useCount == 0 ) {

				if( isUnrefForgiven( uof) ) {
					continue;
				}

				unrefs.add(uof);
				unrefFxCount++;
			}
		}
		
		// sort by name / line num
		
		// order span results using their spanType and the calibration span order type
		unrefs.sort(
			( UseOfFunction uof1, UseOfFunction uof2 ) -> {

				int compareTo = uof1.sourceProgBasedUrl.compareTo(uof2.sourceProgBasedUrl);
				if( compareTo != 0 ) {
					return compareTo;
				}

				if( uof1.sourceLineNum > uof2.sourceLineNum ) {
					return 1;
				}
				
				return -1;
			}
		);
		
		for( UseOfFunction uof : unrefs ) {
			
			log = String.format("Unreferenced function [%s@%s] declaration : %s(%d)\n", uof.functionType, uof.functionName, uof.sourceName, uof.sourceLineNum ); System.out.print(log); writer.write(log);			
		}
		
		log = String.format(" ====================================================\n" ); System.out.print(log); writer.write(log);		
		log = String.format(" total sources line count          : %d\n", fpr.sourceLineCount ); System.out.print(log); writer.write(log);
		log = String.format("       PC sources line count       : %d\n", fpr.pcFilesSourceLineCount ); System.out.print(log); writer.write(log);
		log = String.format("    C/CPP sources line count       : %d\n", fpr.cppFilesSourceLineCount ); System.out.print(log); writer.write(log);
		log = String.format(" total C/CPP/PC sources file count : %d\n", fwr.files.size() );	System.out.print(log); writer.write(log);
		log = String.format("       PC sources count            : %d\n", pcFilesCount ); System.out.print(log); writer.write(log);
		log = String.format("    C/CPP sources count            : %d\n", cppFilesCount ); System.out.print(log); writer.write(log);		
		log = String.format(" total function count              : %d\n", fpr.functionsCount ); System.out.print(log); writer.write(log);
		log = String.format(" Unreferenced functions count      : %d\n", unrefFxCount ); System.out.print(log); writer.write(log);		
	}
	
	static void searchUnreferencedFunctions( String progFolder, BufferedWriter writer ) throws IOException {

		// make the list of the source to analyze
		FolderWalker.WalkResult fwr = compilingSourcesPath( progFolder, writer );
		
		// perform the analyze		
		analysingSourcesContent( progFolder, fwr, writer );			
	}
	
	// ------- find unreferenced VCXPROJ -------------
	
	static void searchUnreferencedMvsc10Project( String progFolder, BufferedWriter writer ) throws IOException {
		
		FolderWalker fw = new FolderWalker();
		FolderWalker.WalkResult fwr = fw.new WalkResult();
		fwr.backref = fw;
				
		fw.listVCXPROJ( progFolder + "/msvc10" , fwr );
		
		System.out.format("--- begin ---\n" );		
		
		for( FolderWalker.FileItem vcxprojFileItem : fwr.files ) {
			
			String name = vcxprojFileItem.name;
			boolean founded = false; 
			
			// System.out.format("census use of %s\n", name );
			
			try( BufferedReader reader = new BufferedReader( new FileReader( progFolder + "/msvc10/DMS.sln" )) )
			{
				while( true ) {

					String line = reader.readLine();
					if( line == null ) { break; }
					
					int chpos = line.indexOf( name );
					if( chpos != -1 ) {
						
						// ok
						founded = true;
						continue;
					}
				}
			}
			
			if( founded == false ) {
				
				System.out.format("unreferenced vcxproj %s\n", vcxprojFileItem.fullpath );
				
			}
		}
		
		System.out.format("--- end ---\n" );
	}
	
	// ------- find unreferenced source file ------------
		
	static void searchUnreferencedSourceFile( String progFolder, BufferedWriter writer ) throws IOException {
		
		// make the list of the source to analyze
		FolderWalker.WalkResult fwr = compilingSourcesPath( progFolder, writer );
		
		// make the list of the project
		FolderWalker.WalkResult fwrp = compilingProjectPath( progFolder, writer );		
		
		// for each source file try to find a reference into at least one project file (makefile or vcxproj)
		
		System.out.format("--- begin ---\n" );
		
		for( FolderWalker.FileItem srcFileItem : fwr.files ) {

			boolean founded = false; 					
			
			//System.out.format("census use of %s (%s)\n", srcFileItem.name, srcFileItem.basename );
			
			for( FolderWalker.FileItem projFileItem : fwrp.files ) {
				
				// for each project file
				
				String pattern = srcFileItem.name;
				
				if( projFileItem.name.equals( "makefile")) {
					pattern = srcFileItem.basename + ".o";	
				}
				
				try( BufferedReader reader = new BufferedReader( new FileReader( projFileItem.fullpath )) )
				{
					while( true ) {

						String line = reader.readLine();
						if( line == null ) { break; }
						
						int chpos = line.indexOf( pattern );
						if( chpos != -1 ) {
							
							// ok
							founded = true;
							//System.out.format("founded into %s\n", projFileItem.fullpath );
							break;
						}
					}
				}
			}
			
			if( founded == false ) {
				
				System.out.format("**** unfounded source %s\n", srcFileItem.fullpath );				
			}
		}
		
		System.out.format("--- end ---\n" );
	}
	
	// -----------------------------
	
	public static void main( String[] args ) throws Exception {

        // String dateTag = EpochTool.convertToString( EpochTool.getNowEpoch(), EpochTool.Format.ISO8601);

		//String root= "D:\\GIT\\SRV65EE\\SRV";
		//String root= "D:\\GIT\\SRV65_LINUX";
		//String root= "D:\\GIT\\SRV-Oracle19";
		//String progFolder = root + "\\prog";
		
		String root= "D:\\GIT\\SAM66_DEV";
		String progFolder = root;
		
		String logfile   = root + "\\codeCheck." + EpochTool.getNowEpoch() + ".log";

		BufferedWriter writer = new BufferedWriter(new FileWriter(logfile));

		// ------- find unreferenced function ------------
		searchUnreferencedFunctions( progFolder, writer );
		
		// ------- find unreferenced VCXPROJ -------------
		//searchUnreferencedMvsc10Project( progFolder, writer );		
		
		// ------- find unreferenced source file ---------
		//searchUnreferencedSourceFile( progFolder, writer );		

		writer.close();
	}
}
