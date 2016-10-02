import java.util.*;
import java.io.*;
import java.lang.Object;

public class facebook {
	
	public static void main(String[] s) throws IOException {
	  if(s.length>=3){
		String fileName = s[0];
		System.out.println("given file name: " + fileName);
		int x = Integer.parseInt(s[1]);
		int y = Integer.parseInt(s[2]);
		
		List<String> userIdList = new ArrayList<String>();

		Hashtable usersList = new Hashtable();

		File dir = new File(".");
		File fin = new File(fileName);
		FileInputStream fis = new FileInputStream(fin);

		
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		System.out.println("Reading file...");
		while ((line = br.readLine()) != null) {
			String[] splitLine = (line.split(","));
			String userId = splitLine[0];	
			userIdList.add(userId);
			String[] hobbies = line.replace(userId+",","").trim().split(",");		
			List<String> userHobbies = Arrays.asList(hobbies);		
			usersList.put(userId,userHobbies);		
		}

		br.close();
		System.out.println("Reading file finished.");
		System.out.println("Creating circles list.....");
		List<String> circleUsersList = CreateCirclesList(usersList,x);
		System.out.println("Creating Circles list completed");
		System.out.println("Creating populars list....");
		createPopularList(userIdList,circleUsersList,y);
		System.out.println("Creating populars list completed.");
	  }
	  else{
		  System.out.println("please pass all command arguments");
	  }
	}
	public static List<String> CreateCirclesList(Hashtable usersList, int x){
		//Circles list
		Hashtable circlesList = new Hashtable();
		int outterIndex = 0;
		int innerIndex = 0;
		List outterKeys = new ArrayList(usersList.keySet());
		List innerKeys = new ArrayList(usersList.keySet());

		List<String> outterHobbies = new ArrayList<String>();
		List<String> innerHobbies = new ArrayList<String>();

		List<String> circleUsersList = new ArrayList<String>();
		
		Writer writer = null;
		try{
			writer = new BufferedWriter(new OutputStreamWriter(
				  new FileOutputStream("circles.txt"), "utf-8"));
			for(outterIndex=0;outterIndex<outterKeys.size();outterIndex++){
				outterHobbies = (List<String>)usersList.get(outterKeys.get(outterIndex));
				for(innerIndex=outterIndex+1;innerIndex<innerKeys.size();innerIndex++){
					if(innerIndex==outterIndex){
					}
					else
					{
						innerHobbies = (List<String>)usersList.get(innerKeys.get(innerIndex));	
						List<String> similarHobbies = new ArrayList<String>();
						for(String hobby : outterHobbies){
							if(innerHobbies.contains(hobby)){
								similarHobbies.add(hobby);
							}
						}
						if(similarHobbies.size()>=x){
							circlesList.put(outterKeys.get(outterIndex)+","+innerKeys.get(innerIndex),similarHobbies);	
						}
					}
				}
			}
			Hashtable newCirclesList = sortList(circlesList);
			List<String> newKeys = new ArrayList(newCirclesList.keySet());
			for(String key : newKeys){
				circleUsersList.add(String.join(",",(List<String>)newCirclesList.get(key)));
				writer.write(String.join(",",(List<String>)newCirclesList.get(key)) + "\t \t \t" + key );
				writer.write("\n");
			}
			
		}catch (IOException ex) {
		  
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		return circleUsersList;
	}
	
	public static void createPopularList(List<String> userIdList, List<String> circleUsersList, int y){
		Writer writer = null;
		boolean popularUser = false;
		try{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("popular.txt"), "utf-8"));
			for(String id : userIdList){
				int count =0;
				for(String circle: circleUsersList){
					if(circle.contains(id)){
						count++;
					}
				}
				if(count>=y){
					popularUser = true;
					writer.write(id + "\t" + count);
					writer.write("\n");	
				}
			}
			if(!popularUser){
				writer.write("No popular users found");
			}
		}catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
	public static Hashtable sortList(Hashtable circlesList){
		Hashtable newCirclesList = new Hashtable();
		try{
			List<String> outterKeys = new ArrayList(circlesList.keySet());
			List<String> outterHobbies = new ArrayList<String>();
			
			for(int outterIndex = 0; outterIndex<outterKeys.size();outterIndex++){
				outterHobbies = (List<String>)circlesList.get(outterKeys.get(outterIndex));			
				newCirclesList.put(String.join(",",outterHobbies),Arrays.asList(outterKeys.get(outterIndex).split(",")));
			}

			
		}catch(Exception exe){
			System.out.println(exe);
			return newCirclesList;
		}
		return newCirclesList;
	  
	}
	public static <T> boolean listEqualsNoOrder(List<T> l1, List<T> l2) {
		final Set<T> s1 = new HashSet<>(l1);
		final Set<T> s2 = new HashSet<>(l2);

		return s1.equals(s2);
	}
}