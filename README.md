# Silk Visualisations
Provides a set of statistical visualisations for Alignment files produced by the [Silk Link Discovery Framework](http://www.assembla.com/spaces/silk/wiki)


The Silk Link Discovery Framework helps automate the process of finding relationships between items within the Semantic Web. It can produce large numbers of "candidate" matches based on user-specified criteria. The aim of this project is to provide an overview of match output and ability to compare results between entire files. This can help users better understand the data being queried, and lead to a more efficient work-effort.

In the spirit of the Semantic Web, this project is implemented as a web application using open source technologies. The three visual models currently implemented allow users to view and compare (1) the number of links and distribution of similarity scores, (2) shared versus independent links between two files, and (3) for shared links, a view of how similarity scores differ.

A working example of the project can be found at: [http://silk-visualisations.appspot.com/](http://silk-visualisations.appspot.com/)

A WAR file of the project can be downloaded here: [Silk Visualisations 1.2](/downloads/tgiunipero/silk-visualisations/silk-visualisations-1.2.war)

* [Overview](#overview)
* [Visual Models](#visual-models)
* [System Architecture](#system-architecture)
* [Evaluation](/tgiunipero/silk-visualisations/wiki/Evaluation)
* [Scalability](/tgiunipero/silk-visualisations/wiki/Scalability)
* [About](/tgiunipero/silk-visualisations/wiki/About/)


## Overview

This project focuses on a tool that helps automate the process of identifying links between structured data, called the [Silk Link Discovery Framework](http://www4.wiwiss.fu-berlin.de/bizer/silk/). It applies user-defined similarity metrics to perform instance matching between data items from two different sets. When the data sets being compared are very large, Silk has the potential of generating a sizeable number of candidate links. This can lead to a cumbersome evaluation, as many links need to be examined manually in order to determine their validity. The purpose of this project is to aid in this evaluation by producing several visualisations that provide a statistical view on Silk output.

### Motivation

Silk users set up a “job” by creating a Link Specification that explicitly defines similarity metrics between entities of heterogeneous data sets. This task however often requires a familiarity with the data on both sides of the comparison in order to accurately assess whether a job will produce desired results. Since there is no gold standard for determining whether a match between two data sets is valid, users are obliged to examine results manually. Hence, if a user examines results and decides that the Link Specification needs to be recalibrated, he or she makes modifications, then reruns the job.

This “trial by error” process can be cumbersome, especially if output files contain thousands of links. Some questions a user might encounter are:

* _Why do some invalid matches receive a high confidence score?_
* _Why are some valid matches receiving a low confidence score?_
* _Does my Link Specification capture all of the matches I am looking for?_
* _In what ways has the data changed that will affect the accuracy of my Link Specification?_

### Visualisation Data

Silk output files can be produced in either [N-Triples](http://www.w3.org/2001/sw/RDFCore/ntriples/) or [Alignment](http://alignapi.gforge.inria.fr/format.html) format. The Silk documentation and several of the example Link Specifications use the N-Triples format for matches that receive a high confidence score, and so are considered valid without the need to examine them manually. They can be readily published or added to a triple store. Alignment format can be applied to matches whose confidence scores fall within an “uncertain” range, and would consequently call for further examination. The following listings demonstrate what an example match might look like in N-Triples and Alignment formats, respectively.

##### N-Triples

    <http://dbpedia.org/resource/United_States_Agency_for_International_Development>
    <http://www.w3.org/2002/07/owl#sameAs>
    <http://logd.tw.rpi.edu/source/data-gov/dataset/92/value-of/agency/US_Agency_for_International_Development> .

##### Alignment

    <Cell>
      <entity1 rdf:resource="http://dbpedia.org/resource/United_States_Agency_for_International_Development"></entity1>
      <entity2 rdf:resource="http://logd.tw.rpi.edu/source/data-gov/dataset/92/value-of/agency/US_Agency_for_International_Development"></entity2>
      <relation><http://www.w3.org/2002/07/owl#sameAs></relation>
      <measure rdf:datatype="http://www.w3.org/2001/XMLSchema#float">0.902</measure>
    </Cell>

Because the main purpose of the visualisations is to help understand how adjustments to Link Specification matching criteria can affect the output on given data, the focus in this project is on the Alignment format, which records a confidence score for each match.

The structure of the data in set **S** can be condensed as: **S { ei, ej, ck }** where **ei** and **ej** represent the subject and object of the match, and **ck** represents the confidence score. Working with these three elements, the following information was deemed useful:

1. A representation of the quantity of matches and distribution of confidence scores
1. A representation of shared versus independent matches
1. For shared links, a view of how confidence scores differ

The first point is valid for one or more sets; points 2 and 3 however would require a comparison of two sets. Use cases supporting the need for the above three points are outlined as follows.

### Use Cases

A primary use case for comparison of two output files is for scenarios in which many occurrences of valid matches exist, but these are disguised by numerous discrepancies between the strings or words being compared. In this case, the Link Specification would need to be optimised by applying one or more [transformation functions](http://www.assembla.com/spaces/silk/wiki/Transformation) in order to get the compared strings into a similar state.

Iterating through a series of transformation changes might show a trend in the increase of confidence scores between shared matches. Being able to identify this trend between job runs could help a user track improvement and avoid instances where a transformation change leads to a decrease of confidence score values. A view of how confidence scores differ between shared matches would provide this.

Another use case involves not modifications to the Link Specification, but to the data. For example, if Silk has produced an output file, and the data from the source, target, or both sets has changed, a view of independent matches between the two files could help uncover which of the two sets has changed, to what extent, and if necessary, offer insight into how the Link Specification could be adjusted to accommodate the change. A representation of shared versus independent matches would provide this.

Regardless of whether a comparison between two files is in order or not, this work contends that a general overview of Silk output results is desirable. For example, if the user has an idea of how large the data being queried from both sets is, or the number of matches he or she expects to find, then being able to discern the number of matches and distribution of confidence scores can allow him or her to immediately gain a relative insight into whether the output is successful. A representation of match quantity and confidence score distribution would provide this.

### Discussion

Good visual tooling is founded on a need for specific information which can be better relayed graphically than textually. Successful visualisations do not merely render data in a novel way, they serve a functional purpose. Unfortunately, this project suffers from the fact that the typical Silk user does not work with the Silk framework to view trends over large data. He or she uses the tool for a particular, often straight-forward, linking task, and either examines “uncertain” matches manually, or, if the numbers are high, eliminates them altogether from the result pool. The “usefulness” of the presented visualisations becomes a question of whether the visualisations fit into a user’s workflow so as to become a contributing factor to his or her work effort.

Were the presented visualisations to be integrated into [Silk Workbench](http://www.assembla.com/spaces/silk/wiki/Silk_Workbench), the amended workflow might resemble the diagram below, in which the user has the option to inspect a visual representation of the data in tandem with individual links.

![Diagram of Workbench amended workflow](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/workbench_amended_workflow.png)

### Future Work

Further work on this project would require feedback from the Silk user base to determine whether the implemented set of visualisations is most appropriate, and if so, how the visualisations could be improved.

#### Enhanced Interactivity

The visualisations in their present state are reasonably successful in providing an overview of the data. However, in order to be truly helpful to the Silk user, they must provide functionality that lets him or her control and explore the data. The following ideas are candidates for enhanced interaction:

* **“Drill-down” functionality for viewing selected matches.** This would require enabling a selection of matches, and then displaying the textual data in a separate table. Selection could be implemented in the form of a horizontal and/or vertical slider, or as a lasso tool, enabling the user to click and drag over a specific range. Otherwise, simple “on click” functionality could suffice, whereby clicking the column/row/dot triggers the table display. Furthermore, the table could allow users to flag individual matches as valid or invalid, similar to the [Evaluation component of Silk Workbench](http://www.assembla.com/spaces/silk/wiki/Evaluation).

* **Confidence score filter.** This could be implemented as a slider adjacent to the Grouping Number slider, so that users are able to hone in on a specific range of data. Otherwise, a more elaborate alternative would be a form of zoom functionality; this could be particularly appropriate for the scatterplot.

* **URI lookup.** The ability to locate a specific match within the visualisation could be a desirable feature. URI lookup could be implemented as a filterable text box: as the user types in a string, all occurrences on the graphic are dynamically highlighted. This could be supplemented with a toggle allowing the selection of only source or target URIs.

#### Improved Design

The visualisations themselves can be improved. In hindsight, the “rainbow effect” used by the scatterplot is not optimal, as the graph quickly becomes overwhelmed by larger circles when displaying numerous matches with equal confidence scores. When the circles overlap, their opacity increases to the extent that they have the adverse effect of masking singular dots within their vicinity. A more suitable representation would be a heat map, in which multiple instances are rendered using a different colour.

Edward Tufte, in his book, [The Visual Display of Quantitative Information](http://www.edwardtufte.com/tufte/books_vdqi), provides examples of bar chart and scatterplot redesign, in which the data-ink ratio is maximised, and minor changes to conventional representations are invoked to provide additional statistical information. For example, the scatterplot axes could be modified to indicate confidence score ranges for the two files (shown below). The [five-number summary](http://en.wikipedia.org/wiki/Five-number_summary) could also be represented through minor shifts in the lines drawn for each axis.

![Image of scatterplot axes as ranges](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/scatterplot-ranges.png)

Finally, an assessment of the visualisations’ graphical integrity would be recommended. There is a possible point of confusion surrounding the interpretation of the rotated histogram. The processing task for this model involves comparing the matches of a given row from File 1 with all of the matches from File 2. This task is then repeated in the direction of File 2 to File 1. A “shared match” occurs when the source and target URIs are identical between files. Shared matches are therefore counted twice: once for each file occurrence. Because of this, the ratio between red and blue areas and the purple area is misleading: if the user assumes that the visualisation shows a comparison of “all shared matches to all independent matches occurring in two files,” he or she will misinterpret the graphic. As such, this model comes close to violating Tufte’s rule that data representation should not be misleading. This is more formally expressed as the "Lie Factor":

> _Lie Factor = (size of effect shown in graphic) / (size of effect in data)_

in which the graphic distorts an effect taken from the data. Further usability studies could target this issue, and a possible remedy would be to include a functional description of the visualisation within the interface.


## Visual Models

Data used by the visualisations is sent to the client in JSON format, and is stored in global variables which are declared in script.js.

    var fileData = [];        // array for file data returned from server
    var sketchData = [];      // array for sketch data returned from server
    var lowerThreshold;       // minimum confidence score from user data
    var upperThreshold;       // maximum confidence score from user data

    var groupingNumber=70;    // number of 'buckets' used to compartmentalise
                              // data. Depending on the sketch, this number
                              // translates to the total number of rows or
                              // columns displayed on the canvas

    // arrays to contain file comparison data
    var leftUniqueCount = [];       
    var rightUniqueCount = [];
    var leftSharedMatchCount = [];
    var rightSharedMatchCount = [];
    var confScoreDelta = [];

Each of the three visual models uses a unique set of data. The histogram renders data from the `sketchData` array; the rotated histogram renders data from `leftUniqueCount`, `rightUniqueCount`, `leftSharedMatchCount`, and `rightSharedMatchCount`; the scatterplot renders data from the `confScoreDelta` array.

All three visual models rely on the `lowerThreshold` and `upperThreshold` values - these define the confidence score range, which is dynamically set on the server-side based on the minimum and maximum values of the uploaded file(s).

Of particular value to the histogram and rotated histogram with central axis is the “grouping number,” which specifies the number of columns (histogram) or rows (rotated histogram) displayed on the canvas. This value defaults to 70, but can be adjusted by the user with the help of a slider widget provided in the interface. The grouping number is also used on the server-side by the `parseData` method contained in the `DataParser` class; it is used with the `lowerThreshold` and `upperThreshold` values to determine the confidence score range per grouping.

    /**
     * Organises data from selected files into a multi-dimensional array *
     * (array of ArrayLists). Each ArrayList, or 'grouping' will contain *
     * confidence score values pertaining to that grouping's range.      */
     public void parseData(ArrayList fileNames, HashMap uploadedFiles) {
         // Get range for confidence score thresholds
         float thresholdRange = (upperThreshold - lowerThreshold);
         // Specify confidence score range for each grouping
         float confidenceScoreRangePerGrouping = thresholdRange/groupingNumber;

### Prototype 1: Histogram

The histogram represents the quantity of matches and distribution of confidence scores. The image below shows an example depicting two files. (Red and blue represent each file; purple signifies an overlap.)

![Prototype 1: Histogram](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch1.png)

The data for the histogram is rendered by looping through elements within the `sketchData` array. Each element of the `sketchData` array is an array that represents an uploaded file. The size of each file array is equal to the above-mentioned grouping number, and contains “grouping” arrays which serve as buckets to contain matches whose confidence scores fall within the given range. The structure of the 3-dimensional `sketchData` array is shown below with sample data. The histogram loops through each grouping array and displays a rectangle whose height is determined by the size of the grouping array. The width of each column is determined by the `getColumnWidth` method (contained in shared.pde), which is also necessarily affected by the grouping number.

    [                                                 // sketchData array
      [                                               // first file
        [                                             // bucket 1
          Object { e1=17, e2=15, c=0.503 },
          Object { e1=27, e2=24, c=0.5 }
        ],
        [                                             // bucket 2
          Object { e1=24, e2=21, c=0.513 },
          Object { e1=123, e2=29, c=0.511 },
          Object { e1=195, e2=15, c=0.509 },
          Object { e1=15, e2=58, c=0.510 }
        ],
        [                                             // bucket 3
          Object { e1=1, e2=1, c=0.518 },
          Object { e1=46, e2=37, c=0.518 },
          Object { e1=88, e2=57, c=0.519 }
        ],
        [ ... ]                                       // more buckets
      ],
      [ ... ]                                         // second file
    ]


The following code segment is contained within prototype1.pde’s draw method and is responsible for rendering the histogram data.

    /* Render the data */
       noStroke();
       for (int i=0; i<currentlySelected.length; i++) {
           int c = currentlySelected[i].selectionColour;       // get colour
           fill(colours.get(c));                               // set bar colour
           for (int j=0; j<sketchData[i].length; j++) {
               ArrayList currentColumn = sketchData[i][j];
               columnSize = currentColumn.length;
               if (columnSize != 0) {
                   rect(LEFT_BOUND+1 + (j*columnWidth),        // x-coordinate         
                   BOTTOM_BOUND - (columnSize * unitHeight),   // y-coordinate
                   columnWidth-1,                              // width
                   (columnSize * unitHeight) -2);              // height
               }
           }
       }

### Prototype 2: Rotated Histogram with Central Axis

The rotated histogram with central axis displays a view of shared versus independent matches. The image below portrays an example of the implementation showing a comparison of the shared and independent matches of two files. Red and blue bars represent independent matches for the first and second file, respectively; purple bars indicate matches that occur in both files.

![Prototype 2: Rotated Histogram with Central Axis](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch2.png)

This visualisation operates by iterating through four arrays of “file comparison” data (`leftUniqueCount`, `rightUniqueCount`, `leftSharedMatchCount`, and `rightSharedMatchCount`) to represent the size of each element as a rectangle on the canvas. The size of each array is equal to the grouping number, and each element of the array is an integer that corresponds to the number of matches occurring within the given confidence score range. The listing below shows how the draw method in prototype2.pde creates the purple bars along the central axis. Once the shared matches are rendered, the function iterates through independent matches (`leftUniqueCount`, `rightUniqueCount`) in a similar way to display rectangles that are displaced by the positions of the purple bars.

    /* Display shared matches */
       noStroke();
       fill(colours.get(2));    // set bar colour to purple
       // left file
       for (int i=0; i<rowNumber; i++) {
           if (leftSharedMatchCount[i] != 0) {
               rowWidth = leftSharedMatchCount[i] * unitWidth;
               // x-coordinate
               rect(median - rowWidth,
               // y-coordinate
               BOTTOM_BOUND - rowHeight - rowRanges[i] - 1,
               rowWidth,        // width
               rowHeight-1);    // height
           }
       }
       // right file
       for (int i=0; i<rowNumber; i++) {
           if (rightSharedMatchCount[i] != 0) {
               rowWidth = rightSharedMatchCount[i] * unitWidth;
               // x-coordinate
               rect(median,
               // y-coordinate
               BOTTOM_BOUND - rowHeight - rowRanges[i] - 1,
               rowWidth,        // width
               rowHeight-1);    // height
           }
       }

### Prototype 3: Scatterplot

The scatterplot displays differences in confidence scores for matches that occur in two files. (These are synonymous with the “shared matches” from the rotated histogram.) An image of the implementation is displayed below.

![Prototype 3: Scatterplot](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch3.png)

The scatterplot renders a portion of the “file comparison” data which is contained in the `confScoreDelta` array. Elements in the `confScoreDelta` array contain three pieces of data:

1. confidence scores as x-y coordinates
1. the number of matches exhibiting the confidence scores in (1)
1. keys for entity URI pairs of each recorded match

The listing below shows an example element of the `confScoreDelta` array, indicating that there are 3 matches exhibiting a confidence score of “0.951” in the first file, and “0.902” in the second file. The keys for entity URI pairs are: “4,4,” “13,12,” and “19,17.”

    ["0.951,0.902",3,"4,4|13,12|19,17"]

The keys are used for performing an AJAX call to the server to retrieve the URIs related to the given matches (this is the on-hover functionality for the scatterplot). The URIs are maintained on the server-side in two `ArrayList`s (one for source URIs and another for target URIs) which are stored as class variables in the `AlignmentFile` class.

The scatterplot renders the data by looping through the `confScoreDelta` array and displaying each confidence score pair as a point within the cartesian plane. When there are multiple matches exhibiting equal confidence scores, the implementation uses a “rainbow effect” that shows a maximum of five, increasingly faint, consecutive circles. This effect scales according to the largest quantity. In the above image, this is demonstrated in the upper right corner (where confidence scores are [1.0, 1.0]), and represents a maximum number of 62 matches. The below listing shows the `for` loop contained in prototype3.pde’s draw method which is responsible for iterating through the `confScoreDelta` array and rendering points on the graph.

    for (int i=0; i<confScoreDelta.length; i++) {
        String item = confScoreDelta[i][0];
        token = item.indexOf(",");
        leftScore = item.substring(0,token);
        rightScore = item.substring(token+1);
        xOffset = leftScore - lowerThreshold;
        yOffset = rightScore - lowerThreshold;
        xCoord = LEFT_BOUND + (xOffset/thresholdRange*widthInPixels);
        yCoord = BOTTOM_BOUND - (yOffset/thresholdRange*heightInPixels);
        quantity = confScoreDelta[i][1];
        ...
        // render the data
        if (quantity == 1) {
            ellipse(xCoord, yCoord, 4, 4);
        } else {
            ellipse(xCoord, yCoord, 4, 4);
            noStroke();
            alpha = 80;
            diameter = 15;
         /* Suppose maxQuantity == 500;           *
          * Quantity grouping = 500/5 => 100, so: *
          *     qty           diameter            *
          *     1               4 px              *
          *     2-100          15 px              *
          *     101-200        25 px              *
          *     201-300        35 px              *
          *     301-400        45 px              *
          *     401-500        55 px              */
            for (c=2; c<=quantity; c+=maxQuantity/5) {
                fill(colours.get(2), alpha);
                ellipse(xCoord, yCoord, diameter, diameter);
                diameter += 10;
                if (alpha <= 40) { alpha -= 10; }
                else { alpha -= 20; }
            }
            fill(colours.get(2));
            stroke(colours.get(2), 200);
        }
    }

### On-hover functionality

On-hover functionality is implemented in the scatterplot as an AJAX call to the server, and allows the user to view the source and target URIs for each match. The functionality is implemented in five steps:

1. The Processing file, prototype3.pde, retrieves the entity URI pair keys from the point being hovered over, and uses them in a call to a JavaScript function, `getMatches`.
1. The `getMatches` function, in script.js, formulates a query string from the match list (it concatenates a maximum of five instances), then sends an asynchronous “getMatches” request to the server.
1. The server’s `DataServlet` parses the query string, and uses the DataParser’s `getURIs` method to identify each source and target URI within the AlignmentFile’s `Entity1Pool` and `Entity2Pool`, respectively.
1. The `DataServlet` sends the URIs to the client; the `getMatches` function receives them and calls the Processing file’s `displayMatches` function.
1. The `displayMatches` function renders the URIs in the upper left corner of the canvas.


## System Architecture

The application is based on a client-server model. The architecture looks as follows:

![diagram of application architecture](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/application-component-diagram.png)

### Client-Side

The client-side is responsible for the user interface, including the Processing scripts for each visualisation, jQuery interface widgets such as the upload widget, tabbed panel, slider, dialogs and buttons, and general HTML, JavaScript and CSS.

#### Visualisations

The implementation of the 3 visual models is discussed in [Visual Models](/tgiunipero/silk-visualisations/wiki/Visual-Models).

#### User Interface

Much of the interface functionality is available in the region to the left of the canvas, and was implemented with an aim to avoid unnecessary confusion involved in the activity of visualising one’s files. There is a 3-step process which provides visual cues to help a user interact with the system. In the image below, the three panels show the interface’s left region in various states of interaction.

![image showing 3 interface steps](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/interface-steps.png)

These steps are described as follows.

**Step 1:** The opening state of the interface displays an “Import files” button. The button affords clicking, and so the user clicks the button, uploads relevant files into the displayed dialog, and confirms his or her selection to close the dialog. (A progress bar displays while files are being uploaded.)

**Step 2:** Successfully uploaded files are displayed beneath the “Import files” button, and new content appears in the lower region of the interface. Instructions read, _“First select one or two files (click on the file names above), then click ‘Render chart’ to view their data in the canvas.”_ The “Render chart” button is in a deactivated state. The user selects files by clicking on the file names.

**Step 3:** The interface is in a state where the background for file names is highlighted red or blue to indicate their selection. The “Render chart” button becomes active. The user clicks the “Render chart” button to initiate the visualisations.

When the visualisations are rendered, the colour of the data corresponds to the background colours of the file names (red and blue). The user is able to click other tabs and immediately view the visualisation pertaining to that tab.

### Server-Side

The server-side is primarily responsible for handling data: parsing, storing, packaging and sending. The following provides a summary of the classes included in the server-side. As indicated in the image above showing the system architecture, classes are grouped into three packages: (1) Servlet, (2) Alignment, and (3) Data.

#### Servlet package

* **UploadServlet:** This servlet handles file uploads and deletion. It iterates through uploaded files and uses the `AlignmentParser` to convert them into `AlignmentFile` objects. `AlignmentFile` objects are then stored in a session-scoped HashMap. This servlet also contains a private method, `getUniqueFileName`, which determines a file name that can be used as a unique identifier. When a request to delete a file is received, this servlet locates the file in the session-scoped HashMap and removes it.

* **DataServlet:** The `DataServlet` handles two requests, “`parseData`,” and “`getMatches`.” The “`parseData`” request occurs when a user clicks the “Render chart” button in the interface. The `DataServlet` retrieves the session-scoped HashMap containing `AlignmentFile` objects, as well as a session-scoped `DataParser` object. It then makes successive calls to the `DataParser` object’s methods using the HashMap and information from the query to (1) set the grouping number and confidence score thresholds, (2) parse data, and, if two files were selected by the user, (3) prepare file comparison data. It collects this data in JSON format and sends it to the client. The “`getMatches`” request responds to an AJAX call made from the on-hover functionality in the scatterplot. It uses the `DataParser` to look up URIs for the specified pairs of match keys, then returns them to the client.

* **GeneratorServlet:** This servlet responds to form input from _WEBROOT_/file-generator/index.html, which enables generation of Alignment files used for testing purposes. It creates a folder named “alignmentOutput” in the user’s home directory, and outputs generated Alignment Files there. It calls `AlignmentFileGenerator` to create a pair of sample Alignment files.

#### Alignment package

* **AlignmentFile:** This class is used to represent an uploaded file. It stores four pieces of data: (1) the source URIs (content between `<entity1>` tags), (2) the target URIs (content between `<entity2>` tags), (3) the relation (content between the `<relation>` tags), and (4) matchings, which is a List of strings that contain the source and target URI keys, and the related confidence score. The `AlignmentFile` class also contains two class variables, `Entity1Pool` and `Entity2Pool`, which store source and target URIs across multiple files in a user session. (This ensures that URIs are not confused or overwritten between files.)

* **AlignmentParser:** The `AlignmentParser` is responsible for parsing uploaded Alignment files into `AlignmentFile` objects.

* **AlignmentFileWriter:** The `AlignmentFileWriter` contains two overloaded methods, called `writeToDisk`. The first method serialises an `AlignmentFile` object and stores it to disk. (This functionality is currently not used.) The second method is called by the `GeneratorServlet` to create two sample files in Alignment format and store them to disk.

#### Data package

* **DataParser:** This class is responsible for back-end data processing. Its tasks are to:

   * parse `AlignmentFile` objects into a multidimensional array that is conducive to representation on the client-side
   * set confidence score thresholds based on uploaded data
   * set the grouping number (on the server-side) based on user choice
   * generate file comparison data based on a 2-file selection
   * fetch stored URIs based on key values
   * prepare all data in JSON format

* **RdfPrefixMap:** This class is an extended HashMap that stores RDF namespace prefixes as key-value pairs. It is used for the on-hover effect in the scatterplot to make a best-effort attempt to replace the full URI with an RDF namespace prefix. `RdfPrefixMap` stores 100 popular namespaces, according to: <http://richard.cyganiak.de/blog/2011/02/top-100-most-popular-rdf-namespace-prefixes/>.


## Evaluation

The implementation was tested using several Link Specifications from [LATC's 24/7 Interlinking Platform](https://github.com/LATC/24-7-platform). The goal was to ascertain whether the visualisations could uncover information that could be deemed helpful given a particular use case (such as those discussed in the [Overview](/tgiunipero/silk-visualisations/wiki/Overview)). While this evaluation is not exhaustive, it successfully demonstrates that the visualisations can provide insight into the data and applied similarity measures.

The presented example involves querying [LinkedGeoData](http://linkedgeodata.org/) and the [Climb Dataincubator](http://ckan.net/package/data-incubator-climb) (a collection of data from [UKClimbing.com](http://www.ukclimbing.com/)) to obtain a listing of supermarkets within a given radius of climbing venues. Two Silk jobs were run; the first identified supermarkets within a 15 km radius of climbing venues, while the second extended the radius to 20 km. The visualisations provided useful insight into the WGS84 distance measure, and helped identify “curious” matches that would warrant inspection.

Unsurprisingly, the histogram below shows that (1) there are approximately two thirds the number of supermarkets within a 15 km radius of climbing venues compared with that of a 20 km radius, and (2) as the distance from the venue increases, so does the number of supermarkets within that distance. This is indicated by the fact that the graphic tapers to the left - the WGS84 distance measure normalises the recorded distance so that a high confidence score signifies a short distance between two locations; distances beyond the specified threshold receive a zero value.

![Sketch 1](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch1-eval.png)

The rotated histogram and scatterplot highlight a unique property of the WGS84 distance measure: as the distance threshold increases, so do the confidence scores assigned to a given distance. In the below image, the blue portion represents supermarket-climbing venue pairs that are between 15 and 20 km apart from each other. Since all of the matches occurring in the 15 km file also occur in the 20 km file, and since the confidence score distribution would remain the same between the two files, we can deduce that matches with confidence scores above 0.5 in the left file would have scores just above 0.62 in the right file.

![Sketch 2](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch2-eval.png)

The rotated histogram also points out several instances of matches occurring in the right file which fall within the confidence range of matches that occur in the left file. (One such instance is denoted by the pop-up in the above image.) It could be worthwhile to investigate such matches as they are typically manifestations of a lack of understanding of either the similarity measures used in the Link Specification, and/or the data itself.

Finally, the scatterplot provides a clear representation of the mentioned “unique” property of the WGS84 distance measure. In the image below, a linear trend indicates that matches in the 20 km file receive a higher confidence score, but that the difference between the two files decreases as scores increase. In other words, the closer two locations are to one another, the more similar the confidence scores will be, regardless of the distance threshold.

![Sketch 3](https://github.s3.amazonaws.com/downloads/giuniperoo/silk-visualisations/sketch3-eval.png)

The above images suggest that useful information can be gathered from the data as well. For example, if there were relatively few supermarkets within a 5 km radius of venues, but the number jumped significantly between 5 and 10 km, this would become clear through use of the visualisations, as the confidence score is directly proportional to distance.

