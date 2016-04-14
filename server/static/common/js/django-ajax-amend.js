/**
 * Created by liumengjun on 1/12/16.
 * @see https://docs.djangoproject.com/en/dev/ref/csrf/#ajax
 */
// using jQuery
function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie != '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = $.trim(cookies[i]);
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) == (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    } else {
        throw "can't get cookies";
    }
    if (cookieValue == null) {
        cookieValue = $.cookie(name);
    }

    return cookieValue;
}
function csrfSafeMethod(method) {
    // these HTTP methods do not require CSRF protection
    return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
}
var _beforeSend0 = function(xhr, options) {
    if (!csrfSafeMethod(options.type) && !this.crossDomain) {
        var csrftoken = getCookie('csrftoken');
        if (csrftoken == null) {
            throw "can't get csrftoken from cookies";
        }
        if (xhr.setRequestHeader) {
            xhr.setRequestHeader("X-CSRFToken", csrftoken);
        } else {
            options.data["X-CSRFToken"] = csrftoken;
        }
    }
};
if ($.ajaxSetup) { // jQuery
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            _beforeSend0(xhr, settings);
        }
    });
} else { // for zepto.js
    $(document).on(
        'ajaxBeforeSend', function(e, xhr, options) {
            _beforeSend0(xhr, options);
        }
    );
}