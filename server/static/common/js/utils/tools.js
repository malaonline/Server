/**
 * Created by caoyawen on 16/2/2.
 */
//检查手机号码的正则表达式
//正确返回true
//错误返回false
function checkMobile(phone_val){
    var pattern=/(^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    return !!pattern.test(phone_val);
}
