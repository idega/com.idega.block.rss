var isAmino = (navigator.userAgent.indexOf('Amino')>0);

/* CLOCK */
var clockID = 0;
var weekday=new Array(7);
weekday[0]="Sun";
weekday[1]="Mán";
weekday[2]="Þri";
weekday[3]="Mið";
weekday[4]="Fim";
weekday[5]="Fös";
weekday[6]="Lau";

var month=new Array(12);
month[0]="jan";
month[1]="feb";
month[2]="mars";
month[3]="apríl";
month[4]="maí";
month[5]="júní";
month[6]="júlí";
month[7]="ágúst";
month[8]="sept";
month[9]="ókt";
month[10]="nóv";
month[11]="des";

function UpdateClock() {
	if(clockID) {
		clearTimeout(clockID);
		clockID  = 0;
	}
	var tDate = new Date();
	var second = tDate.getSeconds();
	if(second<10){
		second = "0"+second;
	}
	// findObj('clock').innerHTML= "" +  weekday[tDate.getDay()] +"  "+tDate.getDate()+"."+ month[tDate.getMonth()]+" - "+ tDate.getHours() + ":" + tDate.getMinutes() + ":" + second;
	findObj('clock').innerHTML= "" +  weekday[tDate.getDay()] +"  "+tDate.getDate()+"."+ month[tDate.getMonth()]+" - "+ tDate.getHours() + ":" + tDate.getMinutes();  
	clockID = setTimeout("UpdateClock()", 1000);
}

function StartClock() {
	clockID = setTimeout("UpdateClock()", 500);
}

function KillClock() {
	if(clockID) {
		clearTimeout(clockID);
		clockID  = 0;
	}
}
/*Clock ends*/

/* Navigation rules , uses the Behaviour library*/
/** START OF NAVIGATION BINDING CODE , eiki@idega.is **/
var started = false;

/* RSS viewers navigation */
var rssViewersArray = new Array();
var currentViewerNumber = 1;

/* Image ticker navigation */
var imageGalleriesArray = new Array();
var imageGalleriesSourceArray = new Array();
var currentSelectedGallery = -1;
var imageGallery;
var lastGallery;
var lastHighlighted;

//for faster teardown
window.onbeforeunload = function(){
	//alert('working');
	hideIWIPTVFrame();
	hideSlideShow();
	
	slides = null;
	KillClock();
	if(isAmino){
		AVMedia.Kill();
		VideoDisplay.SetPIG(false);
		VideoDisplay.FullScreen(false);
	}
}

function startStuff(){

	if(!started){
		this.focus();
		StartClock();
		
		Behaviour.register({ 
	        'div.rssItem' : function(e){ 
	        
	        		var rssViewer = e.parentNode;
	       		       		
	       		if(!rssViewer.inited){
		       		rssViewer.maxNumberOfItems = 0;
		       		rssViewer.currentOffset = 0;
		       		rssViewer.maxOffset = 0;
		       		rssViewer.currentItemInRSSItemsArray = -1;
		       		rssViewer.moveByXPixels = 42;
		       		rssViewer.inited = true;
		       		rssViewer.rssItemsArray = new Array();
	       		}
	       		
	        		//add to the arrary and update the objects info
	        		rssViewer.rssItemsArray.push(e);
	        		rssViewer.maxNumberOfItems++;
	        		rssViewer.maxOffset = rssViewer.maxNumberOfItems * rssViewer.moveByXPixels;
	        		
	        		//don't know if this needs to be here
	        		e.onclick = function(){ 
	               // e.className = 'rssItemHover';
	            }
	        },
	        'div.rss' : function(e){ 
	        		rssViewersArray.push(e);
	        		
	        		//e.onclick = function(){ 
	               // e.className = 'rssItemHover';
	            //}
	        },
	        'div.IMAGE_TICKER .galleryImage img' : function(e){ 
	        		imageGalleriesSourceArray.push(e.getAttribute('orgIMGPath'));
	        		imageGalleriesArray.push(e.parentNode.parentNode);
	        		//alert('found one '+new Date());
	        },
	        'div.multiImageGallery' : function(e){ 
	        		imageGallery = e;
	        }  
	    }); 
	
	    Behaviour.apply(); 
	    
	    if(isAmino){
			try{
			//	AVMedia.Play('src=mp3://10.17.254.89:8000/listen');
				//AVMedia.Play('src=mp3://157.157.136.145:8000/listen');
			}
			catch(e) {
		    	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		    	alert("Failed to start video:\n" + msg);
		        return;
		    }
		}
		
		
		//start the slideshow
		var slides = new Slidalicious('SHOWCASE',{transition_effect: Effect.Appear, display_time: 7, images: imageGalleriesSourceArray, links:  imageGalleriesSourceArray});
		slides.play();
		
		started = true;
	   }
}



