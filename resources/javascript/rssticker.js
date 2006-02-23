// -------------------------------------------------------------------
// Based on Advanced RSS Ticker (Ajax invocation)
// by Dynamic Drive (http://www.dynamicdrive.com)
// IdegaWeb changes and addons by Eirikur S. Hrafnsson, eiki@idega.is
// -------------------------------------------------------------------

////////////No need to edit beyond here//////////////

function createAjaxObj(){
var httprequest=false
if (window.XMLHttpRequest){ // if Mozilla, Safari etc
	httprequest=new XMLHttpRequest()
	if (httprequest.overrideMimeType)
		httprequest.overrideMimeType('text/xml')
	}
	else if (window.ActiveXObject){ // if IE
		try {
		httprequest=new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch (e){
		try{
		httprequest=new ActiveXObject("Microsoft.XMLHTTP");
		}
		catch (e){}
		}
	}
	return httprequest
}

// -------------------------------------------------------------------
// Main RSS Ticker Object function
// rssticker_ajax(RSS_URL, cachetime, divId, divClass, delay, optionallogicswitch)
// -------------------------------------------------------------------

function rssticker_ajax(RSS_URL, cachetime, divId, divClass, delay, logicswitch){
	this.RSS_URL = RSS_URL //Array key indicating which RSS feed to display
	this.cachetime=cachetime //Time to cache feed, in minutes. 0=no cache.
	this.tickerid=divId //ID of ticker div to display information
	this.delay=delay //Delay between msg change, in miliseconds.
	this.logicswitch=(typeof logicswitch!="undefined")? logicswitch : ""
	this.mouseoverBol=0 //Boolean to indicate whether mouse is currently over ticker (and pause it if it is)
	this.pointer=0
	this.opacitysetting=0.2 //Opacity value when reset. Internal use.
	this.title=[], this.link=[], this.description=[], this.pubdate=[] //Arrays to hold each component of an RSS item
	this.ajaxobj=createAjaxObj()
	document.write('<div id="'+divId+'" class="'+divClass+'" >Initializing ticker...</div>')
	if (window.getComputedStyle) //detect if moz-opacity is defined in external CSS for specified class
	this.mozopacityisdefined=(window.getComputedStyle(document.getElementById(this.tickerid), "").getPropertyValue("-moz-opacity")==1)? 0 : 1
	this.getAjaxcontent()
}

// -------------------------------------------------------------------
// getAjaxcontent()- Makes asynchronous GET request to RSS_URL
// -------------------------------------------------------------------

rssticker_ajax.prototype.getAjaxcontent=function(){
	if (this.ajaxobj){
	var instanceOfTicker=this;
	//var parameters="id="+encodeURIComponent(this.RSS_id)+"&cachetime="+this.cachetime+"&bustcache="+new Date().getTime()
	var parameters="";
	
	this.ajaxobj.onreadystatechange=function(){instanceOfTicker.initialize()}
	
	this.fullURL = "http://"+window.location.hostname + ":"+window.location.port + this.RSS_URL
	//alert(this.fullURL);
	this.ajaxobj.open('GET',this.fullURL +"?"+parameters, true);
	this.ajaxobj.send(null);
	}
}

// -------------------------------------------------------------------
// initialize()- Initialize ticker method.
// -Gets contents of RSS content and parse it using JavaScript DOM methods 
// IMPORTANT!!--> For this to work the content type of the rss file must be "text/xml" !
// After days of debugging I found out that parsing a document with some other content type results in jibberish or no data! (eiki@idega.is)
// -------------------------------------------------------------------

	rssticker_ajax.prototype.initialize=function(){ 
	if (this.ajaxobj.readyState == 4){ //if request of file completed
		if (this.ajaxobj.status==200){ //if request was successful
			var xmldata=this.ajaxobj.responseXML;
			
			var isRSSTWO = false;
			
			if(xmldata.getElementsByTagName("item").length==0){ //if no <item> elements found in returned content
				if(xmldata.getElementsByTagName("entry").length==0){ //if no <item> elements found in returned content
					document.getElementById(this.tickerid).innerHTML="<b>Error</b> fetching remote RSS feed!<br />"+this.ajaxobj.responseText;
					return;
				}
			}
			else{
				isRSSTWO = true;
			}
			
			var instanceOfTicker=this;
			
			if(isRSSTWO){//for rss 2.0 feeds
				this.feeditems=xmldata.getElementsByTagName("item");
			}
			else{//for atom 1.0 feeds
				this.feeditems=xmldata.getElementsByTagName("entry");
			}
			
			//alert('isRSSTWO:'+isRSSTWO+' number of items: '+ this.feeditems.length);
			
			//Cycle through RSS XML object and store each peice of an item inside a corresponding array
			for (var i=0; i<this.feeditems.length; i++){
				if(isRSSTWO){
					this.title[i]=this.feeditems[i].getElementsByTagName("title")[0].firstChild.nodeValue;
					this.link[i]=this.feeditems[i].getElementsByTagName("link")[0].firstChild.nodeValue;		
					this.description[i]=this.feeditems[i].getElementsByTagName("description")[0].firstChild.nodeValue;
					this.pubdate[i]=this.feeditems[i].getElementsByTagName("pubDate")[0].firstChild.nodeValue;
				}
				else{
					this.title[i]=this.feeditems[i].getElementsByTagName("title")[0].firstChild.nodeValue;
					this.link[i]=this.feeditems[i].getElementsByTagName("link")[0].getAttribute("href");	
					//try{	
						//may not exist
					//	this.description[i]=this.feeditems[i].getElementsByTagName("summary")[0].firstChild.nodeValue;
					//}
					//catch(e){
					//}
					this.pubdate[i]=this.feeditems[i].getElementsByTagName("published")[0].firstChild.nodeValue;
					//alert(this.title[i] + " "+this.link[i]+" "+this.description[i]+" "+this.pubdate[i]);
				}
			}
			
			document.getElementById(this.tickerid).onmouseover=function(){instanceOfTicker.mouseoverBol=1}
			document.getElementById(this.tickerid).onmouseout=function(){instanceOfTicker.mouseoverBol=0}
			this.rotatemsg()
		}
	}
}


// -------------------------------------------------------------------
// rotatemsg()- Rotate through RSS messages and displays them
// -------------------------------------------------------------------

	rssticker_ajax.prototype.rotatemsg=function(){
	var instanceOfTicker=this
	if (this.mouseoverBol==1) //if mouse is currently over ticker, do nothing (pause it)
		setTimeout(function(){instanceOfTicker.rotatemsg()}, 100)
	else{ //else, construct item, show and rotate it!
		var tickerDiv=document.getElementById(this.tickerid)
		var linktitle='<div class="rsstitle"><a href="'+this.link[this.pointer]+'">'+this.title[this.pointer]+'</a></div>'
		var description='<div class="rssdescription">'+this.description[this.pointer]+'</div>'
		var feeddate='<div class="rssdate">'+this.pubdate[this.pointer]+'</div>'
		if (this.logicswitch.indexOf("description")==-1) description=""
		if (this.logicswitch.indexOf("date")==-1) feeddate=""
		var tickercontent=linktitle+feeddate+description //STRING FOR FEED CONTENTS 
		this.fadetransition("reset") //FADE EFFECT- RESET OPACITY
		tickerDiv.innerHTML=tickercontent
		this.fadetimer1=setInterval(function(){instanceOfTicker.fadetransition('up', 'fadetimer1')}, 100) //FADE EFFECT- PLAY IT
		this.pointer=(this.pointer<this.feeditems.length-1)? this.pointer+1 : 0
		setTimeout(function(){instanceOfTicker.rotatemsg()}, this.delay) //update container every second
	}
}

// -------------------------------------------------------------------
// fadetransition()- cross browser fade method for IE5.5+ and Mozilla/Firefox
// -------------------------------------------------------------------

rssticker_ajax.prototype.fadetransition=function(fadetype, timerid){
	var tickerDiv=document.getElementById(this.tickerid)
	if (fadetype=="reset")
		this.opacitysetting=0.2
	if (tickerDiv.filters && tickerDiv.filters[0]){
		if (typeof tickerDiv.filters[0].opacity=="number") //IE6+
			tickerDiv.filters[0].opacity=this.opacitysetting*100
		else //IE 5.5
			tickerDiv.style.filter="alpha(opacity="+this.opacitysetting*100+")"
		}
	else if (typeof tickerDiv.style.MozOpacity!="undefined" && this.mozopacityisdefined){
		tickerDiv.style.MozOpacity=this.opacitysetting
	}
	if (fadetype=="up")
	this.opacitysetting+=0.2
	if (fadetype=="up" && this.opacitysetting>=1)
	clearInterval(this[timerid])
}


