package org.alignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author tgiunipero
 */
public class AlignmentFileWriter {

    /* Serialises an AlignmentFile object and stores it to disk */
    public static boolean writeToDisk(AlignmentFile alignmentFile,
                                      String location) {

        boolean success = false;

        // Because the flattened object is not human-readable,
        // remove the '.xml' extension from the file name
        if (location.endsWith(".xml")) {
            location = location.substring(0, location.length()-4);
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(location);
            oos = new ObjectOutputStream(fos);

            // perform serialisation
            oos.writeObject(alignmentFile);
            oos.close();
            success = true;
        } catch(IOException ex) {
            System.err.println("Unable to write AlignmentFile object to disk: " + ex);
        }

        return success;
    }

    /**
     * Writes 2 text files in Alignment format to disk
     * Used by the GeneratorServlet for sample file generation
     */
    public static boolean writeToDisk(String outputFile1,
                                      String outputFile2,
                                      String linkQuantity1,
                                      String linkQuantity2,
                                      String numSharedLinks,
                                      String numEqualConfScores,
                                      String location) throws SecurityException {

        boolean success1 = false;
        boolean success2 = false;

        // create random floats for confidence scores (i.e., measure values)
        Random random = new Random();
        float measure;

        // convert input strings to integers
        int quantity1 = Integer.parseInt(linkQuantity1);
        int quantity2 = Integer.parseInt(linkQuantity2);
        int numShared = Integer.parseInt(numSharedLinks);
        int numEqualCS = Integer.parseInt(numEqualConfScores);

        // variable to iterate through both files
        int i = 1;

        // ArrayList to hold confidences scores for shared matches
        List<Float> sharedConfScores = new ArrayList<Float>();

        // make sure number of shared links is less
        // than total number of links in either file
        if (numShared > quantity1 || numShared > quantity2) {
            System.err.println("Number of shared links is greater than total number"
                    + " of links in one of the files.");
            return (success1 && success2);
        }

        // make sure number of shared links with equal confidence
        // scores is not greater than total number of shared links
        if (numEqualCS > numShared) {
            System.err.println("Number of shared links with equal confidence scores"
                    + " is greater than total number of shared links.");
            return (success1 && success2);
        }

        // create 'alignmentOutput' folder in home directory
        File applicationDir = new File(location);
        if (!applicationDir.exists()) {
            if (!applicationDir.mkdir()) {
                throw new SecurityException("Unable to create 'alignmentOutput' " +
                                            "folder in user directory");
            }
        }

        FileWriter fw = null;
        BufferedWriter out = null;

        // write first file
        try {
            fw = new FileWriter(location + outputFile1);
            out = new BufferedWriter(fw);

            // write header
            out.write("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n");
            out.write("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n");
            out.write("\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n");
            out.write("\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n");
            out.write("\t xmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n\n");
            out.write("  <Alignment>\n");

            // write content
            for (; i<=quantity1; i++) {

                measure = random.nextFloat();               // an ineloquent way of avoiding
                if (measure < 0.001) { measure += 0.001; }  // instances of scientific notation
 
                // if current match is less than number of shared matches with equal
                // conf scores, store measure so that it can be used in second file
                if (i<=numEqualCS) { sharedConfScores.add(measure); }

                out.write("\t<map>\n");
                out.write("\t  <Cell>\n");
                out.write("\t\t<entity1 rdf:resource=\"<http://source.org/resource/" + i + "\"></entity1>\n");
                out.write("\t\t<entity2 rdf:resource=\"<http://target.org/resource/" + i + "\"></entity2>\n");
                out.write("\t\t<relation>http://www.w3.org/2002/07/owl#sameAs</relation>\n");
                out.write("\t\t<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" +
                        Float.toString(measure) + "</measure>\n");
                out.write("\t  </Cell>\n");
                out.write("\t</map>\n");
            }

            // write footer
            out.write("  </Alignment>\n");
            out.write("</rdf:RDF>\n");

            out.close();

            success1 = true;
        } catch(IOException ex) {
            System.err.println("Unable to write file to disk: " + ex);
        }

        // write second file
        try {
            fw = new FileWriter(location + outputFile2);
            out = new BufferedWriter(fw);

            // write header
            out.write("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n");
            out.write("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n");
            out.write("\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n");
            out.write("\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n");
            out.write("\t xmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n\n");
            out.write("  <Alignment>\n");

            // write shared links
            for (int j=1; j<=numShared; j++) {

                // if there are shared matches with equal conf scores, use the
                // conf scores from sharedConfScores before creating new values
                if (j <= sharedConfScores.size()) {
                    measure = sharedConfScores.get(j-1);
                } else {
                    measure = random.nextFloat();
                    if (measure < 0.001) { measure += 0.001; }
                }

                out.write("\t<map>\n");
                out.write("\t  <Cell>\n");
                out.write("\t\t<entity1 rdf:resource=\"<http://source.org/resource/" + j + "\"></entity1>\n");
                out.write("\t\t<entity2 rdf:resource=\"<http://target.org/resource/" + j + "\"></entity2>\n");
                out.write("\t\t<relation>http://www.w3.org/2002/07/owl#sameAs</relation>\n");
                out.write("\t\t<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" +
                        Float.toString(measure) + "</measure>\n");
                out.write("\t  </Cell>\n");
                out.write("\t</map>\n");
            }

            // determine how many more links to write
            int independentLinks = i+quantity2-numShared;

            // write independent links
            for (; i<independentLinks; i++) {

                measure = random.nextFloat();
                if (measure < 0.001) { measure += 0.001; }

                out.write("\t<map>\n");
                out.write("\t  <Cell>\n");
                out.write("\t\t<entity1 rdf:resource=\"<http://source.org/resource/" + i + "\"></entity1>\n");
                out.write("\t\t<entity2 rdf:resource=\"<http://target.org/resource/" + i + "\"></entity2>\n");
                out.write("\t\t<relation>http://www.w3.org/2002/07/owl#sameAs</relation>\n");
                out.write("\t\t<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">" +
                        Float.toString(measure) + "</measure>\n");
                out.write("\t  </Cell>\n");
                out.write("\t</map>\n");
            }

            // write footer
            out.write("  </Alignment>\n");
            out.write("</rdf:RDF>\n");

            out.close();

            success2 = true;
        } catch(IOException ex) {
            System.err.println("Unable to write file to disk: " + ex);
        }

        return (success1 && success2);
    }
}
