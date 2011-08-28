/* Placed in global scope so they can be accessed from all Processing files */

var fileData = [];              // array for file data returned from server
var sketchData = [];            // array to contain user data returned from server
var lowerThreshold;             // minimum confidence score from user data
var upperThreshold;             // maximum confidence score from user data

var groupingNumber=70;          // number of 'buckets' used to compartmentalise
                                // data. Depending on the sketch, this number
                                // translates to the total number of rows or
                                // columns displayed on the canvas

var leftUniqueCount = [];       // arrays to contain file comparison data
var rightUniqueCount = [];      //
var leftSharedMatchCount = [];  //
var rightSharedMatchCount = []; //
var confScoreDelta = [];        //

var currentlySelected = [];     // array to keep track of selected files in UI


/* Application-specific JavaScript functions */

// currently not used (limit specified in file upload widget)
function checkLimit() {
    if (fileData.length > 3) {

        // call jQuery dialog
        $('#limit').dialog({
            height: 124,
            width: 404,
            modal: true
        });
    }
}


// triggers the specified processing sketch to perform a redraw()
function drawCanvas(sketchId) {

    processingInstance = Processing.getInstanceById(sketchId);
    processingInstance.redraw();
}


// uses jQuery to perform an AJAX call to retrieve (up to) the
// first five matches whose confidence scores are specified by
// the function's input parameters
function getMatches(matchList) {

    // prepare data as an HTTP query string
    var queryString = '';

    // If the list contains more than 5 matches, just get the first 5...
    if (matchList.length > 5) {
        for (i=0; i<5; i++) {
            queryString += matchList[i] + '&';
        }

        // then attach remaining number of matches to query string
        queryString += 'remaining=';
        queryString += matchList.length-5;
    } else {
        for (i=0; i<matchList.length; i++) {
            queryString += matchList[i] + '&';
        }

        // remove final '&'
        queryString = queryString.substr(0, queryString.length-1);
    }

    // send request
    $.getJSON('getMatches', queryString,
        function(data) {

            processingInstance = Processing.getInstanceById('sketch3');
            processingInstance.displayMatches(data);
        });
}


function getSelectedTabIndex() {
    var $tabs = $('#tabs').tabs();
    return $tabs.tabs('option', 'selected');
}


function parseData() {

    // prepare data as an HTTP query string
    var queryString = "";

    // get names for selected files
    for (var i in currentlySelected) {
         var idx = currentlySelected[i].fileIdx;
         var fileName = fileData[idx].name;

        queryString += fileName + "&";
    }

    // add the grouping number
    queryString += groupingNumber;

    // send request and return jqXHR object
    return $.getJSON('parseData', queryString, function(data) {

        if (data != null && data.length > 1) {
            lowerThreshold = data[0].lowerThreshold;
            upperThreshold = data[0].upperThreshold;
            sketchData = data[1];

            // check if file comparison data is returned
            // if so, store it
            if (data.length == 3) {
                leftUniqueCount = data[2][0];
                rightUniqueCount = data[2][1];
                leftSharedMatchCount = data[2][2];
                rightSharedMatchCount = data[2][3];
                confScoreDelta = data[2][4];
            }
        } else { displayError(); }
    });
}


function removeData(data) {
    // get file name
    var fileName = data.url.substr(12, data.url.length);

    // remove data from local store
    for(var i in fileData) {
        if (fileData[i].name == fileName) {

            // check if file is currently selected
            // if so, remove it from array
            for (var j in currentlySelected) {
                if (currentlySelected[j].fileIdx == i) {
                    currentlySelected.splice(j, 1);
                }
            }

            // loop again to decrement any indexes that
            // are higher than the one being removed
            for (j in currentlySelected) {
                if (currentlySelected[j].fileIdx > i) {
                    var newIdx = --currentlySelected[j].fileIdx;
                    currentlySelected[j].fileIdx = newIdx;
                }
            }

            // enable/disable 'Render chart' button based
            // on whether there are any files still selected
            if (currentlySelected.length > 0) {
                $( "button" ).button( "option", "disabled", false );
            } else {
                $( "button" ).button( "option", "disabled", true );
            }

            fileData.splice(i, 1);

            break;
        }
    }
}


function renderChart() {

    // get tab index and sketch ID
    var tabIndex = getSelectedTabIndex();
    var sketchId = "sketch" + (tabIndex + 1);

    if (tabIndex > 0 && currentlySelected.length != 2) {

        $('#twoFileSelection').dialog({
            height: 145,
            width: 290,
            modal: true
        });

    } else if (currentlySelected.length > 0) {

        // set grouping number according to slider widget value
        groupingNumber = $('#grouping-slider').slider('value');

        // parse data
        var dataParsed = parseData();

        // when parsing is complete, render chart
        $.when( dataParsed )
            .then(
                function() {            // success
                    // hide loader
                    $("#loader").hide();

                    // redraw canvas
                    drawCanvas(sketchId);
                },
                function() {            // failure
                    // hide loader
                    $("#loader").hide();

                    // print error message
                    displayError();
                });

        // if tab description is currently displayed, remove it
        var tab = tabIndex+1;
        var tabDescriptionId = "#tab" + tab + "-description";
        if( $(tabDescriptionId).css('display') == 'block' ) {
            toggleDisplay(tabIndex);
        }

        // display animated gif while data is being processed
        showLoader();
    }
}


