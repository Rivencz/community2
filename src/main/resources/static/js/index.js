$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    var title = $("#recipient-name").val();
    var content = $("#message-text").val();

    $.post(
        //访问的请求路径
        CONTEXT_PATH + "/discuss/add",
        //传到controller中的参数
        {"title":title,"content":content},
        function (data) {
            data = $.parseJSON(data);
            $("#hintBody").text(data.msg);
            $("#hintModal").modal("show");
            //定时器,2s之后隐藏并刷新界面
            setTimeout(function () {
                //隐藏提示框
                $("#hintModal").modal("hide");
                //刷新整个界面
                window.location.reload();
            }, 2000);
        }
    )
}