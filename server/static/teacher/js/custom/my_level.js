/**
 * Created by caoyawen on 16/3/25.
 */
$(
    function(){
        $("#expand_record").click(function(){
            console.log("click");
            var expand_button = $("#expand_record");
            var rest_part_record = $('#rest_part_record');
            // console.log("expand_record is "+window.expand_record_tag);
            if (window.expand_record_tag == undefined || window.expand_record_tag == false){
            //    展开
            //     console.log("展开");
                window.expand_record_tag = true;
                expand_button.html("-");
                rest_part_record.attr('attrHidden', false);
            }else{
            //    收起
            //     console.log("收起");
                window.expand_record_tag = false;
                expand_button.html("+");
                rest_part_record.attr('attrHidden', true);
            }
        });
    }
);
