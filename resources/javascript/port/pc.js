// iTTV Portal 4.7.0 - >> PC CLIENT PORT <<
// Cross Platform Porting Layer JS API 1.02
//
// Copyright (c) 2004 TANDBERG Television.
// All Rights Are Reserved.
//

// PC Input Control Codes

var last_tune=-1;
var last_tune_url = "";

var KEY_PLAY		= 112; 	// 'p'
var KEY_PAUSE		= 111;	// 'o'
var KEY_INFO		= 105;	// 'i' 
var KEY_PLAY_PAUSE  = 108;	// 'l' 
var KEY_UP		    = 119;	// 'w' 
var KEY_DOWN		= 122;	// 'z' 
var KEY_LEFT		= 97;	// 'a'
var KEY_RIGHT		= 115;	// 's'
var KEY_ENTER		= 101;	// 'e'
var KEY_MENU		= 109;	// 'm'
var KEY_RED			= 110;  // 'n'
var KEY_GREEN		= 98;	// 'b'
var KEY_YELLOW		= 118;	// 'v'
var KEY_BLUE		= 99;	// 'c'
var KEY_TELETEXT	= 0;	
var KEY_TV			= 116;	// 't'
var KEY_SERVICE		= 0;	
var KEY_BACK		= 106;  // 'j'	
var KEY_SCROLL_UP	= 101;
var KEY_SCROLL_DOWN	= 100;
var KEY_SCROLL_LEFT	= 106;  // 'j'
var KEY_SCROLL_RIGHT= 107;  // 'k'
var KEY_REW			= 117;
var KEY_FFD			= 113;
var KEY_STOP		= 104;
var KEY_OK			= 13;	// 'enter' key
var KEY_0			= 48;
var KEY_1			= 49;
var KEY_2			= 50;
var KEY_3			= 51;
var KEY_4			= 52;
var KEY_5			= 53;
var KEY_6			= 54;
var KEY_7			= 55;
var KEY_8			= 56;
var KEY_9			= 57;
var KEY_CHANNEL_UP	= 114;	// 'r'
var KEY_CHANNEL_DOWN= 102;	// 'f'

var ipAddress ="";

function stb_setIpAddress()
{
	ipAddress = "127.0.0.1";
}

function stb_getIpAddress()
{
	return ipAddress;

}
function stb_VideoHide()
{}
//var KEY_CHANNEL_UP	= 0;	// 'r'
//var KEY_CHANNEL_DOWN= 0;	// 'f'

	// TELEVISION CONTROL -----------------------------------------------------------------------------------
	// av control methods  - Television Control
function stb_tvSetup() {}
function stb_tvPlay(tune_url) { /*alert(tune_url);*/ }
function stb_tvKill() { /*alert(tune_url);*/ }
function stb_tvPlayPids() {}
function stb_tvSetAudioPid() {}
function stb_tvSetVideoPid() {}
function stb_tvSetTeletextPid() {}
function stb_tvSetSubtitlePid() {}
function stb_tvGetPidList() {}
function stb_tvStop() {}
function stb_tvResume() {}

function stb_setTvMode()
{
	NTSCmode=0;
}
	// av picture in graphics
function stb_tvPigSetup() {}
function stb_tvPigOn() {}
function stb_tvPigOff() {}
function stb_tvEventHandler() {}
function stb_tvFullscreen() {}


	// VIDEO ON DEMAND CONTROL -----------------------------------------------------------------------------------
// vod control methods - Video On Demand Control
function stb_vodSetup(start_url) {}
function stb_vodPlay() {}
function stb_vodPause() {}
function stb_vodStop() {}
function stb_vodRewind(speed) {}
function stb_vodForward(speed) {}
function stb_vodSetPos(pos) {}
function stb_vodGetState() {}
function stb_vodGetPos() {return 25;}
function stb_vodPlaytime() {return 100;}
function stb_vodEventHandler() {}


	// DISPLAY CONTROL -----------------------------------------------------------------------------------
	// stb settings set/get methods - STB Settings
	// tv display mode
