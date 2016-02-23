/**
 * Created by caoyawen on 16/2/16.
 */
$(
    function(){

    }
);

function show_reply_form(form_id, reply_id){
    var form = $("#"+form_id);
    form.attr("attrHidden", false);
    var reply = $("#"+reply_id);
    reply.attr("attrHidden", true);
}