// store file data to global array
function storeFileData(data) {

    if (data.textStatus == "success") {

        for (var each in data.result) {
            fileData.push(data.result[each]);
        }
    }
}


function toggleDisplay(tabIndex) {

    var tab = tabIndex+1;
    var tabDescriptionId = "#tab" + tab + "-description";
    var sketchId = "#sketch" + tab;

    if( $(tabDescriptionId).css('display') == 'block' ) {
        $(tabDescriptionId).fadeOut('fast', function() {
            $(sketchId).fadeIn();
        });
    }
    else {
        $(sketchId).fadeOut('fast', function() {
            $(tabDescriptionId).fadeIn();
        });
    }
}


/* Functionality for toggling the user-uploaded files in the left column.  *
 * The user is allowed to select at most 2 files, which are highlighted by *
 * 2 colours.  When the file is selected, it must first be associated with *
 * its data contained in the fileData array.  A separate array of objects, *
 * called currentlySelected, keeps track of which files are selected.  It  *
 * is structured as follows:                                               *
 *                                                                         *
 *     [ { fileIdx: int , selectionColour: int } ]                         *
 *                                                                         *
 * ...where fileIdx represents the file index in fileData, and             *
 * selectionColour is an integer (i.e., either 0 or 1) that maps to the    *
 * colour applied to the selection.                                        *
 *                                                                         */
function toggleFileSelection(event) {

    // get the file name from the row id
    var fileName = event.target.parentNode.parentNode.id;

    // locate file in fileData
    var fileIdx = null;
    for (var file in fileData) {
        if (fileName == fileData[file].name) {
            fileIdx = file;
            break;
        }
    }

    // if the file is already selected, deselect it
    var removed = false;
    var classList = event.target.parentNode.parentNode.className.split(/\s+/);
    for (var i = 0; i < classList.length; i++) {
        if (classList[i] == 'red-box' || classList[i] == 'blue-box') {
            $(event.target.parentNode.parentNode).removeClass('red-box');
            $(event.target.parentNode.parentNode).removeClass('blue-box');
            $(event.target.parentNode.parentNode).addClass('unselected');

            // remove from currentlySelected
            for (var j in currentlySelected) {
                if (fileIdx == currentlySelected[j].fileIdx) {
                    currentlySelected.splice(j, 1);
                    break;
                }
            }

            // flag removed
            removed = true;
        }
    }

    if (!removed) {
        // if there are no selected files, apply first colour
        if (currentlySelected.length == 0) {
            $(event.target.parentNode.parentNode).removeClass('unselected');
            $(event.target.parentNode.parentNode).addClass('red-box');
            currentlySelected[0] = {'fileIdx' : fileIdx, 'selectionColour' : 0};

        // if there is one selected file, find out what color
        // is being used before applying the other color
        } else if (currentlySelected.length == 1) {

            var c = currentlySelected[0].selectionColour;
            $(event.target.parentNode.parentNode).removeClass('unselected');

            // if first color is already used, apply second color
            if (c == 0) {
                $(event.target.parentNode.parentNode).addClass('blue-box');
                c = 1;
            }
            // otherwise, apply first color
            else {
                $(event.target.parentNode.parentNode).addClass('red-box');
                c = 0;
            }

            // update currentlySelected
            currentlySelected[1] = {'fileIdx' : fileIdx, 'selectionColour' : c};
        }
    }

    // enable/disable 'Render chart' button based on whether there are any files selected
    if (currentlySelected.length > 0) {
        $( "button" ).button( "option", "disabled", false );
    } else {
        $( "button" ).button( "option", "disabled", true );
    }
}


// displays animated gif while data is being prepared
function showLoader() {

    var pos = $("#tabs").offset();
    var width = $("#tabs").width();
    var height = $("#tabs").height();

    $("#loader").css( { "left": (pos.left + width/2 -56) + "px",
                        "top": (pos.top + height/2) + "px" } );

    $("#loader").show();
}


// displays error message
function displayError() {

    var pos = $("#tabs").offset();
    var width = ($("#tabs").width() - $("#processingError").width())/2;
    var height = ($("#tabs").height() - $("#processingError").height())/2;

    $("#processingError").css( { "left": (pos.left + width) + "px",
                                 "top": (pos.top + height) + "px" } );

    $("#processingError").show();
}


// toggles display of upload note (beneath Import file(s) button)
function toggleUploadNoteDisplay() {
    if ($('#upload-note').css('display') == 'block') {
        $('#upload-note').css('display', 'none')
    } else {
        $('#upload-note').delay(500).fadeIn();
    }
}
