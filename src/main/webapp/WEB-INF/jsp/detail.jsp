<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>

    <!--静态包含：将引用的jsp文件整合到当前jsp文件，作为整个servlet输出-->
    <!--动态包含：将引用的jsp文件先转化为servlet，执行后将执行结果，再和当前html文档做合并-->
    <!--以下方式为静态包含-->
    <%@include file="common/head.jsp"%>

</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panle-heading">
                <h1>${seckill.name}</h1>
            </div>
        </div>
        <div class="panel-body"></div>
    </div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>