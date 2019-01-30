require(['layer','ajax','js/hframework/errormsg'], function () {


    $.checkListIsEmpty = function (_$this) {
        if($(_$this).find(".hflist-data").size() > 0) {
            if($(_$this).find(".hflist-data tr").length <= 1){
                var isEmpty = true;
                $(_$this).find(".hflist-data tr:first td :visible:not(a,i)").each(function(){
                    if($(this).val()){
                        isEmpty = false;
                        return false;
                    }
                })
                return isEmpty;
            }
        }
        return false;
    }

    $.checkSubmit = function (_$this) {
            var result =true;
            var isList = false;
            if($(_$this).find(".hflist-data").size() > 0) isList = true;
            if($.checkListIsEmpty(_$this)) return true;

            $(_$this).find("[not-null=true]").each(function(){
                var $this = $(this);
                if($this.is('select1')) {
                    if(!$this.val())  result = false;
                }else {
                    if(!$this.val()) {
                        if( $this.parent().find(".check-result-tip").size() == 0) {
                            $this.addClass("check-failed");
                            if(isList) {
                                if(!$this.attr("orig-border")) {
                                    $this.attr("orig-border", $this.css("border"));
                                }
                                $this.css("border","1px solid #FF7F50");
                            }else {
                                $this.parent().append($("<span style='margin-left: 5px;color: red;font-size: small' class='check-result-tip'>不能为空！</span>"));
                            }


                            $($this).live("focus",function(){
                                $this.removeClass("check-failed");
                                if(isList) {
                                    $this.css("border",$this.attr("orig-border"));
                                }else {
                                    $this.parent().find(".check-result-tip").remove();
                                }

                            });

                        }
                        result = false;
                    }else {
                        $this.removeClass("check-failed");
                        if(isList) {
                            $this.css("border",$this.attr("orig-border"));
                        }else {
                            $this.parent().find(".check-result-tip").remove();
                        }
                    }
                }
            });
            return result;
        }
});
