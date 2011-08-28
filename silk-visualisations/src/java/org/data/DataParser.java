package org.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.alignment.AlignmentFile;

/**
 *
 * @author tgiunipero
 */
public class DataParser implements Serializable {

    private ArrayList<ArrayList[]> parsedData;

    private float lowerThreshold, upperThreshold;

    // Number of 'buckets' used to compartmentalise data
    private int groupingNumber = 70;

    // Data structures for file comparison
    private int[] leftUniqueCount;
    private int[] rightUniqueCount;
    private int[] leftSharedMatchCount;
    private int[] rightSharedMatchCount;
    private ArrayList<String[]> confScoreDelta;


    /**
     * Prepares a JSON-formatted string containing the following items:
     *
     *  1. leftUniqueCount
     *  2. rightUniqueCount
     *  3. leftSharedMatchCount
     *  4. rightSharedMatchCount
     *  5. confScoreDelta
     *
     * @return parsedComparisonData
     */
    public String getComparisonData() {

        String parsedComparisonData = "[";

     /* Prepare leftUniqueCount */
        parsedComparisonData += "[";

        for (int count : leftUniqueCount) {
            parsedComparisonData += count + ",";
        }

        // remove comma
        parsedComparisonData = parsedComparisonData.substring(0,
                               parsedComparisonData.length()-1);

        parsedComparisonData += "],";

     /* Prepare rightUniqueCount */
        parsedComparisonData += "[";

        for (int count : rightUniqueCount) {
            parsedComparisonData += count + ",";
        }

        // remove comma
        parsedComparisonData = parsedComparisonData.substring(0,
                               parsedComparisonData.length()-1);

        parsedComparisonData += "],";

     /* Prepare leftSharedMatchCount */
        parsedComparisonData += "[";

        for (int count : leftSharedMatchCount) {
            parsedComparisonData += count + ",";
        }

        // remove comma
        parsedComparisonData = parsedComparisonData.substring(0,
                               parsedComparisonData.length()-1);

        parsedComparisonData += "],";

     /* Prepare rightSharedMatchCount */
        parsedComparisonData += "[";

        for (int count : rightSharedMatchCount) {
            parsedComparisonData += count + ",";
        }

        // remove comma
        parsedComparisonData = parsedComparisonData.substring(0,
                               parsedComparisonData.length()-1);

        parsedComparisonData += "],";

     /* Prepare confScoreDelta */
        parsedComparisonData += "[";

        if (!confScoreDelta.isEmpty()) {

            for (String[] matchData : confScoreDelta) {

                parsedComparisonData += "[";
                parsedComparisonData += "\"" + matchData[0] + "\",";  // confidence score pair
                parsedComparisonData += matchData[1] + ",";           // quantity
                parsedComparisonData += "\"" + matchData[2] + "\"";   // entity pairs
                parsedComparisonData += "],";
            }

            // remove comma
            parsedComparisonData = parsedComparisonData.substring(0,
                                   parsedComparisonData.length()-1);
        }

        parsedComparisonData += "]]";
        return parsedComparisonData;
    }


    public String getConfScoreThresholds() {

        return "{\"lowerThreshold\":\"" + lowerThreshold +
               "\", \"upperThreshold\":\"" + upperThreshold + "\"}";
    }


    public ArrayList<ArrayList[]> getParsedData() {
        return this.parsedData;
    }


    public String getSketchDataAsJSON() {

        String parsedFileData = "[";

        for (ArrayList[] file : parsedData) {

            parsedFileData += "[";

            for (ArrayList grouping : file) {

                parsedFileData += "[";

                if (grouping.size()>0) {

                    for (int i=0; i<grouping.size(); i++) {

                        parsedFileData += "{";

                        String[] matching = (String[]) grouping.get(i);

                        String entity1 = (String)  matching[0];
                        String entity2 = (String)  matching[1];
                        String confScore = (String) matching[2];

                        parsedFileData += "\"e1\":" + entity1 + "," +
                                          "\"e2\":" + entity2 + "," +
                                          "\"c\":" + confScore;

                        parsedFileData += "},";
                    }

                    // remove final comma
                    parsedFileData = parsedFileData.substring(0, parsedFileData.length()-1);
                }

                parsedFileData += "],";
            }

            // remove final comma
            parsedFileData = parsedFileData.substring(0, parsedFileData.length()-1);
            parsedFileData += "],";
        }

        // remove final comma
        parsedFileData = parsedFileData.substring(0, parsedFileData.length()-1);
        parsedFileData += "]";

        return parsedFileData;
    }


