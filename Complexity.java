/**
 * The Complexity class is a simple tool to compute the complexity of the 
 * CH class as O(d, r). 
 * <p>
 * @author Yu-jong Tzeng
 * @version 2.0
 * @since August 25, 2019.
 */
public class Complexity
{
    public int[] parN = {1, 1, 2, 3, 5, 7, 11, 15, 22, 30, 42,
        56, 77, 101, 135, 176, 231, 297, 385, 490, 627,
        792, 1002, 1255, 1575, 1958, 2436, 3010, 3718, 4565, 5604,
        6842, 8349, 10143, 12310, 14883, 17977, 21637, 26015, 31185, 37338,
        44583, 53174, 63261, 75175, 89134, 
        105558, 124754, 147273, 173525, 204226};
    public int length = parN.length;   
    public int[] numN = new int[length];
    public long[] space = new long[length];
    public long[] time =  new long[length];
    private arrayOP arrOP = new arrayOP(length);  
    public long count = 0;
    partitions parArr = new partitions(length, length);
    
    public Complexity()  
    {
        for (int i = 0; i < length; i++) {
            numN[i] = 0;
            for (int j = 0; j <= i; j++) {
                numN[i] += parN[j] * parN[i - j];
            }
        }
        //calculate space
        space[0] = 0;
        for (int i = 1; i < length; i++) {
            space[i] = numN[i] + numN[i - 1];
        }
        //calculate time
        for (int d = 1; d < length; d++) {
            count = 0;
            for (int r = 0; r <= 4; r++) {                     
                for (int j = d; j >= 0; j--) {
                    for (int[] alpha : parArr.get(j)) {
                        for (int[] beta : parArr.get(d - j)) {
                            N(d, r, alpha, beta);  
                        }
                    }
                }    
            }     
            time[d] = count;
        }   
                              
        int[] timed = {1, 30, 257, 915, 3000, 
            9310, 27051, 74780, 194335, 483973, 
            1148592, 2617798, 5749747, 12223732, 25213618};
        int[] time4 = {1, 10, 77, 235, 634, 
            1570, 3698, 8437, 18282, 38773, 
            79256, 158318, 307721, 588888, 1097305, 
            2018479, 3641194, 6482355, 11349039, 19662757, 
            33554728, 56741220, 94743429, 156789182, 256654163, 
            416941899, 670573783};
    }
    private void N(int d, int r, int[] alpha, int[] beta) {  
        count++;            
        for (int k = 0; k < length; k++) { // the first term
            if (beta[k] > 0) {
                count++;      
            }                
        }        
        if (d > 0) {                              // the second term
            for (int j = Math.max(0, arrOP.sum(beta) - r + d - 1); j < d; j++) {
                count += parN[j] * parN[d - 1 - j];             
            }
        }                                       
    }     
}