function showSlideShow(){
	$('SHOWCASE').style.visibility='visible';
}

function hideSlideShow(){
	$('SHOWCASE').style.visibility='hidden';
}

function hideIWIPTVFrame(){
	$('IPTV_FRAME').style.visibility = 'hidden';
}

function showIWIPTVFrame(){
	$('IPTV_FRAME').style.visibility = 'visible';
}
	
						
						
/*
Highlights the current RSSViewer and selects and scrolls to the next RSSItem
*/
function selectNextRSSItem(){
	//Get the current viewer
	currentViewer = getCurrentViewer();

	//reset last selection
	if(currentViewer.currentItemInRSSItemsArray >= 0){
		rssItem = currentViewer.rssItemsArray[currentViewer.currentItemInRSSItemsArray];
		rssItem.className = 'rssItem';
	}
	
	currentViewer.currentItemInRSSItemsArray = Math.max(0, Math.min(++currentViewer.currentItemInRSSItemsArray,currentViewer.maxNumberOfItems-1));
	currentViewer.currentOffset = (currentViewer.currentItemInRSSItemsArray)*currentViewer.moveByXPixels;
	
	//alert('currentpos: '+currentViewer.currentItemInRSSItemsArray + ' currentoff:'+currentViewer.currentOffset);
	//increment the counter and hightlight the next selection
	rssItem = currentViewer.rssItemsArray[currentViewer.currentItemInRSSItemsArray];
	rssItem.className = rssItem.className + ' rssItemHover';

	//slide the layer up
	if( (currentViewer.currentOffset+currentViewer.moveByXPixels) < currentViewer.maxOffset ){
		if(currentViewer.currentItemInRSSItemsArray!=0){
			new Effect.MoveBy(currentViewer , -currentViewer.moveByXPixels, 0,  {queue: 'end', mode: 'relative', duration: 1.0 });
		}
		currentViewer.currentOffset +=currentViewer.moveByXPixels;
	}
}


/*
Highlights the current RSSViewer and selects and scrolls to the previous RSSItem
*/
function selectPreviousRSSItem(){
//Get the current viewer
	currentViewer = getCurrentViewer();
	//reset last selection
	if(currentViewer.currentItemInRSSItemsArray >= 0){
		rssItem = currentViewer.rssItemsArray[currentViewer.currentItemInRSSItemsArray];
		rssItem.className = 'rssItem';
	}
	//decrease the counter and hightlight the next selection
	currentViewer.currentItemInRSSItemsArray = Math.max(0,--currentViewer.currentItemInRSSItemsArray);
	if(currentViewer.currentItemInRSSItemsArray==0){
		if(currentViewer.currentOffset==currentViewer.moveByXPixels){
			currentViewer.currentOffset = 0;
		}
		else{
			currentViewer.currentOffset = (currentViewer.currentItemInRSSItemsArray+1)*currentViewer.moveByXPixels ;
		}
	}
	else{
		currentViewer.currentOffset = (currentViewer.currentItemInRSSItemsArray)*currentViewer.moveByXPixels ;
	}
	
	
	rssItem = currentViewer.rssItemsArray[currentViewer.currentItemInRSSItemsArray];
	rssItem.className = rssItem.className + ' rssItemHover';
	
	rssViewer = rssItem.parentNode;
	
	//alert('currentpos: '+currentViewer.currentItemInRSSItemsArray + ' currentoff:'+currentViewer.currentOffset);
	
	//slide the layer down
	if( (currentViewer.currentOffset-currentViewer.moveByXPixels) > 0){
		new Effect.MoveBy(rssViewer, currentViewer.moveByXPixels, 0,  {queue: 'end', mode: 'relative', duration: 1.0 });
 		
 		currentViewer.currentOffset -=currentViewer.moveByXPixels;
 	}
/*
  new Effect.MoveBy(element, y, x, [options]);
  Effect.DefaultOptions = {
  transition: Effect.Transitions.sinoidal,
  duration:   1.0,   // seconds
  fps:        25.0,  // max. 25fps due to Effect.Queue implementation
  sync:       false, // true for combining
  from:       0.0,
  to:         1.0,
  delay:      0.0,
  queue:      'parallel'
*/

}


/*
Highlights the Image gallery ticker and selects and scrolls to the next imageGallery
*/
function selectNextImageGallery(){
	highlight(imageGallery.parentNode);
	move = false;
				
	if(currentSelectedGallery==-1){
		currentSelectedGallery = Math.round(imageGalleriesArray.length / 2) - 1; 
	}
	else{
		move = true;
	}
		
	if( (currentSelectedGallery+1) < imageGalleriesArray.length){
		currentSelectedGallery++;
	}
	else{
		move = false;
	}
	
	highlightImageGallery(imageGalleriesArray[currentSelectedGallery]);
		
	if(move){
		new Effect.MoveBy(imageGallery , 0, -45,  {queue: 'end', mode: 'relative', duration: 0.3 });
	}
}

