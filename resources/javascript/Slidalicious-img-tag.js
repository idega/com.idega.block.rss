// Slidalicious - Slideshow, v. 0.5.0
// Copyright (c) 2006 Flurin Egger, DigitPaint B.V. (http://www.digitpaint.nl)
// ======================================= //

var Slidalicious = Class.create();
Slidalicious.prototype = {
      initialize: function(){
            if(arguments.length > 0) this.element = arguments[0];
            if(arguments.length > 1) { this.setOptions(arguments[1]); }
            else { this.setOptions({}); }
            
            this.display_time = this.options.display_time;
            this.transition_time = this.options.transition_time;
            this.transition_effect = this.options.transition_effect;
            this.images = this.options.images;
            this.links = this.options.links;
            this.width = this.options.width;
            this.height = this.options.height;
      },
  setOptions: function(options) {
    this.options = Object.extend({
      transition_effect: Effect.Appear, // transition effect
      transition_time: 1.0,           // seconds
      display_time: 5.0                                // seconds
    }, options || {});
  },  
      play: function(){
            if(this.images.length < 1) return this.error("Please specify at least one image.")
            if(!this.initialized) this.initializeSlideshow(); 
            if(!this.initialized) return false;
            this.position = 1;
            
            this.setSlide(this.current_slide,0);
            this.setupSlideshowSize();
            
            if(this.images.length > 1){
                  this.setSlide(this.next_slide,this.position);
                  this.timer = 0;
                  if(!this.interval) this.interval = setInterval(this.loop.bind(this),1000);
            } 
      },
      loop: function(){
            this.timer += 1;
            if(this.timer >= this.display_time){
                  this.transition();
                  this.timer = 0;
            }
      },
      transition: function(){
            this.transition_effect(this.next_slide,{
                        duration:this.transition_time,
                        afterFinish: this.prepareNext.bind(this)});
      },
      prepareNext: function(){
            this.position += 1;
            if(this.position >= this.images.length) this.position = 0;
            
            var swap = this.current_slide;
            this.current_slide = this.next_slide;
            this.next_slide = swap;
            
            this.setupBottomSlide(this.current_slide);
            this.setupTopSlide(this.next_slide);
            this.setSlide(this.next_slide,this.position);
      },
      setSlide: function(slide,position){
            slide.image.src = this.images[position];
            if(this.links.length > position && this.links[position]){
                  slide.href = this.links[position];
            } else {
                  slide.href = "#";
            }
      },
      setupSlideshowSize: function(){
            var dims = Element.getDimensions(this.current_slide.image);
            var d = {};
            if(!this.width) this.width = dims.width;
            if(!this.height) this.height = dims.height;
            d.width = this.width + "px";
            d.height = this.height + "px";
            if(dims.width > 0 && dims.height > 0) Element.setStyle(this.element,d);
      },
      initializeSlideshow: function(){
            this.element = $(this.element);
            if(!this.element) return this.error("Could not find element '"+this.element+"' ");
            
            this.element.style.position = "relative";
            
            this.next_slide = document.createElement("A");
            this.current_slide = document.createElement("A");

            this.next_slide.image =  document.createElement("IMG");
            this.current_slide.image = document.createElement("IMG");                     
            
            this.setupTopSlide(this.next_slide);
            this.setupBottomSlide(this.current_slide);
            
            this.next_slide.appendChild(this.next_slide.image);
            this.current_slide.appendChild(this.current_slide.image);
                                    
            this.element.appendChild(this.current_slide);
            this.element.appendChild(this.next_slide);                        
            this.initialized = true;
      },
      setupTopSlide: function(element){
            this.setupSlide(element);
            element.style.zIndex = 1;
            element.style.display = "none";
      },
      setupBottomSlide: function(element){
            this.setupSlide(element);
            element.style.zIndex = 0;
            element.style.display = "";                     
      },
      setupSlide: function(element){
            element.style.border = 0;
            if(element.image) element.image.style.border = 0;
            element.style.position = "absolute";
            element.style.top = 0;
            element.style.left = 0;
      },
      error: function(msg){
            alert("Slidalicious Error: " + msg);
            return false;
      }
}