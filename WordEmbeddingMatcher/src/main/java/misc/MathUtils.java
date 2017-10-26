package misc;

/**
 * @author audunvennesland
 * 26. okt. 2017 
 */
public class MathUtils {
	
	/**
	 * Rounds a double to a specified number of digits after the decimal point
	 * @param value the double to be rounded
	 * @param places number of digits after decimal point
	 * @return rounded double
	 */
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

}
