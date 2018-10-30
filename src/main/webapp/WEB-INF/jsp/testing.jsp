!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title></title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
    $('[data-toggle="popover"]').popover({
        placement : 'top',
        trigger : 'hover',
        html : true,
        content : '<div class="media"><a href="#" class="pull-left"><img src="image2.png" class="media-object" alt="Sample Image"></a><div class="media-body"></div></div>'
    });
});
</script>
<style type="text/css">
    .bs-example{
        margin: 200px 150px 0;
    }
    .bs-example button{
        margin: 10px;
</style>
</head>
<body>
<div class="bs-example">
    <button type="button" class="btn btn-primary" data-toggle="popover">Popover with image</button>
</div>
</body>
</html>