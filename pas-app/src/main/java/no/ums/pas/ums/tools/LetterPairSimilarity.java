package no.ums.pas.ums.tools;

import java.util.ArrayList;

public class LetterPairSimilarity {

	/** @return lexical similarity value in the range [0,1] */

	public static double compareNames(String name1 /*namefilter*/, String name2 /*adr-database*/, boolean b_pri_first_and_last) {
		name1 = name1.replace(".", "");
		name2 = name2.replace(".", "");
		name1 = name1.replace("-", " ");
		name2 = name2.replace("-", " ");
		
		
		String [] arr1 = name1.split(" ");
		String [] arr2 = name2.split(" ");
		if(arr1.length == 0)
			return 100;
		
		double best_case = 0.0;
		double total_case = 0.0;
		double n_ignored = 0;
		for(int src=0; src < arr1.length; src++) {
			best_case = 0.0;
			for(int dst=0; dst < arr2.length; dst++) {
				double current_case = 0.0;
				if(arr1[src].length() == 1) { //Morten [H] Helvig
					if(arr2[dst].length() == 1) {
						if(arr2[dst].equals(arr1[src]))
							best_case = 1.0;
					} else if(arr2[dst].toUpperCase().startsWith(arr1[src].toUpperCase())) { //count as a third
						//current_case += 1/3;
						double count_as = 0.5;
						if(count_as > best_case) 
							best_case = count_as;
					}
				} else if(arr2[dst].length() == 1 ) {
					if(arr1[src].length() == 1) {
						if(arr1[src].equals(arr2[dst]))
							best_case = 1.0;
					} else if(arr1[src].toUpperCase().startsWith(arr2[dst].toUpperCase())) { //count as a third
						double count_as = 0.5;
						//current_case += 1/3;
						if(count_as > best_case)
							best_case = count_as;
					}
				} else {
					current_case = compareStrings(arr1[src], arr2[dst]);
					if(current_case > best_case)
						best_case = current_case;
				}
			}
			
			if(arr1.length == arr2.length) { //assume all word will exist
				total_case += best_case;
			} else if(src==0) { //prioritize first name
				total_case += best_case;
			} else if(src==arr1.length-1) { //last name
				total_case += best_case * 0.5;
				n_ignored += 0.5;
			} else { //middle names
				total_case += best_case * 0.3;
				n_ignored += 0.7;
			}
			/*if(src==0 || src==arr1.length-1 || !b_pri_first_and_last) {
				total_case += best_case;
			} else {
				n_ignored++; //ignore middle names
			}*/
		}
		
		return (total_case / ((double)arr1.length - n_ignored)); //return average hit
	}
	
	
	   public static double compareStrings(String str1, String str2) {
	
	       ArrayList<String> pairs1 = wordLetterPairs(str1.toUpperCase());
	
	       ArrayList<String> pairs2 = wordLetterPairs(str2.toUpperCase());
	
	       int intersection = 0;
	
	       int union = pairs1.size() + pairs2.size();
	
	       for (int i=0; i<pairs1.size(); i++) {
	
	           Object pair1=pairs1.get(i);
	
	           for(int j=0; j<pairs2.size(); j++) {
	
	               Object pair2=pairs2.get(j);
	
	               if (pair1.equals(pair2)) {
	
	                   intersection++;
	
	                   pairs2.remove(j);
	
	                   break;
	
	               }
	
	           }
	
	       }
	
	       return (2.0*intersection)/union;
	
	   }
	
	   private static ArrayList<String> wordLetterPairs(String str) {
	
	       ArrayList<String> allPairs = new ArrayList<String>();
	
	       // Tokenize the string and put the tokens/words into an array 
	
	   String[] words = str.split("\\s");
	
	   // For each word
	
	   for (int w=0; w < words.length; w++) {
	
	       // Find the pairs of characters
	
	           String[] pairsInWord = letterPairs(words[w]);
	
	           for (int p=0; p < pairsInWord.length; p++) {
	
	               allPairs.add(pairsInWord[p]);
	
	           }
	
	       }
	
	       return allPairs;
	
	   }
	
	   
	   /** @return an array of adjacent letter pairs contained in the input string */
	   private static String[] letterPairs(String str) {
	
	       int numPairs = str.length()-1;
	
	       String[] pairs = new String[numPairs];
	
	       for (int i=0; i<numPairs; i++) {
	
	           pairs[i] = str.substring(i,i+2);
	
	       }
	
	       return pairs;
	
	   }

   
}