function stb_getTvDisplay() {}
function stb_setTvDisplayNormal() {}
function stb_setTvDisplayWidescreen() {}
	// tv display format mode
function stb_getTvFormat() {}
function stb_setTvFormatNone() {}
function stb_setTvFormatLetterbox() {}
function stb_setTvFormatCentrecutout() {}
	// tv macrovision copy protection
function stb_getTvMacrovision() {}
function stb_setTvMacrovisionOn() {}
function stb_setTvMacrovisionOff() {}	

	// tv transparency osd
function stb_getTvTransparency() {}
function stb_setTvTransparency(level) {}
	// tv colourkey osd
function stb_getTvOsdColourKey() {} 
function stb_setTvOsdColourKey(colorkey) {}

	// AUDIO CONTROL -----------------------------------------------------------------------------------
	// tv audio output
function stb_getTvAudio() {}
function stb_setTvAudioDigital() {}
function stb_setTvAudioAnalog() {}
	// tv audio volume
function stb_setTvAudioVolume() {}
function stb_getTvAudioVolume() {}
	// tv audio language
function stb_getTvAudioLangPri1() {}
function stb_getTvAudioLangPri2() {}
function stb_setTvAudioLangPri1(lang_id) {}
function stb_setTvAudioLangPri2(lang_id) {}
		
	// SUBTITLES CONTROL -----------------------------------------------------------------------------------
	// tv subtitles language
function stb_getTvSubtitles() {}
function stb_setTvSubtitlesOn() {}
function stb_setTvSubtitlesOff() {}
function stb_getTvSubtitlesLangPri1() {}
function stb_getTvSubtitlesLangPri2() {}
function stb_setTvSubtitlesLangPri1(lang_id) {}
function stb_setTvSubtitlesLangPri2(lang_id) {}

	// TELETEXT CONTROL -----------------------------------------------------------------------------------
	// tv teletext
function stb_getTvTeletext() {}
function stb_setTvTeletextOn() {}
function stb_setTvTeletextOff() {}

	// EVENT HANDLERS -----------------------------------------------------------------------------------
	// event handlers - Keys & Startup
function stb_eventBoot()
{
	default_boot_event();
	stb_inputSetup();
	create_event();
	bootComplete = 1;
}

function stb_inputSetup()
{
	document.onkeypress = stb_eventInput;
}

function stb_eventInput(event) 
{
	var keycode;
	suppressDefault = false;
	try {
		keycode = event.which;
	} catch(e) {}
	try {
		keycode = window.event.keyCode;
	} catch (e) {}
	key_event(keycode);
	return !suppressDefault;
}

function stb_setInputMenuMode() {}
function stb_setInputTextMode() {}

	// MISCELLANEOUS -----------------------------------------------------------------------------------
	// tv debugging
var DEBUG = 1;
function stb_err(e) 
{
	if (DEBUG == 1)
	{
		alert(e);
	}
}

    // DEPRICATED METHODS -----------------------------------------------------------------------------------
	// do not use in future code - code below here will be removed soon!
function boot_event()
{
	stb_eventBoot();
	stb_tvPlay(si_getSvc_tune_url(epg_viewable[currentChannel]));
}

function tune(tune_url)
{
	virtual_current_channel=-1;
	//alert("Tuning\nlast_tune = " + last_tune + "\ncurrentChannelPosition = " + currentChannelPosition);
	if(last_tune!=currentChannelPosition)
	{	
		if(check_service_rights(currentChannelPosition) ==true ) 
		{
			set_span_text("NotSubscribed","");
			stb_tvPlay(tune_url);
		}
		else
		{
			set_span_text("NotSubscribed",'<span class="NotSubscribed" >'+censored[get_ui_language_id()]+'</span>');
			stb_tvStop();
		}
		last_tune=currentChannelPosition;
		last_tune_url = tune_url;
	}
}

function stb_getInfo()
{
	return "<p>STB : PC</p>";
}
    
