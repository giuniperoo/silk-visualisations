package org.alignment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author tgiunipero
 */
public class AlignmentParser implements Serializable {

    private static final long serialVersionUID = 333334566269199033L;

    public static AlignmentFile parse(InputStream stream) {

        // file object to contain data
        AlignmentFile alignmentFile = new AlignmentFile();

        // references to class-wide containers for entity URIs of all loaded files
        List<String> entity1Pool = AlignmentFile.getEntity1Pool();
        List<String> entity2Pool = AlignmentFile.getEntity2Pool();

        try {
            LineIterator li = IOUtils.lineIterator(stream, "UTF-8");

            int startEntity1, endEntity1,
                startEntity2, endEntity2,
                startMeasure, endMeasure,
                startRelation, endRelation = 0;

            String entity1,
                   entity2,
                   measure,
                   line,
                   relation = null;

            try {
                while (li.hasNext()) {
                    line = li.nextLine();

                    if (line.contains("<entity1")) {

                        //get resource for entity 1
                        startEntity1 = line.indexOf("<entity1") + 23;
                        endEntity1 = line.indexOf("\"", startEntity1);
                        entity1 = line.substring(startEntity1, endEntity1);

                        // assume next line contains entity 2
                        if (li.hasNext()) {
                            line = li.nextLine();

                            if (line.contains("<entity2")) {

                                //get resource for entity 2
                                startEntity2 = line.indexOf("<entity2") + 23;
                                endEntity2 = line.indexOf("\"", startEntity2);
                                entity2 = line.substring(startEntity2, endEntity2);

                                // assume next line contains relation
                                if (li.hasNext()) {
                                    line = li.nextLine();

                                    if (line.contains("<relation") && relation == null) {

                                        // get relation URI (only performed once)
                                        startRelation = line.indexOf("<relation>") + 10;
                                        endRelation = line.indexOf("</relation>");
                                        if (endRelation > 0) {
                                            relation = line.substring(startRelation, endRelation);
                                            relation = relation.trim();
                                        } else {
                                            // if closing tag was not found, <relation>
                                            // is likely written to multiple lines
                                            relation = line.substring(startRelation);
                                            line = li.nextLine();
                                            endRelation = line.indexOf("</relation>");
                                            if (endRelation > 0) {
                                                relation += line.substring(0, endRelation);
                                                relation = relation.trim();
                                            } else {
                                                relation += line;
                                                line = li.nextLine();
                                                endRelation = line.indexOf("</relation>");
                                                if (endRelation > 0) {
                                                    relation += line.substring(0, endRelation);
                                                    relation = relation.trim();
                                                } else {
                                                    System.err.println("Unable to identify relation");
                                                }
                                            }
                                        }
                                        alignmentFile.setRelation(relation);
                                    }

                                    if (li.hasNext()) {
                                        line = li.nextLine();

                                        while (!line.contains("<measure")) {
                                            line = li.nextLine();
                                        }

                                        // get measure value
                                        startMeasure = line.indexOf(">") + 1;
                                        endMeasure = line.indexOf("</measure>");
                                        if (endMeasure > 0) {
                                            measure = line.substring(startMeasure, endMeasure);
                                            measure = measure.trim();
                                        } else {
                                            // if closing tag was not found, <measure>
                                            // is likely written to multiple lines
                                            measure = line.substring(startMeasure);
                                            line = li.nextLine();
                                            endMeasure = line.indexOf("</measure>");
                                            if (endMeasure > 0) {
                                                measure += line.substring(0, endMeasure);
                                                measure = measure.trim();
                                            } else {
                                                measure += line;
                                                line = li.nextLine();
                                                endMeasure = line.indexOf("</measure>");
                                                if (endMeasure > 0) {
                                                    measure += line.substring(0, endMeasure);
                                                    measure = measure.trim();
                                                } else {
                                                    System.err.println("Unable to identify measure");
                                                }
                                            }
                                        }

                                        // ensure that all values have been assigned
                                        if (entity1 != null &&
                                            entity2 != null &&
                                            measure != null) {

                                            int idx1, idx2;

                                         /* entity 1 */
                                            alignmentFile.getEntity1Array().add(entity1);
                                            // if already listed in class-wide list...
                                            if (entity1Pool.contains(entity1)) {
                                                // get the index
                                                idx1 = entity1Pool.indexOf(entity1);
                                            } else {
                                                // otherwise, add it to list
                                                AlignmentFile.getEntity1Pool().add(entity1);
                                                idx1 = entity1Pool.indexOf(entity1);
                                            }

                                         /* entity 2 */
                                            alignmentFile.getEntity2Array().add(entity2);
                                            // if already listed in class-wide list...
                                            if (entity2Pool.contains(entity2)) {
                                                // get the index
                                                idx2 = entity2Pool.indexOf(entity2);
                                            } else {
                                                // otherwise, add it to list
                                                AlignmentFile.getEntity2Pool().add(entity2);
                                                idx2 = entity2Pool.indexOf(entity2);
                                            }

                                            alignmentFile.getMatchings().add(idx1 + "," +
                                                    idx2 + "," + measure);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                IOUtils.closeQuietly(stream);
            }
        } catch (Exception ex) {
            System.err.println("Unable to parse file: " + ex);
        }

        return alignmentFile;
    }
}