/*
Highlights the Image gallery ticker and selects and scrolls to the previous imageGallery
*/
function selectPreviousImageGallery(){
	highlight(imageGallery.parentNode);
	move = false;
	
	if(currentSelectedGallery==-1){
		currentSelectedGallery = Math.round(imageGalleriesArray.length / 2) - 1; 
	}
	else{
		move = true;
	}
	
	if( (currentSelectedGallery-1) >= 0){
		currentSelectedGallery--;
	}
	else{
		move = false;
	}
	
	highlightImageGallery(imageGalleriesArray[currentSelectedGallery]);
	
	if(move){
		new Effect.MoveBy(imageGallery, 0, 45,  {queue: 'end', mode: 'relative', duration: 0.3 });
	}
}

function highlight(objectToHighlight){
	if(!objectToHighlight.previousClassName){
	    removeHighlight(lastHighlighted);
		objectToHighlight.previousClassName = objectToHighlight.className;
		objectToHighlight.className = objectToHighlight.className + ' highlighted';
		lastHighlighted = objectToHighlight; 
	}
	
}

function highlightImageGallery(gallery){
	if(!gallery.previousClassName){
	    removeHighlight(lastGallery);
		gallery.previousClassName = gallery.className;
		gallery.className = gallery.className + ' highlighted';
		lastGallery = gallery; 
	}
}



function removeHighlight(highLightedObject){
	if(highLightedObject){
		 if(highLightedObject.previousClassName){
		 	highLightedObject.className = highLightedObject.previousClassName;
		 	highLightedObject.previousClassName = null;
		 }
	 }
}

function getCurrentViewer(){
	currentViewer = rssViewersArray[currentViewerNumber-1];
	parentLayer = currentViewer.parentNode;
	highlight(parentLayer);
	return currentViewer;
}


function setCurrentViewer(viewerNumber){
	//reset older
	currentViewer = rssViewersArray[currentViewerNumber-1];
	parentLayer = currentViewer.parentNode;
	if(parentLayer.previousClassName){
		parentLayer.className = parentLayer.previousClassName;
		parentLayer.previousClassName = null;
	}
	currentViewerNumber = viewerNumber;
}

/** END OF NAVIGATION BINDING CODE **/

// IdegaWeb IPTV support
function sendMessage(message){
	//var receiver = top.document;
	//receiver.postMessage(message);
}


//must call this method from the tandberg javascripts
stb_eventBoot();