    public static String getURIs(ArrayList entityPairs) {

        // references to class-wide containers for entity URIs of all loaded files
        List entity1Pool = AlignmentFile.getEntity1Pool();
        List entity2Pool = AlignmentFile.getEntity2Pool();

        String uriData = "";

        for (Object each : entityPairs) {

            String entityPair = String.valueOf(each);
            String[] ePair = entityPair.split(",");

            int entity1 = Integer.parseInt(ePair[0]);
            int entity2 = Integer.parseInt(ePair[1]);

            String e1 = (String) entity1Pool.get(entity1);
            String e2 = (String) entity2Pool.get(entity2);

            // attempt to find rdf prefix
            e1 = RdfPrefixMap.getPrefix(e1);
            e2 = RdfPrefixMap.getPrefix(e2);

            uriData += "{\"e1\":\"" + e1 +
                       "\",\"e2\":\"" + e2 + "\"},";
        }

        // remove trailing comma
        uriData = uriData.substring(0, uriData.length()-1);

        return uriData;
    }


    public void setConfScoreThresholds(ArrayList fileNames, HashMap uploadedFiles) {

        lowerThreshold = 1;
        upperThreshold = 0;
        float min, max;

        for (Object fileName : fileNames) {

            min = 1;
            max = 0;

            AlignmentFile file = (AlignmentFile) uploadedFiles.get(fileName);
            Iterator iter = file.getMatchings().iterator();

            // get confidence scores
            while (iter.hasNext()) {

                String matching = (String) iter.next();
                String[] tokens = matching.split(",");
                float confidenceScore = Float.parseFloat(tokens[2]);

                if (confidenceScore < 0.001) {  // done to avoid scientific
                    confidenceScore += 0.001;   // notation from occurring
                }

                if (confidenceScore < min) {
                    min = confidenceScore;
                }
                if (confidenceScore > max) {
                    max = confidenceScore;
                }
            }

            // get lower and upper tenths
            min = Float.parseFloat(String.valueOf(min).substring(0,3));
            if (min == 1.0f) {  // in case all values are 1.0
                min = 0.9f;
            }

            if (max < 1.0f) {
                max += 0.1;
            }
            max = Float.parseFloat(String.valueOf(max).substring(0,3));

            if (min < lowerThreshold) {
                lowerThreshold = min;
            }
            if (max > upperThreshold) {
                upperThreshold = max;
            }
        }
    }


    public void setGroupingNumber(int groupingNumber) {
        this.groupingNumber = groupingNumber;
    }


