
//Login

$("#loginForm").submit(function(e){
    event.preventDefault();
    location.href = "/admin/perform_login"
});

//$("#loginForm").submit(function(e){
//    event.preventDefault();
//    var inputEmail = $("#inputEmail").val()
//    var inputPassword = $("#inputPassword").val()
//    $.ajax({
//      headers: { "X-CSRFToken": getCookie("csrftoken") },
//      type: "POST",
//      url: "/admin/",
//      data: {
//            'inputEmail': inputEmail,
//            'inputPassword' : inputPassword,
//      },
//      dataType: "json",
//      success: function(response){
//
//        if(response.status == '0000'){
//            location.href = "/admin/index/"
//        }
//        else {
//            alert(response.message)
//        }
//      },
//      error: function(request, status, error){
//            alert("통신상 오류가 발생하였습니다.")
//            console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
//      },
//    });
//})




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