// Main Key
function key_event(keycode){
	switch(keycode){
		case KEY_INFO:
			alert("info key!");
			break;
		case KEY_TV:    
			alert("tv key!");
			break;
		case KEY_BLUE:   
			sendMessage('BLUE');
			break;
		case KEY_MENU:
			sendMessage('MENU');
			break;
		case KEY_YELLOW: 
			//gotoUrl("http://egotv.sidan.is:8090/cms/");      
			//sendMessage('YELLOW');
			break;
		case KEY_RED:    
			sendMessage('RED');
			break;
		case KEY_GREEN:
			sendMessage('GREEN');
			break;
		case KEY_OK:
			//Get the current viewer
			currentViewer = getCurrentViewer();
			try{
				if(currentViewer){
					rssItem = currentViewer.rssItemsArray[currentViewer.currentItemInRSSItemsArray];
					theRealId = rssItem.getAttribute('name');
					toggleRSSContentLayer(theRealId);
				}
			}
			catch(e) {
		            	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		            	alert("Failed message was:\n" + msg);
		                return;
		            }
			
			break;
		case KEY_UP:
			currentViewer = getCurrentViewer();
			hideRSSContentLayer();
			selectPreviousRSSItem();
		break;
		case KEY_DOWN:
			currentViewer = getCurrentViewer();
			hideRSSContentLayer();
			selectNextRSSItem();
		break;
		case KEY_LEFT:
			selectPreviousImageGallery();
		break;
		case KEY_RIGHT:
			selectNextImageGallery();
		break;
		case KEY_CHANNEL_UP	:
			if(isAmino){
				VideoDisplay.SetPIG(1,4,261,431);
			}
		break;
		case KEY_CHANNEL_DOWN:
			if(isAmino){
				hideIWIPTVFrame();
				VideoDisplay.FullScreen(true);
				VideoDisplay.SetPIGScale(1);
			}
		break;
		case KEY_ENTER:
			alert(keycode);
		break;
		case KEY_TELETEXT:
			alert(keycode);
		break;
		case KEY_TV:
			alert(keycode);
		break;
		case KEY_SERVICE:
			alert(keycode);
		break;
		case KEY_BACK:
			alert(keycode);
		break;
		case KEY_SCROLL_UP:
			alert(keycode);
		break;
		case KEY_SCROLL_DOWN:
			alert(keycode);
		break;
		case KEY_SCROLL_LEFT:
			alert(keycode);
		break;
		case KEY_SCROLL_RIGHT:
			alert(keycode);
		break;
		case KEY_REW	:
			alert(keycode);
		break;
		case KEY_FFD:
			alert(keycode);
		break;
		case KEY_STOP:
			if(isAmino){
					try{
						AVMedia.Kill();
						VideoDisplay.SetPIG(false);
						VideoDisplay.FullScreen(false);
						
						showIWIPTVFrame();
						showSlideShow();
						
					}
					catch(e) {
		            	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		            	alert("Failed to start video:\n" + msg);
		                return;
		            }
				}
		break;
		case KEY_PLAY_PAUSE:
				if(isAmino){
					try{
						AVMedia.Pause();
					}
					catch(e) {
		            	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		            	alert("Failed to start video:\n" + msg);
		                return;
		            }
				}
		break;
		case KEY_PLAY:
				if(isAmino){
					try{
						if(AVMedia.GetPos()>0){
							AVMedia.Continue();
							//Continue is only for rtsp streams...
						}
						else{
						
						//VideoDisplay.SetPIG(bool state,int scale,int x,int y) 
						//Turns the PIG display on (state =1) or off (state=0), and sets up the scale and position using 
						//the remaining parameters. Returns 0 on success and 1 on error. Valid scale values are: 
						// 1 - Full screen. 
						// 2 - Half screen. 
						// 4 - Quarter screen. 
						
							hideSlideShow();
							
							AVMedia.Play('src=igmp://239.255.1.100:5500');
							
							VideoDisplay.SetPIG(1,2,260,431);
							
						}
					}
					catch(e) {
		            	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		            	alert("Failed to start video:\n" + msg);
		                return;
		            }
				}
			break;
		case KEY_PAUSE:
			if(isAmino){
					try{
						AVMedia.Pause();
					}
					catch(e) {
		            	var msg = (typeof e == "string") ? e : ((e.message) ? e.message : "Unknown Error");
		            	alert("Failed to start video:\n" + msg);
		                return;
		            }
				}
		break;
		case KEY_0:
			alert(keycode);
		break;
		case KEY_1:
			currentViewer = setCurrentViewer(1);
			getCurrentViewer();
		break;
		case KEY_2:
			currentViewer = setCurrentViewer(2);
			getCurrentViewer();
		break;
		case KEY_3:
		break;
		case KEY_4:
			alert(keycode);
		break;
		case KEY_5:
			selectNextImageGallery();
		break;
		case KEY_6:
			alert(keycode);
		break;
		case KEY_7:
			alert(keycode);
		break;
		case KEY_8:
			alert(keycode);
		break;
		case KEY_9:
			alert(keycode);
		break;
		default: return false;
		
		
	}
	
	return true;	
}

/*
Sends the user to the specified url but stops any running video first
*/
function gotoUrl(gotoPage)
{
	stb_tvKill();
	document.location.href = gotoPage;
}


// Default Boot Event - Only called once
function default_boot_event(){
//called by port/pc.js
}

// Main Create
function create_event(){	
//called by port/pc.js
}

/**
* Improved finder
* To reference any frame, iframe, form, input, image or anchor (but not link) by its name, or
* positioned element by its id in the current document - it can optionally scan through any frameset
* structure to find it (searching in frames that have not loaded will cause an error):
*/
function findObj( oName, oFrame, oDoc ) {
	if( !oDoc ) {if( oFrame ) { oDoc = oFrame.document; } else { oDoc = window.document; } }
	if( oDoc[oName] ) { return oDoc[oName]; } if( oDoc.all && oDoc.all[oName] ) { return oDoc.all[oName]; }
	if( oDoc.getElementById && oDoc.getElementById(oName) ) { return oDoc.getElementById(oName); }
	for( var x = 0; x < oDoc.forms.length; x++ ) { if( oDoc.forms[x][oName] ) { return oDoc.forms[x][oName]; } }
	for( var x = 0; x < oDoc.anchors.length; x++ ) { if( oDoc.anchors[x].name == oName ) { return oDoc.anchors[x]; } }
	for( var x = 0; document.layers && x < oDoc.layers.length; x++ ) {
	var theOb = findObj( oName, null, oDoc.layers[x].document ); if( theOb ) { return theOb; } }
	if( !oFrame && window[oName] ) { return window[oName]; } if( oFrame && oFrame[oName] ) { return oFrame[oName]; }
	for( var x = 0; oFrame && oFrame.frames && x < oFrame.frames.length; x++ ) {
	var theOb = findObj( oName, oFrame.frames[x], oFrame.frames[x].document ); if( theOb ) { return theOb; } }
	return null;
}







