/**
 * Created by Sean on 2015/12/9.
 */

define(function (require) {
    /**
     * 检测输入内容是否符合类型
     * @param type 类型
     * @param value 输入的内容
     * @returns {boolean} 检测的结果
     * @constructor
     */
    function CheckValue(type, value, length) {

        var _value = value;

        switch (type) {
            case 'mobile':
                return /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(_value);
            case 'idcard':
                return /(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(_value);
                break;
            case 'email':
                return /^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\.][a-z]{2,3}([\.][a-z]{2})?$/i.test(_value);
                break
            case "maxlength":
                return _value.length < length;
            case "minlenght":
                return _value.length > length;
            default :
                return true;
                break;
        }
    }

    /**
     * 检测输入的内容
     * @param selector
     * @returns {}
     * @constructor
     */
    function CheckInput (selector){

        var $el = $(selector);
        var _val = $el.val(),
            _type = $el.attr('data-validate');

        return CheckValue(_type, _val);
    }
    return CheckInput;
});