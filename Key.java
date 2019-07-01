import java.util.ArrayList;
import java.util.Arrays;

/** The Key class has three static methods to concatenate intputs into an 
 * ArrayList of Integer. All methods are called make. The only difference 
 * is they accept different inputs. 
 * @author Yu-jong Tzeng
 * @version 2.1
 * @since March 20, 2019.
 */

public class Key
{ 
    /**
     * The make method concatenate three integers and two ArrayList<Integers>. 
     * 
     * @param a int
     * @param b int
     * @param g int
     * @param alpha ArrayList<Integer>
     * @param beta ArrayList<Integer>
     * @return ArrayList<Integer> The concatenation of the inputs 
     * (no separators). 
     */
    
    public static ArrayList<Integer> make(int a, int b, int g, 
                        ArrayList<Integer> alpha, ArrayList<Integer> beta)
    {
        ArrayList<Integer> key = new ArrayList<Integer>();
        key.add(a);
        key.add(b);
        key.add(g);      
        key.addAll(alpha);
        key.addAll(beta);
        return key;        
    }  
    
    /**
     * The make method concatenate three integers and two ArrayList<Integers>. 
     * 
     * @param a int
     * @param b int
     * @param g int
     * @param alpha int[]
     * @param beta int[]
     * @return ArrayList<Integer> The concatenation of the inputs 
     * (no separators). 
     */
    public static ArrayList<Integer> make(int a, int b, int g, 
                                   int[] alpha, int[] beta) {
        ArrayList<Integer> key = new ArrayList<Integer>();
        key.add(a);
        key.add(b);
        key.add(g);      
        for (int i = 0; i < alpha.length; i++) {
            key.add(alpha[i]);
        }
        for (int i = 0; i < beta.length; i++) {
            key.add(beta[i]);
        }
        return key;        
    }   
    
    /**
     * The make method concatenate two integers and two int[]. 
     * 
     * @param deg int
     * @param r int
     * @param alpha int[]
     * @param beta int[]
     * @return ArrayList<Integer> The concatenation of the inputs 
     * (no separators). 
     */
    public static ArrayList<Integer> make(int deg, int r, 
                                          int[] alpha, int[] beta) {
        ArrayList<Integer> key = new ArrayList<Integer>();
        key.add(deg);
        key.add(r);  
        for (int i = 0; i < alpha.length; i++) {
            key.add(alpha[i]);
        }
        for (int i = 0; i < beta.length; i++) {
            key.add(beta[i]);
        }
        return key;        
    }   
    
}
