import java.util.ArrayList;

/** 
 * The seqOP class does operations on ArrayLists of integers.
 * It contains methods greater, add, substract, greater, I, J(I^c), 
 * sum (|c|) and binom for sequences of integers (as Arraylist<Integer>).
 * All methods are static except binom, because it needs to create a table
 * of binomial coefficients of some given range. The variable N gives 
 * the required the range. 
 * <p>
 * All components of the input are assumed to be nonnegative integers. 
 * All components of the output will be nonnegative integers too. 
 * <p>
 * There are no assumptions on the length of the parameters. 
 * For substract and binom, we check first that c>=d so the operation works 
 * in the naive way.
 * 
 * @author Yu-jong Tzeng
 * @version 2.1
 * @since March 19, 2019.
 */

public class SeqOp
{    
    private static long[][] C;
    private static long max;
    
    /** 
     * Build a table of all binomial coefficients with n choose k 
     * elements for any n, k equal or less than N. 
     * 
     * @param N The max parameter can be taken for binomial coefficients.
     */
    public SeqOp(int N) {
        // 
        max = N;
        C = new long[N + 1][N + 1];
        
        C[0][0] = 1;
        for (int j = 1; j <= N; j++) {
            C[0][j] = 0;
            C[j][0] = 1;
        }    
        
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (j > i) {
                    C[i][j] = 0;
                }                    
                else {
                    C[i][j] = C[i - 1][j - 1] + C[i - 1][j];
                }
            }
        }
    } 
    
    /** Return true if c >= d 
     * 1) If c.size() >= d.size() then we only have to check if there's any 
     *    c_i >= d_i for i= 0....d.size()-1 = minsize-1
     * 2) If c.size() < d.size() then d_i needs to be zero for 
     *    i = c.size(),..., d.size()-1
     */
    private static boolean greater(ArrayList<Integer> c, ArrayList<Integer> d) {
        int minsize = Math.min(d.size(), c.size());
        for (int i = 0; i < minsize; i++) {
            if (c.get(i) < d.get(i)) return false;        
        }
        if (c.size() < d.size()) {
            for (int i = c.size(); i < d.size(); i++) {
                if (d.get(i) > 0) return false;
            }
        }
        return true;
    }
    
    /**
     * Add two sequences of any length componentwisely.
     * @param c ArrayList<Integer>
     * @param d ArrayList<Integer>
     * @return The componentwise sum of the inputs. 
     */
    public static ArrayList<Integer> add(ArrayList<Integer> c, 
                                         ArrayList<Integer> d) {        
        ArrayList<Integer> ans = new ArrayList<Integer>();        
        if (c.size() >= d.size()) {            
            for (int i = 0; i < d.size(); i++) {            
                ans.add(c.get(i) + d.get(i));               
            }  
            for (int i = d.size(); i < c.size(); i++) {            
                ans.add(c.get(i));               
            } 
            return ans;
        }
        else      
            return add(d, c);                
    }  
    
    /**
     * Substract the second input from the first input. Every component 
     * of the first input must be great or equal to the second input. 
     * @param c ArrayList<Integer>
     * @param d ArrayList<Integer>
     * @return The componentwise difference of the inputs. 
     */
    public static ArrayList<Integer> substract(ArrayList<Integer> c, 
                                               ArrayList<Integer> d) {        
        ArrayList<Integer> ans = new ArrayList<Integer>();                
        // if c is not >=d, send error message.       
        if (!greater(c, d)) {
            System.out.println(c + " must be greater then " + d);      
            return new ArrayList<Integer>();
        } 
        
        // Now  c>=d, so we only have to do c_i -d_i for i = 0,....d.size()-1
        // and c_i for i = d.size() ....c.size()
        int minsize = Math.min(c.size(), d.size());
        for (int i = 0; i < minsize; i++) {
            ans.add(c.get(i) - d.get(i));
        }  
        for (int i = minsize; i < c.size(); i++) {
            ans.add(c.get(i));
        }               
        return ans;   
    } 
    
    /**
     * Compute Ic. 
     * @param c ArrayList<Integer>
     * @return If c = (c1, c2, c3,..),  return 1* c1 + 2* c2 + 3 * c3 + ....
     */
    public static int I(ArrayList<Integer> c) {   //Ic
        int ans = 0;        
        for (int i = 0; i < c.size(); i++)
        {
            ans = ans + (i + 1) * c.get(i);
        }        
        return ans; 
    }    
    
    /**
     * Compute Ic only to the given index
     * @param c ArrayList<Integer>
     * @param index int
     * @return If c = (c1, c2, c3,..),  
     * return 1* c1 + 2* c2 + 3 * c3 + .... + index * cindex.x
     */
    public static int I(ArrayList<Integer> c, int index) { 
        int ans = 0;        
        for (int i = 0; i < index; i++) {
            ans = ans + (i + 1) * c.get(i);
        }        
        return ans; 
    }
    
    /**
     * Compute I^c
     * @param c ArrayList<Integer>
     * @return If c = (c1, c2, c3,..),  
     * return 1^(c1) + 2^(c2) + 3^(c3) + .... 
     */
    public static long J(ArrayList<Integer> c) {   
        long ans = 1;        
        for (int i = 0; i < c.size(); i++) {
            ans = ans * ((long) Math.pow(i + 1, c.get(i)));
        }        
        return ans; 
    }
    
    /**
     * Compute |c|
     * @param c ArrayList<Integer>
     * @return If c = (c1, c2, c3,..), return the sum of all ci.
     */
    public static int sum(ArrayList<Integer> c)  
    {
        int ans = 0;        
        for (int i = 0; i < c.size(); i++) {
            ans = ans + c.get(i);
        }        
        return ans; 
    } 
    
    /**
     * Product of componentwise binomial coefficients. Every component 
     * of the first input must be great or equal to the second input. 
     * @param c ArrayList<Integer>
     * @param d ArrayList<Integer>
     * @return The product of C[ci][di] (C[n][k] is the binomial coeffcient).  
     */
    public long binom(ArrayList<Integer> c, ArrayList<Integer> d) {
        long ans = 1;
        // Send error message if c is not >= d
        if (!greater(c, d)) {
            System.out.println(c + " must be greater than " + d);
            return 0;        
        }
        
        // Now c >=d, only need to multiply C[c_i][d_i] in their common range
        // Outside common range, d_i must be zero. C gives 1.
        int minsize = Math.min(c.size(), d.size());
        for (int i = 0; i < minsize; i++) {
            if (c.get(i) > max) {
                System.out.println(c + " is too big." +
                               "Please initiate a larger object.");
                return 0;
            }
            ans = ans * C[c.get(i)][d.get(i)];
        }          
        return ans; 
    }     
}


