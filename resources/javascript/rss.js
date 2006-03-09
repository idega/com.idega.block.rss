	// -------------------------------------------------------------------
// IdegaWeb RSS block helper functions by Eirikur S. Hrafnsson, eiki@idega.is
// -------------------------------------------------------------------

var hiddenLayerId;

function showRSSContentLayer(contentLayerID,contentString) { 
	setRSSContentLayerId(contentLayerID);
	var content = findObj(contentLayerID);
	content.innerHTML = contentString+'<'+'a href="#" id="focusLink" name="focusLink" >&nbsp;<'+'/a>';
	content.style.visibility='visible';
	findObj('focusLink').focus();
}

function hideRSSContentLayer() { 
	if(hiddenLayerId){
		findObj(hiddenLayerId).style.visibility='hidden';
	}
}

function setRSSContentLayerId(contentLayerId){
	hiddenLayerId = contentLayerId;
}