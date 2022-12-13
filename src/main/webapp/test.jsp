<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="style.css" rel="stylesheet" type="text/css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<title>Pubsub System</title>
</head>
<body>
<h1>Server</h1>
<form action="">  
Server Name:<input type="text" id="servername" name="servername"/><br/> <br/> 
<input type="submit" class="btn-submit" id="submitserver" value="submit" onclick="createbroker(); return false;" /> 
<input type="submit" class="btn-delete" id="deleteserver" value="delete" onclick="deletebroker(); return false;" /> 
</form>
<br />
<form action="serverhandler">
<input type="submit" class="btn-serverinfo" id="serverinfo" value="Server Info" />
</form>

<br /><br /><br />
<h1>Subscriber</h1>
<h3>Create Subscriber</h3>
<form>  
Subscriber Name:<input type="text" id="subscriber" name="subscriber"/><br/> <br/> 
<input type="submit" class="btn-submit" value="submit" onclick="createsub(); return false;"/>  
<br /><br /><br />
</form>
<h3>Subscribe/Unsubscribe</h3>
<form>  
Subscriber Name:<input type="text" id="subname" name="subname"/><br/><br />
Transport Mode/ Route:<input type="text" id="topic" name="topic"/><br/> <br/>
<input type="submit" class="btn-subscribe" id="subbutton" value="subscribe" onclick="subscribe(); return false;" />
<input type="submit" class="btn-unsubscribe" id="unsubbutton" value="unsubscribe" onclick="unsubscribe(); return false;" />  
<br /><br />
</form>

<br /><br /><br />
<h1>Publisher</h1>
<form action="">  
Transport Mode/ Route:<input type="text" id="publisher" name="publisher"/><br/><br />
Content:<textarea id="content" name="content" rows="4" cols="50"></textarea><br /><br /> 
<input type="submit" class="btn-submit" value="Publish" onclick="createcontent(); return false;" />  
</form>
<script>
function createbroker(){
	details = {name: document.getElementById('servername').value, action: document.getElementById('submitserver').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "serverhandler",
	      data: details,
	   	  success: function(result){	   		 
	   		  r = result;
	   	  }
	  });	  
	 alert(r);
}

function deletebroker(){
	details = {name: document.getElementById('servername').value, action: document.getElementById('deleteserver').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "serverhandler",
	      data: details,
	   	  success: function(result){	   		 
	   		  r = result;
	   	  }
	  });	  
	 alert(r);
}

function createsub(){
	details = {name: document.getElementById('subscriber').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "subservlet",
	      data: details,
	   	  success: function(result){	   		 
	   		  s = result;
	   	  }
	  });	  
	 alert(s);
}

function createcontent(){
	details = {name: document.getElementById('publisher').value, content: document.getElementById('content').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "pubservlet",
	      data: details,
	      success: function(result){
	   		  t = result;	   		
	   	  }
	  });	  
	 alert(t);
}

function subscribe(){
	details = {sub: document.getElementById('subname').value, topic: document.getElementById('topic').value, button: document.getElementById('subbutton').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "subunsubservlet",
	      data: details,
	      success: function(result){
	   		  u = result;	   		
	   	  }
	  });	  
	 alert(u);
}

function unsubscribe(){
	details = {sub: document.getElementById('subname').value, topic: document.getElementById('topic').value, button: document.getElementById('unsubbutton').value};
	$.ajax({
		  'async': false,
		  type: "POST",
	      url: "subunsubservlet",
	      data: details,
	      success: function(result){
	   		  v = result;	   		
	   	  }
	  });	  
	 alert(v);
}

</script>
</body>
</html>