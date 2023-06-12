package zerobase_study;

public class DistanceCalculator {
	 public static double distance(double lat1, double lon1, double lat2, double lon2) {
	        double toRadians = Math.PI / 180;
	        double earthRadius = 6371; 

	        double dLat = toRadians * (lat2 - lat1);
	        double dLon = toRadians * (lon2 - lon1);
	        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	                Math.cos(toRadians * lat1) * Math.cos(toRadians * lat2) *
	                Math.sin(dLon / 2) * Math.sin(dLon / 2);
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        double distance = earthRadius * c;

	        return distance;
	    }
	}
