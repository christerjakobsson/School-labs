"use strict";

var globalColor = "#00ffff";
var globalRadius = 20;

function getRadius() {
	var radius = document.getElementById("radiusInput").value;
	var isOk = isNaN(radius);
	
	if(isOk === false  && radius > 0) {
		globalRadius = radius;
	}
	else {
		globalRadius = 20;
	}
}

function getColor() {
	var color = document.getElementById("colorInput").value;
	var isOk  = /^#[0-9A-F]{6}$/i.test(color);
	if(isOk === true) {
		globalColor = color;
	}
	else {
		globalColor = "#000000";
	}
}

function getCursorPosition(e) {
    var x;
    var y;
    if (e.pageX != undefined && e.pageY != undefined) {
	x = e.pageX;
	y = e.pageY;
    }
    else {
	x = e.clientX + document.body.scrollLeft +
            document.documentElement.scrollLeft;
	y = e.clientY + document.body.scrollTop +
            document.documentElement.scrollTop;
    }
	x -= myCanvas.offsetLeft;
	y -= myCanvas.offsetTop;
	
	canvasClick(x, y);	
}

function canvasClick(x, y) {
	var drawingcanvas = document.getElementById('myCanvas');
	var drawCont = drawingcanvas.getContext('2d');

	drawCont.beginPath();
	drawCont.fillStyle= globalColor;
    drawCont.arc(x, y, globalRadius ,0,Math.PI*2,true);
    drawCont.fill();
	drawCont.closePath();
	
	window.localStorage['canvas'] = drawingcanvas.toDataURL();	
}


window.onload = function() {
	var drawCont = document.getElementById('myCanvas').getContext('2d');	
	var img = new Image();
	
	if(window.localStorage !== 'undefined'){
    	img.src = window.localStorage["canvas"];
	
		img.onload = function(){
			drawCont.drawImage(img, 0, 0);
		}
	}
	else {
		console.log('Your browser is outdated!');
	}

	drawCont.fillStyle = "#FFBBBA";
	drawCont.font = "bold 10px Arial";
	drawCont.fillText("Watermark", 1, 10);
	
	document.getElementById("clear").onclick = function() {
		drawCont.clearRect(0, 0, myCanvas.width, myCanvas.height);
		drawCont.restore();
		drawCont.fillStyle = "#FFBBBA";
		drawCont.font = "bold 10px Arial";
		drawCont.fillText("Watermark", 1, 10);
		 window.localStorage.clear();
	}
}
	
function vidPlay(vid) {
	var audioID = document.getElementById("audioID");
	var audioPlay = audioID.currentSrc;
			
	var videoID = document.getElementById("videoID");	
	var videoPlay = videoID.currentSrc;
	var videoExt = videoPlay.substr(videoPlay.lastIndexOf("."));
	videoID.src = "http://www8.cs.umu.se/kursmaterial/html5/mediafiler/Waterballon"+vid+videoExt;
	
	videoID.load();
	videoID.play();
}


function playAll() {
	audioID.play();
	videoID.play();
	
}

function stopAll() {
	videoID.pause();
	audioID.pause();
}
