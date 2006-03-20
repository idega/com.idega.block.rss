// iTTV Portal 4.7.0 - >> AMINO PORT <<
// Cross Platform Porting Layer JS API 1.02
//
// Copyright (c) 2004-2005 TANDBERG Television.
// All Rights Are Reserved.
//

// Amino Input Control Codes
var KEY_INFO		= 0;
var KEY_UP			= 38;
var KEY_DOWN		= 40;
var KEY_LEFT		= 37;
var KEY_RIGHT		= 39;
var KEY_CHANNEL_UP	= 8492;	//new
var KEY_CHANNEL_DOWN= 8494;	//new
var KEY_ENTER		= 0;
var KEY_MENU		= 8516;
var KEY_RED			= 8512;
var KEY_GREEN		= 8513;
var KEY_YELLOW		= 8514;	
var KEY_BLUE		= 8515;	
var KEY_TELETEXT	= 0;
var KEY_TV			= 0;
var KEY_SERVICE		= 0;
var KEY_BACK		= 8511;
var KEY_SCROLL_UP	= 8525;
var KEY_SCROLL_DOWN	= 8526;
var KEY_SCROLL_LEFT	= 8511;
var KEY_SCROLL_RIGHT= 8508;
var KEY_REW		= 8500;
var KEY_FFD		= 8502;
var KEY_STOP		= 8501;
var KEY_PLAY_PAUSE	= 0;//static number mapped for the remote fix
var KEY_PLAY		= 8499; //new
var KEY_PAUSE		= 8504;	//new
var KEY_OK			= 13;	
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
var internal_tune="";
var last_tune=-1;
var last_vod;
var ipAddress ="";
var playtime = 0;
function stb_setIpAddress()
{
ipAddress = "127.0.0.1";
return;

	try { ipAddress= ASTB.GetDHCPAddress(); }
	catch (ex) { }
}
function stb_getIpAddress()
{
        return ipAddress;
}

	// TELEVISION CONTROL -----------------------------------------------------------------------------------
	// av control methods  - Television Control
function stb_tvSetup() {}

function stb_tvPlay(tune_url) 
{
	if(tune_url=="")
		tune_url=internal_tune; 
	internal_tune=tune_url;
	try {
		AVMedia.Play("src=igmp://"+tune_url);
	} catch (e) { stb_err("stb_tvPLAY "+e); }
}

function stb_tvPlayPids() {}
function stb_tvSetAudioPid() {}
function stb_tvSetVideoPid() {}
function stb_tvSetTeletextPid() {}
function stb_tvSetSubtitlePid() {}
function stb_tvGetPidList() {}
function stb_tvKill()
{
	AVMedia.Kill();
}
function stb_tvStop()
{
  try {
	AVMedia.Stop();
    	//AVMedia.Kill();
	//VideoDisplay.FullScreen(0);
	last_tune=-1;
  } catch (ex) {stb_err(ex);}
}
function stb_tvResume() {AVMedia.Continue();}

	// av picture in graphics
function stb_tvPigSetup(x,y,width,height) 
{
	try{
	var scale =1;
	if(width==360) scale = 2;
	else scale = 4;
	VideoDisplay.SetPIG(1,scale,x,y);
	}catch(ex){}
	
}
function stb_tvPigOn() 
{
}
function stb_tvPigOff() 
{
	VideoDisplay.SetPIG(0);
}
function stb_tvFullscreen(x)
{
	VideoDisplay.FullScreen(x);
}

function stb_tvEventHandler() {}


	// VIDEO ON DEMAND CONTROL -----------------------------------------------------------------------------------
// vod control methods - Video On Demand Control
function stb_vodSetup(vod_url) {
  last_vod = vod_url;
}
function stb_vodPlay() {
  try {
	AVMedia.Play(last_vod);} catch (ex) {stb_err("stv_vodPlay "+ex);}
}
function stb_vodPause() {
  try {AVMedia.Pause();} catch (ex) {stb_err("stv_vodStop "+ex);}
}
function stb_vodStop() {
  try {AVMedia.Stop();} catch (ex) {stb_err(ex);}
}
function stb_vodRewind(speed) {
  try {AVMedia.SetSpeed(-6);} catch (ex) {stb_err(ex);}
}
function stb_vodForward(speed) {
  try {AVMedia.SetSpeed(6);} catch (ex) {stb_err(ex);}
}
function stb_vodSetPos(pos) {}
function stb_vodGetState() {}
function stb_vodSetPlaytime(x) {playtime=200;}
function stb_vodPlaytime() {return playtime;}
function stb_vodGetPos() {
  try {
    return AVMedia.GetPos(); // returns number of secs
  } catch (ex) {
    stb_err(ex);
    return 0;
  }
}
function stb_vodEventHandler() {}


	// DISPLAY CONTROL -----------------------------------------------------------------------------------
	// stb settings set/get methods - STB Settings
	// tv display mode
