package driver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import models.DeliveryInformation;
import models.DriverInformation;
import models.Location;
import models.Order;
import util.DistanceCalc;
import util.TimeFormatter;

class DriverShell {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to SalEats v2.0!");
        Socket s;
        while (true) {
            String ans = null;
            try {
                System.out.print("Enter the server hostname: ");
                String hostname = in.nextLine();
                System.out.print("Enter the server port number: ");
                ans = in.nextLine();
                int port = Integer.parseInt(ans);
                s = new Socket(hostname, port);
                System.out.println();
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("The given input " + ans + " is not a number.\n");
            } catch (UnknownHostException uhe) {
                System.out.println("The given host is unknown.\n");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        in.close();

        try {
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            while (true) {
                Integer num = (Integer) ois.readObject();
                if (num == 0) {
                    System.out.println("All drivers have arrived!");
                    System.out.println("Starting service.\n");
                    break;
                } else {
                    System.out.println(num + " more driver is needed before the service can begin.");
                    System.out.println("Waiting...\n");
                }
            }
            while (true) {
                DeliveryInformation info = (DeliveryInformation) ois.readObject();
                if (info == null) {
                    System.out.println(TimeFormatter.getTimestamp() + " All orders completed!");
                    break;
                }
    
                DriverInformation driverInfo = new DriverInformation(info);
                deliver(driverInfo);

                pw.println("done");
                pw.flush();
            }
        } catch (SocketException se) {
            System.out.println(TimeFormatter.getTimestamp() + " Server dropped connection. All orders completed!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            System.out.println(cnfe.getMessage());
        }
    }

    @SuppressWarnings("BusyWait")
    private static void deliver(DriverInformation info) {
        for (Order order : info.getOrders()) {
            System.out.print(TimeFormatter.getTimestamp() + " Starting delivery of ");
            System.out.println(order.getItemName() + " to " + order.getRestaurantName() + ".");
        }
        try {
            Order currentOrder = info.getNext();
            if (currentOrder != null) {
                String prevName = currentOrder.getRestaurantName();
                Location prevLocation = currentOrder.getLocation();
                while (true) {
                    info.reorder(prevLocation);
                    //noinspection BusyWait
                    Thread.sleep((long) (1000 * currentOrder.getDistance()));
                    while (currentOrder != null && prevName.equals(currentOrder.getRestaurantName())) {
                        System.out.print(TimeFormatter.getTimestamp() + " Finished delivery of ");
                        System.out
                                .println(currentOrder.getItemName() + " to " + currentOrder.getRestaurantName() + ".");
                        currentOrder = info.getNext();
                    }
                    if (currentOrder == null) {
                        // Go back to HQ
                        System.out.println(
                                TimeFormatter.getTimestamp() + " Finished all deliveries, returning back to HQ.");
                        Thread.sleep((long) (1000 * DistanceCalc.getDistance(prevLocation, info.getHQLocation())));
                        System.out.println(TimeFormatter.getTimestamp() + " Returned to HQ.");
                        break;
                    }
                    prevLocation = currentOrder.getLocation();
                    prevName = currentOrder.getRestaurantName();
                    System.out.println(TimeFormatter.getTimestamp() + " Continuing delivery to "
                            + currentOrder.getRestaurantName() + ".");
                }
            }
        } catch (InterruptedException ie) {
            System.out.println("Interrupted");
        }
    }
}
