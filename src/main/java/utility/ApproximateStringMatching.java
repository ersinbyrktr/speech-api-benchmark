package utility;

import java.util.HashMap;
import java.util.Map;

/**
 * @author josquin
 */
public class ApproximateStringMatching {

    //Initialize Map
    private static Map<String, Integer> answer = new HashMap<>();
    //Create a tab which store the weight
    private static int weight[][];

    public static void main(String[] args){
        String reference = "hello world";
        String output = "hello wold rty";
        computeDistance(reference, output);
    }

    public static Map<String, Integer> computeDistance(String reference, String output){
        answer.put("Reference_length", reference.length());
        answer.put("Output_length", output.length());


        //Initialization
        weight = new int[reference.length()+1][output.length()+1];
        for(int i=0; i<weight.length; weight[i][0]=i++);
        for(int i=0; i<weight[0].length; weight[0][i]=i++);

        //Compute the weight
        for(int i=1; i<weight.length; i++) {
            for (int j = 1; j < weight[0].length; j++) {
                int cost = reference.charAt(i-1) == output.charAt(j-1) ? 0 : 1;
                weight[i][j] = Math.min(weight[i - 1][j - 1] + cost, Math.min(weight[i - 1][j]+1, weight[i][j - 1]+1));
            }
        }
        answer.put("Insertion", 0);
        answer.put("Deletion", 0);
        answer.put("Substitution", 0);
        answer.put("Total_error", weight[weight.length-1][weight[0].length-1]);

        //Travel the matrix recursively to see if it is an insertion, deletion or substitution
        backwardWalking(weight.length-1, weight[0].length-1);

        answer.put("Percentage_insertion", answer.get("Insertion")/reference.length());
        answer.put("Percentage_deletion", answer.get("Deletion")/reference.length());
        answer.put("Percentage_substitution", answer.get("Substitution")/reference.length());
        answer.put("Percentage_total_error", answer.get("Total_error")/reference.length());


        //printWeight();
        printResult(answer);

        return answer;
    }

    private static void backwardWalking(int i, int j){
        if(i==0 && j==0) return;
        int diagonal;
        int horizontal;
        int vertical;

        try {
            diagonal = weight[i - 1][j - 1];
        }catch (ArrayIndexOutOfBoundsException e){
            diagonal = weight[weight.length-1][weight[0].length-1];
        }
        try{
            horizontal = weight[i][j - 1];
        }catch (ArrayIndexOutOfBoundsException e){
            horizontal = weight[weight.length-1][weight[0].length-1];
        }
        try{
            vertical = weight[i - 1][1];
        }catch (ArrayIndexOutOfBoundsException e){
            vertical = weight[weight.length-1][weight[0].length-1];
        }

        int min = Math.min(diagonal, Math.min(horizontal, vertical));

        //Diagonal
        if(min==diagonal){
            if(min!=weight[i][j]) answer.put("Substitution", answer.get("Substitution")+1);
            j--;
            i--;
        }
        //Horizontal
        else if(min==horizontal){
            answer.put("Insertion", answer.get("Insertion")+1);
            j--;
        }
        //Vertical
        else if(min==vertical){
            answer.put("Deletion", answer.get("Deletion")+1);
            i--;
        }
        backwardWalking(i, j);
    }

    private static void printWeight(){
        for(int i=0; i<weight.length; i++) {
            for (int j = 0; j < weight[0].length; j++) {
                System.out.print(weight[i][j]+" ");
            }
            System.out.println();
        }
    }

    private static void printResult(Map<String, Integer> map){
        for (String s: map.keySet()) {
            System.out.println(s+" : "+map.get(s));
        }
    }
}
