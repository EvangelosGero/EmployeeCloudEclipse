function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 3}, 100);
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