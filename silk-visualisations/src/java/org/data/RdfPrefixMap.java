package org.data;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Imports the top 100 most popular RDF namespace prefixes (Feb 2011)
 * (See: http://richard.cyganiak.de/blog/2011/02/top-100-most-popular-rdf-namespace-prefixes/)
 *
 * @author tgiunipero
 */
public class RdfPrefixMap extends HashMap<String, String> {

    private static final long serialVersionUID = -6890002928770139064L;

    public RdfPrefixMap() {

        this.put("http://xmlns.com/foaf/0.1/","foaf:");
        this.put("http://purl.org/dc/elements/1.1/","dc:");
        this.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#","rdf:");
        this.put("http://www.w3.org/2000/01/rdf-schema#","rdfs:");
        this.put("http://www.w3.org/2002/07/owl#","owl:");
        this.put("http://www.geonames.org/ontology#","geonames:");
        this.put("http://www.w3.org/2003/01/geo/wgs84_pos#","geo:");
        this.put("http://www.w3.org/2004/02/skos/core#","skos:");
        this.put("http://dbpedia.org/property/","dbp:");
        this.put("http://ontoware.org/swrc/swrc/SWRCOWL/swrc_updated_v0.7.1.owl#","swrc:");
        this.put("http://rdfs.org/sioc/ns#","sioc:");
        this.put("http://www.w3.org/2001/XMLSchema#","xsd:");
        this.put("http://dbpedia.org/ontology/","dbo:");
        this.put("http://purl.org/dc/elements/1.1/","dc11:");
        this.put("http://usefulinc.com/ns/doap#","doap:");
        this.put("http://dbpedia.org/property/","dbpprop:");
        this.put("http://purl.org/rss/1.0/modules/content/","content:");
        this.put("http://xmlns.com/wot/0.1/","wot:");
        this.put("http://purl.org/rss/1.0/","rss:");
        this.put("http://www.w3.org/2006/gen/ont#","gen:");
        this.put("http://dbpedia.org/resource/","dbpedia:");
        this.put("http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#","d2rq:");
        this.put("http://www.semanticdesktop.org/ontologies/2007/01/19/nie#","nie:");
        this.put("http://www.w3.org/1999/xhtml/vocab#","xhtml:");
        this.put("http://this.invalid/test2#","test2:");
        this.put("http://purl.org/goodrelations/v1#","gr:");
        this.put("http://purl.org/dc/terms/","dcterms:");
        this.put("http://www.w3.org/ns/org#","org:");
        this.put("http://www.w3.org/2006/vcard/ns#","vcard:");
        this.put("http://www.aktors.org/ontology/portal#","akt:");
        this.put("http://purl.org/dc/terms/","dct:");
        this.put("http://example.com/","ex:");
        this.put("http://rdf.freebase.com/ns/","fb:");
        this.put("http://www.ontotext.com/trree/owlim#","owlim:");
        this.put("http://sw.deri.org/2005/08/conf/cfp.owl#","cfp:");
        this.put("http://www.w3.org/2002/xforms/","xf:");
        this.put("http://purl.oclc.org/NET/sism/0.1/","sism:");
        this.put("http://www.w3.org/ns/earl#","earl:");
        this.put("http://purl.org/vocab/bio/0.1/","bio:");
        this.put("http://purl.org/reco#","reco:");
        this.put("http://gmpg.org/xfn/11#","xfn:");
        this.put("http://purl.org/media#","media:");
        this.put("http://dig.csail.mit.edu/TAMI/2007/amord/air#","air:");
        this.put("http://purl.org/dc/dcmitype/","dcmit");
        this.put("http://rdfs.org/ns/void#","void:");
        this.put("http://www.w3.org/2005/xpath-functions#","fn:");
        this.put("http://jena.hpl.hp.com/ARQ/function#","afn:");
        this.put("http://creativecommons.org/ns#","cc:");
        this.put("http://purl.org/cld/terms/","cld:");
        this.put("http://purl.org/vocab/vann/","vann:");
        this.put("http://ontologi.es/days#","days:");
        this.put("http://www.w3.org/2002/12/cal/ical#","ical:");
        this.put("http://www.w3.org/2006/http#","http:");
        this.put("http://www.kanzaki.com/ns/music#","mu:");
        this.put("http://www.w3.org/ns/sparql-service-description#","sd:");
        this.put("http://www.ordnancesurvey.co.uk/ontology/AdministrativeGeography/v2.0/AdministrativeGeography.rdf#","osag:");
        this.put("http://purl.org/NET/biol/botany#","botany:");
        this.put("http://www.w3.org/2002/12/cal/ical#","cal:");
        this.put("http://purl.org/ontology/similarity/","musim:");
        this.put("http://www4.wiwiss.fu-berlin.de/factbook/ns#","factbook:");
        this.put("http://purl.org/vocab/changeset/schema#","cs:");
        this.put("http://www.w3.org/2000/10/swap/log#","log:");
        this.put("http://purl.org/stuff/rev#","rev:");
        this.put("http://purl.org/swan/1.2/discourse-elements/","swande:");
        this.put("http://purl.org/ontology/bibo/","bibo:");
        this.put("http://purl.org/dc/qualifiers/1.0/","dcq:");
        this.put("http://rdfs.org/resume-rdf/","cv:");
        this.put("http://purl.org/ontomedia/core/expression#","ome:");
        this.put("http://purl.org/net/biblio#","biblio:");
        this.put("http://schemas.talis.com/2005/dir/schema#","dir:");
        this.put("http://ontologi.es/giving#","giving:");
        this.put("http://ontologies.smile.deri.ie/2009/02/27/memo#","memo:");
        this.put("http://okkam.org/terms#","ok:");
        this.put("http://purl.org/vocab/relationship/","rel:");
        this.put("http://purl.org/NET/c4dm/event.owl#","event:");
        this.put("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl#","ir:");
        this.put("http://purl.org/vocab/aiiso/schema#","aiiso:");
        this.put("http://schemas.talis.com/2005/address/schema#","ad:");
        this.put("http://dbpedia.org/resource/","dbr:");
        this.put("http://purl.org/ontology/co/core#","co:");
        this.put("http://purl.org/ontology/af/","af:");
        this.put("http://www.ontologydesignpatterns.org/cp/owl/componency.owl#","cmp:");
        this.put("http://www.rdfabout.com/rdf/schema/usbill/","bill:");
        this.put("http://www.w3.org/2007/rif#","rif:");
        this.put("http://www.w3.org/2001/XMLSchema#","xs:");
        this.put("http://www.w3.org/2000/10/swap/math#","math:");
        this.put("http://www.w3.org/2004/03/trix/rdfg-1/","rdfg:");
        this.put("http://purl.org/ontology/daia/","daia:");
        this.put("http://data.semanticweb.org/ns/swc/ontology#","swc:");
        this.put("http://www.holygoat.co.uk/owl/redwood/0.1/tags/","tag:");
        this.put("http://purl.org/swan/1.2/qualifiers/","swanq:");
        this.put("http://www.w3.org/1999/xhtml/vocab#","xhv:");
        this.put("http://purl.org/NET/book/vocab#","book:");
        this.put("http://d2rq.org/terms/jdbc/","jdbc:");
        this.put("http://purl.org/ontology/myspace#","myspace:");
        this.put("http://www.w3.org/2006/timezone#","tzont:");
        this.put("http://www.openrdf.org/config/repository/sail#","sr:");
        this.put("http://commontag.org/ns#","ctag:");
        this.put("http://www.w3.org/2007/uwa/context/deliverycontext.owl#","dcn:");
        this.put("http://ltsc.ieee.org/rdf/lomv1p0/vocabulary#","lomvoc:");

        // test
        this.put("http://inf.ed.ac.uk/resource/","inf:");
        this.put("http://example.org/resource/","example:");
        this.put("http://logd.tw.rpi.edu/source/data-gov/dataset/92/value-of/agency/","logd92:");
        this.put("http://logd.tw.rpi.edu/source/data-govt-nz/dataset/catalog/data.govt.nz/value-of/agency/","logdnz:");
        this.put("http://logd.tw.rpi.edu/source/data-gov-au/dataset/catalog/data.gov.au/value-of/agency/","logdau:");
        this.put("http://logd.tw.rpi.edu/source/data-gov-uk/dataset/catalog/data.gov.uk/value-of/department/","logduk:");
        this.put("http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/logd-million-dataset-challenge/value-of/government_agency/","logd:");

    }

    public static final HashMap<String, String> RDF_PREFIXES = new RdfPrefixMap();

    // Tries to match the input URI against one of the stored
    // prefixes. If no success, the input URI is returned.
    public static String getPrefix(String uri) {

        String prefix = uri;

        for (Iterator<String> it=RDF_PREFIXES.keySet().iterator(); it.hasNext();) {
            String key = it.next();

            if (uri.contains(key)) {

                prefix = prefix.replaceFirst(key, (String) RDF_PREFIXES.get(key));
                break;
            }
        }

        return prefix;
    }
}
