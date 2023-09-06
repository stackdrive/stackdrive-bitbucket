(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var url = AJS.contextPath() + "/rest/stackdrive/1.0/global";

    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
        // request the config information from the server
        $.ajax({
            url: url,
            dataType: "json"
        }).done(function(config) { // when the configuration is returned...
            // ...populate the form.
            $("#front").val(config.front);
            $("#back").val(config.back);
            $("#stackdriveUser").val(config.stackdriveUser);
            $("#log").val(config.log);

            AJS.$("#admin").submit(function(e) {
                e.preventDefault();
                updateConfig();
            });
        });
    });

})(AJS.$ || jQuery);

function updateConfig() {
    AJS.$.ajax({
        url: AJS.contextPath() + "/rest/stackdrive/1.0/global",
        type: "PUT",
        contentType: "application/json",
        data: '{ "front": "' + AJS.$("#front").attr("value") + '", "back": "' +  AJS.$("#back").attr("value") + '", "log": "' +  AJS.$("#log").attr("value") + '","stackdriveUser": "' +  AJS.$("#stackdriveUser").attr("value") + '" }',
        processData: false
    });
}