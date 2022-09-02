package pri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

public class FileParser {

	public class UseOfFunction {
		
		int useCount = 0;
		String functionName;
		String functionType;
		
		// declaration location
		String sourceName; // full path
		String sourceProgBasedUrl;
		int sourceLineNum;		
		String sourceExt; // without .
	}
	
	public class Result {
				
		public int sourceLineCount = 0; // all type of files		
		public int pcFilesSourceLineCount = 0;
		public int cppFilesSourceLineCount = 0;
				
		public int functionsCount = 0;
		
		public Map<String,UseOfFunction> useOfFunctions = new HashMap<String,UseOfFunction>();
	}			

	boolean isCommented( String line ) {

		Character chPrevious = null;
		
		for( int chpos = 0 ; chpos < line.length() ; chpos++ ) {
			
			char ch = line.charAt( chpos );

			if( ch == '*' ) {
				
				// considering it's 
				/*
				 * commented
				 */
				return true;
			}			
			
			if( ch == '/' || ch == ' ' || ch == '\t' ) {
				
				// could be ...
				
				if( chPrevious != null && chPrevious == '/' && ch == '/' ) {
					
					// sure it is
					return true;
				}
				
			}
			else {
				
				// can't
				return false;
			}
			
			chPrevious = ch;
		}
		
		return false;
	}
	
	// function declaration census
	
	void detectFunctionDeclaration( int parsingId, String current, String next, StringBuilder function, StringBuilder type ) {

//		if( parsingId == 468518 ) { 
//			System.out.println( "parsingId=" + parsingId );			
//		}
		
		// we evaluate the mainly the string current + optionally the next part
		
		if( isCommented( current ) ) { return; }

		// detection of presence of a ()		
		
		int pos1 = current.indexOf('(');
		if( pos1 == -1 ) { 
			return;
		}
		
		if( pos1 < 3 ) {
			return; // require a minimum prototype T f();
		}

		String line = null;
		
		if( isCommented( next ) == false )  {
			line = current + next;
		}
		else {
			line = current;
		}

		int pos2 = line.indexOf(')');
		if( pos2 == -1 ) { 
			return;
		}
		
		if( (pos2 > pos1) == false ) {
			return;
		}
		
		// detection presence of a following implementation
		
		int pos3 = line.indexOf('{');
		if( pos3 == -1 ) { 
			return;
		}
		
		if( (pos3 > pos2) == false ) {
			return;
		}			
					
		String leftPart = line.substring( 0, pos1 ); // extract > xxx type functionName  <
				
		// isolate the function name from the left part
		
		String functionLetters = "";
		int savePos = 0;
		
		for( int chpos = (leftPart.length() - 1) ; chpos >= 0 ; chpos-- ) {
			
			char ch = leftPart.charAt( chpos );
			
			if(( ch == '=' || ch == '+' || ch == '-'  || ch == ',' || ch == ';' || ch == '{' || ch == '}' || ch == ':'  || ch == '<'  || ch == '>' || ch == '['  || ch == ']' ) ) { return; }
			if(( ch == '*' || ch == '&' ) && ( functionLetters.length() == 0 ) ) { return; }
			
			if(( ch == ' ' || ch == '\t' ) && ( functionLetters.length() == 0 )) { continue;}
			
			if(( ch == ' ' || ch == '\t' || ch == '*' || ch == '&' ) && ( functionLetters.length() > 0 )) {				
				savePos = chpos; // end of function name
				break;
			}
			
			functionLetters += ch; // concat the function name
		}
		
		if( functionLetters.length() == 0 ) {
			return;
		}
		
		String functionName = new StringBuilder(functionLetters).reverse().toString();
		
		if( functionName.equals("-"      )) { return; }
		if( functionName.equals(":"      )) { return; }		
		if( functionName.equals("*"      )) { return; }
		if( functionName.equals("="      )) { return; }
		if( functionName.equals("if"     )) { return; }
		if( functionName.equals("while"  )) { return; }
		if( functionName.equals("for"    )) { return; }
		if( functionName.equals("switch" )) { return; }
		if( functionName.equals("strcmp" )) { return; }
		if( functionName.equals("main"   )) { return; }
		if( functionName.equals("wmain"  )) { return; }
				
		// isolate the type from the left part
		
		String typeLetters = "";
		
		for( int chpos = savePos ; chpos >= 0 ; chpos-- ) {
			
			char ch = leftPart.charAt( chpos );
			
			if(( ch == '=' || ch == '+' || ch == '-' )) { return; }
			
			if( ch == ' ' || ch == '\t'  ) {
				
				if( typeLetters.length() > 0 ) {					
					break;
				}
				else {
					continue;
				}
			}
			
			typeLetters += ch;
		}
		
		if( typeLetters.length() == 0 ) {
			return;
		}		
		
		String typeName     = new StringBuilder(typeLetters).reverse().toString();
		
		if( typeName.equals("*")) { return; }
		if( typeName.equals("=")) { return; }		
		if( typeName.contains("{")) { return; }
		if( typeName.contains("}")) { return; }
		if( typeName.contains("(")) { return; }
		if( typeName.contains(")")) { return; }
		if( typeName.contains("return")) { return; }
		if( typeName.contains("define")) { return; }
		if( typeName.contains("/*")) { return; }
		if( typeName.contains("&&")) { return; }
		if( typeName.contains("||")) { return; }
		if( typeName.contains(">")) { return; }
		if( typeName.contains("<")) { return; }
		
		type.append(typeName);
		function.append(functionName);

		System.out.print( "-functionName->" + typeName + "@" + functionName + "<-" + parsingId + "\n" );				
	}
	
