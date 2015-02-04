$(function () {
    alert("command:ready");

    $(document).keydown(function (e) {
        if (e.keyCode == 116 /*F5*/) {
            location.reload();
        }
    });

    $(".root__button1").click(function () {
        alert("event:btn1:click");
        $(".root__hello").append("<br/><span>Hello " + window.appBridge.getUsername() + "!</span>");
    });

    $(".root__button2").click(function () {
        alert("event:btn2:click");
        $(".root__hello").append("<br/><span>Current time: " + window.appBridge.getTime() + "</span>");
    });

    $(".root__button3").click(function () {
        var n = 40;
        $(".root__hello").append("<br/>");
        var timer = setInterval(function () {
            $(".root__hello").append("<span>" + String.fromCharCode(n++) + "</span>");
            if (n > 150) {
                clearInterval(timer);
            }
        }, 40);
    });
});
