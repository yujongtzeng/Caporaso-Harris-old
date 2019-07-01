import java.util.ArrayList;
import java.util.Arrays;
/**
 * The partitions class generates all partitions of length less or equal to 
 * maxLength of positive integers less or equal to n. Partitions are written
 * in the way in Caporaso-Harris' paper. i.e. if n = a1*1 + a2*2 + .... is a 
 * partition of n, then this partitions is recoreded as (a1, a2, ....). 
 * 
 *
 * @author Yu-jong Tzeng
 * @since June 13, 2019
 * @version 1.1
 */
public class partitions
{
    private ArrayList<int[]>[] parArray;   
    private static arrayOP aOP;
    private int maxL;
    /**
     * Constructor for objects of class genAlpha
     * 
     * @param n the upper bound for the integers we are going to find 
     * their partions.
     * @param maxLength the max length of the partitions.
     * @param arrOP An arrayOP(maxLength) objects which will help us to do 
     * array operations. 
     */
    public partitions(int n, int maxLength, arrayOP arrOP)
    {
        parArray = new ArrayList[n+1];
        aOP = arrOP;
        maxL = maxLength;
        for (int k = 0; k <= n; k++) {
            parArray[k] = new ArrayList<int[]>();
            initialize(k , new int[maxLength], 0, parArray[k]);       
        }
    }
    public ArrayList<int[]> get(int k)
    {
        if (k >= 0) {return parArray[k];}
        else return new ArrayList<int[]>();
    }
    public void initialize(int k, int[] temp, int current, ArrayList<int[]> res) {
        int remain = k - aOP.I(temp, current);
        // if finished then add to the collection
        if (remain == 0) {res.add(temp.clone());}
        // if not finished but the maxLength is reached
        if (current == maxL) return;
        // not finished but not enough to cover next multiplicity
        if (remain < current + 1) return;
        // not finished, and length not reached
        for (int i = remain / (current + 1); i >= 0; i--) {
            temp[current] = i;            
            initialize(k, temp, current + 1, res);
        }                                            
    }
    public void print() {
        for (int i = 0; i < parArray.length; i++) {
            System.out.println("Partition of " + i);
            for (int[] par : parArray[i]) {
                System.out.println(Arrays.toString(par).replaceAll("\\s", ""));
            }                    
        }    
    }
}