   /**
    * Prepares data for a comparison of two files.
    * Assumes that sketchData contains exactly two items.
    * (This call is made when user selects 2 files, is in
    * either tab 2 or 3, and clicks the 'Render chart' button.) */
    public void generateFileComparisonData() {

        // Initialise data structures
        leftUniqueCount       = new int[groupingNumber];
        rightUniqueCount      = new int[groupingNumber];
        leftSharedMatchCount  = new int[groupingNumber];
        rightSharedMatchCount = new int[groupingNumber];
        confScoreDelta        = new ArrayList();

        ArrayList[] leftFile = parsedData.get(0);
        ArrayList[] rightFile = parsedData.get(1);

        String leftEntity1, leftEntity2, leftConfScore;
        String rightEntity1, rightEntity2, rightConfScore;

        String[] leftMatch, rightMatch;

        int matchCount, uniqueCount;
        boolean matchFound;

     /* Get matches for left file */
        for (int i=0; i<groupingNumber; i++) {

            matchCount = 0;
            uniqueCount = 0;

            // Check if row is empty
            if (!leftFile[i].isEmpty()) {

                // Begin matching
                for (int j=0; j<leftFile[i].size(); j++) {

                    matchFound = false;

                    leftMatch = (String[]) leftFile[i].get(j);

                    leftEntity1 = leftMatch[0];
                    leftEntity2 = leftMatch[1];
                    leftConfScore = leftMatch[2];

                    for (int m=0; m<groupingNumber; m++) {

                        if ( matchFound ) { break; }

                        // Check if row is empty
                        if (!rightFile[m].isEmpty()) {

                            for (int n=0; n<rightFile[m].size(); n++) {

                                rightMatch = (String[]) rightFile[m].get(n);

                                rightEntity1 = rightMatch[0];
                                rightEntity2 = rightMatch[1];
                                rightConfScore = rightMatch[2];

                                // If a match exists, increment matchCount
                                // Also store confidence scores as x, y coordinates
                                // and add entity pair to existing list
                                if (leftEntity1.equals(rightEntity1) &&
                                    leftEntity2.equals(rightEntity2)) {

                                    matchCount++;

                                    String confScores = leftConfScore + "," + rightConfScore;
                                    String entityPair = leftEntity1 + "," + leftEntity2;

                                    if (confScoreDelta.isEmpty()) {
                                        String[] newMatchData = {confScores, "1", entityPair};
                                        confScoreDelta.add(newMatchData);
                                    } else {
                                        boolean duplicateMatchFound = false;
                                        for (int matchData=0; matchData<confScoreDelta.size(); matchData++) {
                                            if(confScores.equals(confScoreDelta.get(matchData)[0])) {
                                                duplicateMatchFound = true;
                                                // increment quantity
                                                int quantity = Integer.parseInt(confScoreDelta.get(matchData)[1]) + 1;
                                                // append entity pair to existing pairs
                                                String entityPairs = confScoreDelta.get(matchData)[2] +
                                                                     "|" + entityPair;
                                                // create an updated version
                                                String[] updatedMatchData = {confScores,
                                                                             String.valueOf(quantity),
                                                                             entityPairs};
                                                // update confScoreDelta with the new version
                                                confScoreDelta.set(matchData, updatedMatchData);
                                                break;
                                            }
                                        }
                                        if(!duplicateMatchFound) {
                                            // create a new item and add it to confScoreDelta
                                            String[] newMatchData = {confScores, "1", entityPair};
                                            confScoreDelta.add(newMatchData);
                                        }
                                    }

                                    matchFound = true;
                                    break;
                                }
                            }
                        }
                    }

                    // If no match was found, increment uniqueCount
                    if ( !matchFound ) { uniqueCount++; }
                }
            }
            // Store total match counts for row
            leftSharedMatchCount[i] = matchCount;
            leftUniqueCount[i] = uniqueCount;
        }

     /* Get matches for right file */
        for (int i=0; i<groupingNumber; i++) {

            matchCount = 0;
            uniqueCount = 0;

            // Check if row is empty
            if (!rightFile[i].isEmpty()) {

                // Begin matching
                for (int j=0; j<rightFile[i].size(); j++) {

                    matchFound = false;

                    rightMatch = (String[]) rightFile[i].get(j);

                    rightEntity1 = rightMatch[0];
                    rightEntity2 = rightMatch[1];

                    for (int m=0; m<groupingNumber; m++) {

                        if ( matchFound ) { break; }

                        // Check if row is empty
                        if (!leftFile[m].isEmpty()) {

                            for (int n=0; n<leftFile[m].size(); n++) {

                                leftMatch = (String[]) leftFile[m].get(n);

                                leftEntity1 = leftMatch[0];
                                leftEntity2 = leftMatch[1];

                                // If a match exists, increment matchCount
                                if (rightEntity1.equals(leftEntity1) &&
                                    rightEntity2.equals(leftEntity2)) {
                                    matchCount++;
                                    matchFound = true;
                                    break;
                                }
                            }
                        }
                    }

                    // If no match was found, increment uniqueCount
                    if ( !matchFound ) { uniqueCount++; }
                }
            }
            // Store total match counts for row
            rightSharedMatchCount[i] += matchCount;
            rightUniqueCount[i] = uniqueCount;
        }
    }


   /**
    * Organises data from selected files into a multi-dimensional array *
    * (array of ArrayLists). Each ArrayList, or 'grouping' will contain *
    * confidence score values pertaining to that grouping's range.      */
    public void parseData(ArrayList fileNames, HashMap uploadedFiles) {

        // Get range for confidence score thresholds
        float thresholdRange = (upperThreshold - lowerThreshold);
        // Specify confidence score range for each grouping
        float confidenceScoreRangePerGrouping = thresholdRange / groupingNumber;

        // Set up structure to hold all data
        parsedData = new ArrayList();

        // Loop through each file
        for (Object fileName : fileNames) {

            // For each file, create new array of ArrayLists and enter it into sketchData
            ArrayList[] data = new ArrayList[groupingNumber];
            parsedData.add(data);

            // Initialise ArrayLists
            for (int i=0; i<groupingNumber; i++) { data[i] = new ArrayList(); }

            // Get the file matchings
            AlignmentFile file = (AlignmentFile) uploadedFiles.get(fileName);
            Iterator iter = file.getMatchings().iterator();

            // Loop through each match, get entity and confidence score values
            float currentMax;
            while (iter.hasNext()) {

                String matching = (String) iter.next();
                String[] tokens = matching.split(",");
                String entity1 = tokens[0];
                String entity2 = tokens[1];
                String confidenceScore = tokens[2];

                // abridge matching
                if (confidenceScore.length() > 5) {
                    confidenceScore = confidenceScore.substring(0, 5);
                }

                // Add to the appropriate grouping based on confidence score
                for (int k=0; k<groupingNumber; k++) {
                    currentMax = ((k+1) * confidenceScoreRangePerGrouping) + lowerThreshold;

                    if (Float.parseFloat(confidenceScore) <= currentMax) {

                        // create new array
                        String[] match = new String[3];
                        match[0] = entity1;
                        match[1] = entity2;
                        match[2] = confidenceScore;
                        data[k].add(match);
                        break;
                    }
                }
            }
        }
    }
}
