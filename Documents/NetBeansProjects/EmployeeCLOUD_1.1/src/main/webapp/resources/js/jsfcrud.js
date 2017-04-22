function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 3}, 100);
    } else {
        PF(dialog).hide();
    }
}

function handleSubmit(args, dialog) {
    var jqDialog = $('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 4}, 1000);
    } else {
        PF(dialog).hide();
    }
 }
 var hiddenFlag = false;
 function hideShow() {
            hiddenFlag = !hiddenFlag;
            var switcherId = $("#switcherId");
            if (hiddenFlag) {
            PF('layoutit').hide('north');
            PF('layoutit').hide('west');
            PF('layoutit').hide('east');
            PF('layoutit').hide('south');
            switcherId.attr("title", "Restore Window");
            }else {
            PF('layoutit').show('north');
            PF('layoutit').show('west');
            PF('layoutit').show('east');
            PF('layoutit').show('south');
            switcherId.attr("title", "Maximize Window");
            }
            }
            function hideTitle(){
            if(!hiddenFlag)PF('layoutit').hide('north');
            }
            function restoreTitle(){
            if(!hiddenFlag)PF('layoutit').show('north');
}

//<![CDATA[
	/**
        * JSF Validator
	*/
	PrimeFaces.validator['custom.emailValidator'] = {

            pattern : /\S+@\S+/,

            validate : function(element, value) {
		//use element.data() to access validation metadata, in this case there is none.
		if (!this.pattern.test(value)) {
			throw {
				summary : 'Validation Error',
				detail : value + ' is not a valid email.'
			}
		}
            }
	};
//]]> */