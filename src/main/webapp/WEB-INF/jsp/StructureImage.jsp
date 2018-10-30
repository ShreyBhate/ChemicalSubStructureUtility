<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="js/promise-1.0.0.min.js"></script>
	<script src="/js/marvinjslauncher.js"></script>	
	<script src="/js/webservices.js"></script>
	<script src="js/jquery-ui.js"></script>
	<script src="js/jquery.min.js"></script>
	
	<style>
	iframe#marvinjs-iframe {
		width: 0;
		height: 0;
		display: initial;
		position: absolute;
		left: -1000;
		top: -1000;
		margin: 0;
		padding: 0;
	}
	</style>
	<script>	
	$(document).ready(function handleDocumentReady (e) {
		// load marvin namespace in a separate frame to avoid css conflict
		// the display attribute of this iframe cannot be "none", but you can hide it somewhere
		$('body').append($('<iframe>', { id: "marvinjs-iframe", src: "/jsp/marvinpack.html"}));
		// wait for the reference of marvin namespace from the iframe
		MarvinJSUtil.getPackage("#marvinjs-iframe").then(function(marvinNameSpace) {
			// the reference to the namespace is arrived but there is no guaranty that its initalization has been finished
			// because of it, wait till the ready state to be sure the whole API is available
			marvinNameSpace.onReady(function() {
				marvin = marvinNameSpace;
				initControl();
			});	
		}, function(error) {
			alert("Cannot retrieve marvin instance from iframe:"+error);
		});
	});
	
	function initControl() {
		$("#molsource-box").val(source);
		$("#createPNG").on("click", function() {
			alert("create PNG");
			createImage("image/png", applyDataUri);
		});
		$("#createJPEG").on("click", function() {
			createImage("image/jpeg", applyDataUri);
		});
		$("#createSVG").on("click", function() {
			createImage("image/svg", applySvg);
		});
	}
	
	function createImage(imageType, callback) {
		var exporter = createExporter(imageType);
		exporter.render($("#molsource-box").val()).then(callback, alert);
	}
	
	
	function createExporter(imageType) {
		var settings = {
			'carbonLabelVisible' : $("#chbx-carbonVis").is(':checked'),
			'cpkColoring' : $("#chbx-coloring").is(':checked'),
			'chiralFlagVisible': $("#chbx-chiral").is(':checked'),
			'lonePairsVisible' : $("#lonepairs").val() != '0',
			'lonepaircalculationenabled' : $("#lonepairs").val() == '2',
			'atomIndicesVisible': $("#chbx-atomIndeces").is(':checked'),
			'implicitHydrogen' : $("#implicittype").val(),
			'displayMode' : $("#displayMode").val(),
			'background-color': $('#bg').val(),
			'zoomMode' : $("#zoommode").val(),
			'width' : parseInt($("#w").val(), 10),
			'height' : parseInt($("#h").val(), 10)
		};

		var inputFormat = $("input[type='radio'][name='inputFormat']:checked").val();
		if(inputFormat == "") {
			inputFormat = null;
		}
		var defaultServices = getDefaultServices();
		var services = {};
		services['molconvertws'] = defaultServices['molconvertws'];
		if($('#chbx-calcStereo').is(":checked")) {
			services['stereoinfows'] = defaultServices['stereoinfows']; // enable stereo calculation
		}
		var params = {
				'imageType': imageType, // type of output image
				'settings': settings, // display settings
				'inputFormat': inputFormat, // renderer will expect molecule source in this format
				'services': services // to resolve any molecule format and be able to calculate stereo info
		}
		return new marvin.ImageExporter(params);
	}
	
	function applyDataUri(dataUri) {
		$('#imageContainer').empty();
		var img = $('<img>', { src: dataUri}).appendTo($("#imageContainer"));
		alert(dataUri)
		$("#imageContainer").css("display", "inline-block");
	}
	
	function applySvg(svg) {
		$("#imageContainer").html(svg);
		$("#imageContainer").css("display", "inline-block");
	}
</script>
</head>
<body>
<div id="imageContainer" class="left10" >
  <img id="image" class="bordered" />
  <textarea id="molsource-box"></textarea>
  <input id="createPNG" type="button" value="Create PNG">
</div>
<div id="imageContainer" class="left10" ></div>
</body>
</html>