/*
1)
Name: 	Ramakanth Reddy Palleti
ID: 	999990418
Sec:	004
2)
Name:	Purushotham Reddy Kovvuri
Id:		999991110
Sec:	004

Instructions:
1. Create class file using command D:/> javac ETL.java
2. Run class file with necessary command arguments as shown in following command
	
	D:/> java ETL Canon_G3_Camera.txt DVD_player.txt Jukebox.txt Nikon_coolpix.txt Nokia_6610.txt
	
	Note: 	Canon_G3_Camera.txt DVD_player.txt Jukebox.txt Nikon_coolpix.txt Nokia_6610.txt
			represents the list of files to be given to ETL.java program.
	
	Note:	Must provide atleast one file to check output
	
	Note:	Input files should be in the same directory
*/

import java.util.*;
import java.io.*;
import java.lang.Object;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ETL{
	public static void main(String[] s) throws FileNotFoundException, IOException {
		int count = 0;
	  for(String fileName : s){
		String fileText = ReadTextFromFile(fileName,count++);		
		WriteIntoJSON(ExtractAndTransform(fileText,fileName),fileName.replaceAll(".txt",".json"),fileName);
	  }	  
	}	
	public static String cleanPunctuations(String text) {
        return text.replaceAll("\\p{Punct}+", "").replace("\\s+", " ");
    }
	public static void WriteIntoJSON(List<String> transformedList, String fileName,String originalName){
		System.out.println("Started loading file " + originalName + " into JSON file " + fileName);
        Writer writer = null;
        try{
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
            for(String transformedBlock : transformedList){
                writer.write(transformedBlock);
                writer.write("\n");
            }
        }catch (IOException ex) {
          // report
        } finally {
           try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
		System.out.println("Completed loading file " + originalName + " into JSON file " + fileName);
    }
	public static String ReadTextFromFile(String fileName,int count) throws FileNotFoundException, IOException{
		System.out.println("Start reading file "+String.valueOf(count)+" " + fileName+".....");
		File dir = new File(".");
		File fin = new File(fileName);
		FileInputStream fis = new FileInputStream(fin);

		
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		System.out.println("Reading file...");
		String fileText = "";
		int lineCount = 1;
		while ((line = br.readLine()) != null) {
			if(lineCount >=12){
				fileText += line;
				
			}
			else{
				lineCount++;
			}
		}
		System.out.println("Completed reading file "+ fileName);
		return fileText;
	}	
	public static List<String> ExtractAndTransform(String fileText, String fileName){
		System.out.println("Started Extraction and Transforming file: " + fileName);
		String titleBlockDelimiter = "(\\[)" + "(t)" + "(\\])";                
		String positiveNegativeDelimiter = "(\\[)"+ "(\\+)" + "(\\d+)" + "(\\])"+"|"+"(\\[)"+ "(\\+)" + "(\\d+)" + "(\\])";
		
		Pattern positivePattern = Pattern.compile("(\\+)" + "(\\d+)" + "(\\])");
		Pattern negativePattern = Pattern.compile("(\\-)" + "(\\d+)" + "(\\])");
		
		List<String> extractedList = new ArrayList();
		List<String> titleBlocksList = Arrays.asList(fileText.split(titleBlockDelimiter));
		
		String title = "", positiveBlock="", negativeBlock = "";
		
		int count = 0;
		
		for(String titleBlock : titleBlocksList){
			if(titleBlock!=null && !titleBlock.isEmpty()){
				List<String> positiveNegativeBlocksList = Arrays.asList(titleBlock.split(positiveNegativeDelimiter));
				for(int i = 0; i< positiveNegativeBlocksList.size();i++){
					String positiveNegativeBlock = positiveNegativeBlocksList.get(i);
					if(i==0){
						title = positiveNegativeBlock;
					}
					else{
						String indicator = titleBlock.substring(titleBlock.indexOf(positiveNegativeBlock)-3, titleBlock.indexOf(positiveNegativeBlock));
						Matcher positiveMatcher = positivePattern.matcher(indicator);
						Matcher negativeMatcher = negativePattern.matcher(indicator);
						if(positiveMatcher.find()){
							positiveBlock += positiveNegativeBlock + " ";
						}
						if(negativeMatcher.find()){
							negativeBlock += positiveNegativeBlock + " ";
						}
					}
				}
				count = count + 1;
				extractedList.add("{\"id\":"+String.valueOf(count)+",\"title\":\""+cleanPunctuations(title)+"\",\"positive\":\""+cleanPunctuations(positiveBlock)+"\",\"negative\":\""+cleanPunctuations(negativeBlock)+"\"}");
			}                    
		}
		System.out.println("Completed Extraction and Transforming file: " + fileName);
		return extractedList;
	}
}