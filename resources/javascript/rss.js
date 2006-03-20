	// -------------------------------------------------------------------
// IdegaWeb RSS block helper functions by Eirikur S. Hrafnsson, eiki@idega.is
// -------------------------------------------------------------------

var hiddenLayerId;
var layerHidden = true;

function showRSSContentLayer(contentLayerID) { 
	setRSSContentLayerId(contentLayerID);
	var content = findObj(contentLayerID);
	//content.innerHTML = content.innerHTML + '<'+'a href="javascript:hideRSSContentLayer();" style="visibility:hidden;" id="focusLink" name="focusLink" >&nbsp;<'+'/a>';
	counter = 0;
	
	var l=document.createElement('a'); 
	l.setAttribute('href', 'javascript:hideRSSContentLayer();window.focus();this.parentNode.removeChild(this);'); 
	l.setAttribute('onclick', 'javascript:hideRSSContentLayer();window.focus();this.parentNode.removeChild(this);'); 
	l.setAttribute('style', 'visibility:hidden;'); 
	l.setAttribute('id', 'focusLink'); 
	l.setAttribute('name', 'focusLink'); 
	
	content.appendChild(l); 
	
	content.style.visibility='visible';	
	layerHidden = false;
	
	//todo laga findObj
	theLink = $('focusLink');
	
	theLink.focus();
	
	
}

function hideRSSContentLayer() { 
	if(hiddenLayerId){
		findObj(hiddenLayerId).style.visibility='hidden';
	}
	layerHidden = true;
}

function setRSSContentLayerId(contentLayerId){
	hiddenLayerId = contentLayerId;
}

function toggleRSSContentLayer(contentLayerID){
	if(contentLayerID!=hiddenLayerId){
		hideRSSContentLayer();
		layerHidden=true;
	}
	
	setRSSContentLayerId(contentLayerID);
		
	
	if(layerHidden){
		showRSSContentLayer(contentLayerID);
	}
	else{
		hideRSSContentLayer();
	}	
}
