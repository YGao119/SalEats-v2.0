import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import models.DeliveryInformation;

public class ServerThread extends Thread {

	private ObjectOutputStream oos;
	private BufferedReader br;
	private Socket s;
	private DeliveryInformation deliverys;
	private boolean availble = true;
	
	
	public ServerThread(Socket socket, int numDrivers, int num) {
		this.s = socket;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			br = new BufferedReader(new InputStreamReader(s.getInputStream())); 
			if(numDrivers - num != 0) {
				oos.writeObject(numDrivers - num);
				oos.flush();
			}
			start();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addOrder(DeliveryInformation d) {
		deliverys = d;
		availble = false;
		try {
			oos.writeObject(deliverys);
			oos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startService() {
		try {
			oos.writeObject(0);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void endService() {
		try {
			oos.writeObject(null);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isAvailble() {
		return availble;
	}
	public void run() {
		try {
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				if(line.equals("done")) {
					this.availble = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				br.close();
				s.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
