import java.util.Arrays;
/**
 * MyF is a class which contains short misc functions. 
 *
 * @author Yu-jong Tzeng
 * @version 2.0
 * @since August 27, 2019.
 */
public class MyF 
{
    /**
     * Convert the input array to a monomial "b^(a0) c^(a1) d^(a2)...." 
     * The elements of the input array is the power of b, c, d,...,z.  
     * For example, if arr = [1, 3, 0, 4] then output = "b^1 c^3 e^4".
     * Only the first 25 elements will be used, if the length of input 
     * is greater. 
     * @param arr An integer array 
     * @return The string of correponding monomial.
     */
    public static String toVar(int [] arr) {
        String ans = "";
        for (int i = 1; i < Math.min(26, arr.length); i++) {
            if (arr[i] > 0) {
                ans = ans + (char) (i + 97) + "^" + arr[i] + " ";            
            }
        }
        return (ans.length() == 0 ? "" : ans.substring(0, ans.length() - 1));
    }
    /** 
     * Return the string representation of an array (seperated by "," ). 
     * @param arr An integer array
     * @return String
     */   
    public static String str(int[] arr) {
        return Arrays.toString(arr).replaceAll("\\s", "");
    }    
    /**
     * Return the arithmetic genus of the curve class O(a,b), which is 
     * (a - 1)*(b - 1). 
     * @param a An Integer
     * @param b An Integer
     * @return The integer (a - 1)*(b - 1) 
     */
    public static int g_a (int a, int b) { 
        return (a - 1) * (b - 1);
    }
}
