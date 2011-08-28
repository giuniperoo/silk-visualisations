package org.alignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tgiunipero
 */
public class AlignmentFile implements Serializable {

    private List entity1Array;
    private List entity2Array;
    private List matchings;
    private String relation;

    // class-wide containers for entity URIs of all loaded files
    private static List Entity1Pool = new ArrayList();
    private static List Entity2Pool = new ArrayList();


    // constructor
    public AlignmentFile() {
        entity1Array = new ArrayList();
        entity2Array = new ArrayList();
        matchings = new ArrayList();
    }

    public List getEntity1Array() {
        return entity1Array;
    }

    public List getEntity2Array() {
        return entity2Array;
    }

    public List getMatchings() {
        return matchings;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    /* Returns matchings in a format that is JSON-compliant
     *
     * The int parameter is used to specify an abridged
     * version of the measure values - it represents the
     * number of significant digits included in the
     * measure - useful for restricting the data size.
     *
     * A stringified matching will include the values for
     * entities 1 and 2, and the confidence score:
     *
     *      {"e1":2,"e2":5,"c":0.708}
     */
    public String getMatchingsAsString(int i) {

        i = i+2;    // shift 2 chars to account for '0.'

        String data = "";
        Iterator iter = getMatchings().iterator();

        while (iter.hasNext()) {
            String matching = (String) iter.next();
            String[] tokens = matching.split(",");
            String confScore = tokens[2];

            // abridge matching
            if (confScore.length() > i) {
                confScore = confScore.substring(0, i-1);
            }

            data += "{\"e1\":" + tokens[0] +   // entity 1
                    ",\"e2\":" + tokens[1] +   // entity 2
                    ",\"c\":"  + confScore +   // confidence score
                    "},";
        }

        // remove final ','
        data = data.substring(0, data.length()-1);

        return data;
    }

    public static List getEntity1Pool() {
        return Entity1Pool;
    }

    public static List getEntity2Pool() {
        return Entity2Pool;
    }
}