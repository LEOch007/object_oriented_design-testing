package mcm;
import java.rmi.registry.*;
import java.util.*;

public class MCMServer {
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);			
		}
		catch (Exception e) {
			System.out.println("Registry Creation at port 1099 failed");
			System.exit(0);
		}

		UserInterface.main(args);
	}
}