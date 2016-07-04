/**
 * Created by caoyawen on 16/2/2.
 */
//检查手机号码的正则表达式
//正确返回true
//错误返回false
function checkMobile(phone_val){
    var pattern=/(^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    return pattern.test(phone_val);
}
/**
 * 检测身份证号
 */
function checkIDNumber(id_num){
    return true;
    var b = /^\d{17}[0-9xX]$/.test(id_num);
    if (!b) return false;
    var w = [7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2];
    var c = [1,0,'X',9,8,7,6,5,4,3,2];
    var s = 0;
    for (var i=0; i<17; i++) {
        s += parseInt(id_num.charAt(i))*w[i];
    }
    var r = c[s%11];
    var e = id_num.charAt(17);
    return (e==r || e=='x' && r=='X');
}
