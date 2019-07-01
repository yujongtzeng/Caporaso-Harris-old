import java.util.Arrays;
/**
 * Write a description of class genFunction here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class myf
{
    public static String toVar(int [] arr) {
        String ans = "";
        for (int i = 1; i < Math.min(26, arr.length); i++) {
            if (arr[i] > 0) {
                ans = ans + (char) (i + 97) + "^" + arr[i] + " ";            
            }
        }
        return (ans.length() == 0? "" : ans.substring(0, ans.length()-1));
    }
    /** 
     * Convert the integer array to a String (without space in between). 
     */   
    public static String str(int[] arr) {
        return Arrays.toString(arr).replaceAll("\\s", "");
    }    
    /**
     * Compute the arithmetic genus of the curve class O(a,b)
     */
    public static int g_a (int a, int b) {  // compute the arithmetic genus for ah+bf on F_n
        return (a - 1) * (b - 1);
    }
}
