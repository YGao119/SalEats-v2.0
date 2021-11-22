import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import models.DeliveryInformation;
import models.Event;
import models.Location;
public class Headquater {

	static Scanner scan;
	static ArrayList<Event> events;
	static Location HQloc;
    static int numDrivers;
    static ServerSocket serverSocket;
    static ArrayBlockingQueue<ServerThread> serverThreads;
    
	public static void readFile() {
		events = new ArrayList<Event>();
		scan = new Scanner(System.in);
		System.out.println("What is the name of the schedule file?");
		String fileName = scan.nextLine();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String [] temp = line.split(",");
		    	Event e = new Event(Integer.parseInt(temp[0]), temp[1].trim(), temp[2].trim());
		    	events.add(e);
		    }
		    System.out.println();
		} catch (FileNotFoundException e) {
			System.out.println();
			System.out.println("File " + fileName + " cannot be found! ");
			readFile();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println();
			readFile();
		} catch(NumberFormatException e) {
			System.out.println("The file contains invalid start time. Make sure it is provided as an integer.");
			System.out.println();
			readFile();
		} catch(Exception e) { 
			System.out.println("The file cannot be parsed due to invalid format. check number of arguments in each line.");
			System.out.println();
			readFile();
		}			
	}
	
	public static void readLocation() {
		try {	
			System.out.println("What is your latitude?");
			String temp = scan.nextLine();
			double latitude = Double.parseDouble(temp);
			System.out.println();
			System.out.println("What is your longitude?");
			temp = scan.nextLine();
			double longitude = Double.parseDouble(temp);
			if(latitude <= -90 || latitude >= 90 || longitude <= -180 || longitude >= 180) {
				throw new Exception();
			}
			HQloc = new Location(latitude, longitude);
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println();
			System.out.println("Re-enter latitude and longitude and make sure they're valid doubles!");
			readLocation();
		} catch (Exception e) {
			System.out.println();
			System.out.println("Re-enter latitude and longitude and make sure they're in valid range!");
			readLocation();
		}
	
	}
	
	public static void readDrivers() {
		try {
			System.out.println("How many drivers will be in service today?");
			String temp = scan.nextLine();
			numDrivers = Integer.parseInt(temp);	
			if(numDrivers <= 0) {
				throw new Exception();
			}
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println();
			System.out.println("Re-enter number of drivers and make sure it is an integer!");
			readDrivers();
		} catch (Exception e) {
			System.out.println();
			System.out.println("Re-enter number of drivers and make sure it is an Postive integer!");
			readDrivers();
		}
	}
	
	public static void establishConnection() {
		try {
			int num = 0;
			serverThreads = new ArrayBlockingQueue<ServerThread>(numDrivers);
			serverSocket = new ServerSocket(3456);
			System.out.println("Listening on port 3456.\nWaiting for drivers...");
			System.out.println();
			while(true) {
				Socket s = serverSocket.accept();
				System.out.println("Connection from " + s.getInetAddress());
				num++;
				ServerThread st = new ServerThread(s, numDrivers, num);
				serverThreads.add(st);
				if(num == numDrivers) {
					startService();
					break;
				}
				System.out.println("Waiting for " + (numDrivers - num) + " more dirver(s)...");	
				System.out.println();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void startService() {
		System.out.println("Starting service.");
		System.out.println();
		for(ServerThread st: serverThreads) {
			st.startService();
		}
		
	}
	public static void giveOrders() {
		Collections.sort(events, new Comparator<Event>() {
			@Override
			public int compare(Event A, Event B) {
				if(A.getTime() == B.getTime()) {
		        	return 0;
		        }
		        else if(A.getTime() >= B.getTime()) {
		        	return 1;
		        }
		        else {
		        	return -1;
		        }
			}
		});
		long serviceStartTime = System.currentTimeMillis();
		int indexOfEvent = 0;
		while(indexOfEvent < events.size()) {					
			if(System.currentTimeMillis() - serviceStartTime >= events.get(indexOfEvent).getTime() * 1000) {
				for(ServerThread st: serverThreads) {
					if(st.isAvailble()) {
						DeliveryInformation info = getDeliveryInfo(System.currentTimeMillis() - serviceStartTime, indexOfEvent);
						st.addOrder(info);
						indexOfEvent += info.getRestaurants().size();
						break;
					}
				}
			
			}
		}
		for(ServerThread st: serverThreads) {
			while(!st.isAvailble()) {}				
		}			
		System.out.println("All orders completed! ");
		for(ServerThread st: serverThreads) {
				st.endService();
		}

		try {
			serverSocket.close();
			scan.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	
	}
	public static DeliveryInformation getDeliveryInfo(long time, int index) {
		List<String> restaurants = new ArrayList<String>();
	    List<String> items = new ArrayList<String>();
	    int end = events.size();
		for(int i = index; i < events.size(); i++) {
			if(time < events.get(i).getTime() * 1000) {
				end = i;
				break;
			}
		}
	    for(int i = index; i < end; i++) {
	    	Event e = events.get(i);
	    	restaurants.add(e.getStartLocation());
	    	items.add(e.getItemName());
	    }
	    return new DeliveryInformation(restaurants, items, HQloc);	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile();
		readLocation();
		readDrivers();
		establishConnection();
		giveOrders();

	}

}