	void censusFunctionDeclaration( List<FolderWalker.FileItem> fileItems, Result r, BufferedWriter writer ) throws IOException {
		
		int parsingId = 1;
		
		for( FolderWalker.FileItem fileItem : fileItems ) {
			
			// for each source file

			try( BufferedReader reader = new BufferedReader( new FileReader( fileItem.fullpath )) )
			{
				String previous = null; // previous line
				int fileLineNum = -1;
				
				while( true ) {

					String line = reader.readLine();
					if( line == null ) { break; }

					// detection comment
					
					if( isCommented( line ) == false )  {
						r.sourceLineCount++; 
						
						File file = new File( fileItem.fullpath );
			            String ext = FilenameUtils.getExtension(file.getName());

		            	if( ext.equalsIgnoreCase("c") || ext.equalsIgnoreCase("cpp") ) {
		            		r.cppFilesSourceLineCount++;
		            	}
		            	else if( ext.equalsIgnoreCase("pc") ) {
		            		r.pcFilesSourceLineCount++;
		            	}
					}

					fileLineNum++; // numero de ligne du fichier source

					if( previous != null ) {
						
						parsingId++;
						
						StringBuilder functionNameSb = new StringBuilder();
						StringBuilder functionTypeSb = new StringBuilder();
						
						detectFunctionDeclaration( parsingId, previous, line, functionNameSb, functionTypeSb ); // detection is made on two concatened line
												
						if( functionNameSb.length() > 0 ) {
							
							UseOfFunction uof = r.useOfFunctions.get( functionNameSb.toString() );
							if( uof == null ) {
								
								// if not already done for this function_name
								// create an object
								
								uof = new UseOfFunction();
								uof.functionName = functionNameSb.toString();
								uof.functionType = functionTypeSb.toString();
								uof.sourceName = fileItem.fullpath;								
								uof.sourceProgBasedUrl = fileItem.fullpath.substring( fileItem.fullpath.indexOf("prog"), fileItem.fullpath.length() );
								uof.sourceLineNum = fileLineNum;
								uof.sourceExt = FilenameUtils.getExtension(new File(fileItem.fullpath).getName());
								r.functionsCount++;
								
								writer.write( String.format("%s(%d) [%s@%s]\n", uof.sourceProgBasedUrl, fileLineNum, uof.functionType, uof.functionName ));
								
							} else {
								
								String firstFileName = new File(uof.sourceName).getName();
								String secondFileName = new File(fileItem.fullpath).getName();
								
								if( firstFileName.equals( secondFileName )) {								
									System.out.format("duplicated function names : %s\n", functionNameSb );
									System.out.format("first  : %s(%d)\n", uof.sourceName, uof.sourceLineNum );
									System.out.format("second : %s(%d)\n", fileItem.fullpath, fileLineNum );
								}
							}

							r.useOfFunctions.put( uof.functionName, uof ); 
						}
					}					
					
					previous = line;
				}				
			}
		}
	}
	
	// function use census
	
	void detectFunctionUse( String file, int lineNum, String line, Map< String, UseOfFunction > useOfFunctions ) {
		
		Set<String> functionNames = useOfFunctions.keySet();
		
		for( String functionName : functionNames ) {
			
			// for each functions
			
			int chpos = line.indexOf( functionName );
			if( chpos != -1 ) {
				
				UseOfFunction uof = useOfFunctions.get( functionName );

				// count use of other than the declaration

				if( file.equals(uof.sourceName) == false ) {
					uof.useCount++;
				}
				else {
			
					if( lineNum != uof.sourceLineNum ) {
						uof.useCount++;	
					}
				}			
			}
		}
	}

	void censusFunctionUse( List<FolderWalker.FileItem> fileItems, Result r, BufferedWriter writer ) throws IOException {
		
		for( FolderWalker.FileItem fileItem : fileItems ) {
			
			// for each source file
			
			System.out.format("census use of %s\n", fileItem.fullpath );
			
			int lineNum = 0;
			int scannedLines = 0;

			try( BufferedReader reader = new BufferedReader( new FileReader( fileItem.fullpath )) )
			{
				while( true ) {

					String line = reader.readLine();
					if( line == null ) { break; }
					
					lineNum++;
					
					if( isCommented( line ) ) { continue; }
					
					scannedLines++;
					detectFunctionUse( fileItem.fullpath, lineNum, line, r.useOfFunctions );
				}
			}
			
			writer.write(String.format("census use of %s (%d/%d)\n", fileItem.fullpath, scannedLines, lineNum ));			
		}
	}
}
