
$(document).ready(function (e) {

    var search_date_filter = getParameterByName('search_date_filter');
    if(search_date_filter != "")
    {
        $('#search_date_filter').val(search_date_filter)
    }

    $('#back-button').click(function(){
        window.history.back();
    });
});

function getCookie(c_name)
{
    if (document.cookie.length > 0)
    {
        c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1)
        {
            c_start = c_start + c_name.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) c_end = document.cookie.length;
            return unescape(document.cookie.substring(c_start,c_end));
        }
    }
    return "";
}

$(function() {

  $('input[name="search_date_filter"]').daterangepicker({
        autoUpdateInput: false,
        locale: {
          format: 'YYYY-MM-DD',
          cancelLabel: '초기화',
          applyLabel: '적용'
        }
  });

  $('input[name="search_date_filter"]').on('apply.daterangepicker', function(ev, picker) {
      $(this).val(picker.startDate.format('YYYY-MM-DD') + ' ~ ' + picker.endDate.format('YYYY-MM-DD'));
      $('#search-form').submit();
  });

  $('input[name="search_date_filter"]').on('cancel.daterangepicker', function(ev, picker) {
      $(this).val('');
  });

});


//$('#search_cs_grade').change(function(){
//       $('#search-form').submit();
//});


function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}




