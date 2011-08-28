
/* jQuery page functionality */

$(document).ready(function() {
    // display tabs
    $('#tabs').tabs({

        // if data has already been loaded and
        // user clicks a new tab, draw the canvas
        select: function(event, ui) {

            if (sketchData.length > 0) {    // i.e., if there is data to be displayed

                var index = ui.index;
                var tab = index+1;
                var tabDescriptionId = "#tab" + tab + "-description";

                if (fileData != 0) {
                    if (index == 0 ||
                        index > 0 && sketchData.length == 2) {

                        if( $(tabDescriptionId).css('display') == 'block' ) {

                            // toggle display
                            //toggleDisplay(index);

                            // work around, as toggleDisplay above does not seem  //
                            // able to hide tabDescriptionId when tab is inactive //
                            var id = "tab" + tab + "-description";                //
                            document.getElementById(id).style.display = 'none';   //
                            var sid = "#sketch" + (index + 1);                    //
                            $(sid).fadeIn();                                      //

                            // get sketch id
                            var sketchId = "sketch" + (index + 1);
                            // redraw canvas
                            drawCanvas(sketchId);
                        }
                    }
                } else {    // fileData is empty (for scenario when user
                            // deletes uploaded files after rendering chart)
                    if( $(tabDescriptionId).css('display') == 'none' ) {

                        // work around (see above)                                //
                        sid = "sketch" + (index + 1);                             //
                        document.getElementById(sid).style.display = 'none';      //
                        id = "#tab" + tab + "-description";                       //
                        $(id).fadeIn();                                           //
                    }
                }
            }
        }
    });

    // 'Render chart' button
    $('button').button(
            {icons:{primary:'ui-icon ui-icon-circle-arrow-e'},
             disabled: true,
             label: 'Render chart'
         });

    $('button').click(function() { renderChart(); return false; });

    // slider
    $('#grouping-slider').slider({
            range: "min",
            value: 70,
            min: 50,
            max: 120,
            slide: function( event, ui ) {
                $('#grouping-number').val( ui.value );
            }
    });

    // sets grouping number above slider in UI
    $('#grouping-number').val( $('#grouping-slider').slider('value') );

    // tooltip
    $('#grouping-box p').cluetip({
        cluetipClass: 'rounded',
        cursor: '',
        dropShadow: false,
        leftOffset: 20,
        positionBy: 'fixed',
        showTitle: false,
        splitTitle: '|',
        topOffset: -20,
        width: 180
    });

    // initialize the jQuery File Upload widget
    $('#fileupload').fileupload();

    // load existing files
    $.getJSON($('#fileupload form').prop('action'), function (files) {
        var fu = $('#fileupload').data('fileupload');
        fu._adjustMaxNumberOfFiles(-files.length);
        fu._renderDownload(files)
            .appendTo($('#fileupload .files'))
            .fadeIn(function () {
                // Fix for IE7 and lower:
                $(this).show();
            });
    });

    // open download dialogs via iframes, to prevent aborting current uploads
    $('#fileupload .files a:not([target^=_blank])').live('click', function (e) {
        e.preventDefault();
        $('<iframe style="display:none;"></iframe>')
            .prop('src', this.href)
            .appendTo('body');
    });

    // callbacks for file upload
    $('#fileupload').bind('fileuploaddone',
          function (e, data) {
              storeFileData(data);  // store data in global fileData array

              if (fileData.length > 0) {
                  $('#render-box').css('display', 'block');
              } else {
                  $('#render-box').css('display', 'none');
              }
    });

    $('#fileupload').bind('fileuploaddestroy',
          function (e, data) {
              removeData(data);

              // if all files are removed:
              // (1) hide 'Render chart' button
              // (2) display tab descriptions
              // (3) display upload note
              // (4) if processing error is displayed, remove it
              if (fileData.length == 0) {

                  if ($('#render-box').css('display') == 'block') {
                      $('#render-box').fadeOut();
                  }

                  var tabIndex = getSelectedTabIndex();
                  var tab = tabIndex+1;
                  var tabDescriptionId = "#tab" + tab + "-description";

                  if ($(tabDescriptionId).css('display') == 'none') {
                      toggleDisplay(tabIndex);
                  }

                  if ($('#upload-note').css('display') == 'none') {
                      toggleUploadNoteDisplay();
                  }

                  if ($('#processingError').css('display') == 'block') {
                      $('#processingError').hide();
                  }
              }
    });

    // callback to hide upload note when 'Open'
    // button is clicked in File Upload dialog
    $('#fileupload').bind('fileuploadadd',
          function (e, data) {
              if ($('#upload-note').css('display') == 'block') {
                  toggleUploadNoteDisplay();
              }
    });
});