function stb_getTvDisplay() { }
function stb_setTvDisplayNormal() {alert("NORMAL");VideoDisplay.SetAspect(1);}
function stb_setTvDisplayWidescreen() {alert("WIDESCREEN");VideoDisplay.SetAspect(3);}
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

function stb_setTvTransparency(level) 
{
	try {
		VideoDisplay.SetAlphaLevel((level*100)/255);
	} catch (e) { }
}

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
function stb_getTvSubtitles()
{
	try {
		VideoDisplay.GetSubtitles();
	} catch (ex) {stb_err(ex);}
}
function stb_setTvSubtitlesOn()
{
	try {
		VideoDisplay.SetSubtitles(true,true);
	} catch (ex) {stb_err(ex);}
}
function stb_setTvSubtitlesOff()
{
	try {
		VideoDisplay.SetSubtitles(false,true);
	} catch (ex) {stb_err(ex);}
}
function stb_getTvSubtitlesLangPri1()
{
	try {
		AVMedia.GetPrimarySubtitleLanguage();
	} catch (ex) {stb_err(ex);}
}
function stb_getTvSubtitlesLangPri2()
{
	try {
		AVMedia.GetSecondarySubtitleLanguage();
	} catch (ex) {stb_err(ex);}
}
function stb_setTvSubtitlesLangPri1(lang_id)
{
	try {
		AVMedia.SetPrimarySubtitleLanguage(lang_id);
	} catch (ex) {stb_err(ex);}
}
function stb_setTvSubtitlesLangPri2(lang_id)
{
	try {
		AVMedia.SetSecondarySubtitleLanguage(lang_id);
	} catch (ex) {stb_err(ex);}
}

	// TELETEXT CONTROL -----------------------------------------------------------------------------------
	// tv teletext
function stb_getTvTeletext() {}
function stb_setTvTeletextOn() {}
function stb_setTvTeletextOff() {}

	// EVENT HANDLERS -----------------------------------------------------------------------------------
	// event handlers - Keys & Startup
function stb_eventBoot()
{
	try {
	default_boot_event();
	stb_inputSetup();
	VideoDisplay.SetChromaKey(0x00,0x00,0x00);
	stb_setTvTransparency(210);
	VideoDisplay.RetainAlphaLevel(true);
	VideoDisplay.RetainMouseState(true);
	ASTB.SetMouseState(false);
	//idegaweb changed
	VideoDisplay.UnloadVideo(true);
	//
	
	VideoDisplay.DefaultUnloadVideo(false);
	ASTB.DefaultKeys(true); // false turn off opera keys
	ASTB.WithChannels(false);
	//idegaweb change stb_tvPlay(si_getSvc_tune_url(epg_viewable[currentChannel]));
	create_event();
	} catch (e) { }
}

function stb_VideoHide(v)
{
	AVMedia.VideoHide(v);
}

function stb_inputSetup()
{
	document.onkeypress = stb_eventInput;
}

function stb_eventInput(event) 
{
	try {
		key_event(event.keyCode);
	} catch (e) { alert("EVENT INPUT "+e); }
	return true;
}

function stb_setInputMenuMode() {}
function stb_setInputTextMode() {}

	// MISCELLANEOUS -----------------------------------------------------------------------------------
	// tv debugging
function stb_err(e) 
{ 
  alert(e); 
  //alert(e.fileName + " : " + e.lineNumber + "\n" + e.name + " " + e.message + "\n" + e.stack);
}

    // DEPRICATED METHODS -----------------------------------------------------------------------------------
	// do not use in future code - code below here will be removed soon!
function boot_event()
{
	stb_eventBoot();
}

    
function tune(tune_url)
{
	try{
	virtual_current_channel=-1;
	if(last_tune!=currentChannelPosition)
	{	if(check_service_rights(currentChannelPosition) == true) 
		{
			set_span_text("NotSubscribed","");
			stb_tvPlay(tune_url);
		}
		else
		{
			set_span_text("NotSubscribed",'<span class="NotSubscribed" >'+not_subscribed[get_ui_language_id()]+'</span>');
			stb_tvStop();
		}
		last_tune=currentChannelPosition;
	}
	}catch(ex){alert("TUNE "+ex)}
}
    
