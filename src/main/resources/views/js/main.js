$(function () {
    alert("command:ready");

    $(document).keydown(function (e) {
        if (e.keyCode == 116 /*F5*/) {
            location.reload();
        }
    });

    $(".root__button1").click(function () {
        alert("event:btn1:click");
        $(".root__hello").append("<span>Hello " + window.appBridge.getUsername() + "!</span><br/>");
    });

    $(".root__button2").click(function () {
        alert("event:btn2:click");
        $(".root__hello").append("<span>Current time: " + window.appBridge.getTime() + "</span><br/>");
    });
});
