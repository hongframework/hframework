require(['layer','ajax','js/hframework/errormsg'], function () {

    $.checkSubmit = function (_$this) {
            var result =true;
            var isList = false;
            if($(_$this).find(".hflist-data").size() > 0) isList = true;
            $(_$this).find("[not-null=true]").each(function(){
                var $this = $(this);
                if($this.is('select1')) {
                    if(!$this.val())  result = false;;
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
