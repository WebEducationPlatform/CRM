(function($){
	$.fn.imgCheckbox = function(options){

		var settings = $.extend({
			width: 'auto',
			height: 'auto',
			textColor: 'white',
			overlayBgColor: 'black',
			overlayOpacity: '0.4',
			round: false,
			animation: false,
			animationDuration: 300,
			animationArray: ['scale']
		}, options);

		var methods = {
			toggleCheckbox: function(){
				var figure = $(this)
				var input = $('input[type="checkbox"]', figure)
				var overlay = $('.checkbox-overlay', figure)
				var caption = $('figcaption', figure)
				var animation = false

				if(settings.animation === true){
					animation = settings.animationArray[0]
				}else if($.inArray(settings.animation, settings.animationArray) > -1){
					animation = settings.animation
				}

				if(animation){
					figure.css('animation-duration', settings.animationDuration + 'ms').addClass('imgcheckbox-animate-' + animation)
					if(figure.css('animation-name') != 'none'){
						figure.one('animationend', function(){
							figure.removeClass('imgcheckbox-animate-' + animation)
						})
					}else{
						console.error('To use animations include the file "animation.imgcheckbox.min.css" in the head tag!')
					}
				}

				if(input.prop('checked')){
					input.prop('checked', false)
					animation ? overlay.fadeOut(settings.animationDuration) : overlay.hide()
					animation ? caption.fadeOut(settings.animationDuration) : caption.hide()
				}else{
					input.prop('checked', true)
					animation ? overlay.fadeIn(settings.animationDuration) : overlay.show()
					animation ? caption.fadeIn(settings.animationDuration) : caption.show()
				}
			}
		};

		return this.each(function(i,e){
			var figure = $(e)
			var content = $('div.figure-content', figure)
			var figcaption = $('figcaption', figure)
			var label = $('label', figure)
			var checkbox = $('input[type="checkbox"]', figure)
			var overlay = $('<div class="checkbox-overlay"></div>').appendTo(figure)

			// Skip the element if it does not contain the desired tags
			if(figure.length < 1 || content.length < 1 || figcaption.length < 1){return true}

			// Styles
			if(settings.round){
				figure.css('border-radius', '50%')
			}

			figure
				.css('display', 'inline-flex')
				.css('justify-content', 'center')
				.css('align-items', 'center')
				.css('position', 'relative')
				.css('overflow', 'hidden')
				.css('cursor', 'pointer')
				.css('width', settings.width)
				.css('height', settings.height)
			content
				.css('display', 'inline-flex')
				.css('width', settings.width)
				.css('height', settings.height)
			overlay
				.css('position', 'absolute')
				.css('top', 0)
				.css('left', 0)
				.css('right', 0)
				.css('bottom', 0)
				.css('opacity', settings.overlayOpacity)
				.css('background-color', settings.overlayBgColor)
				.css('z-index', 1)
			figcaption
				.css('position', 'absolute')
				.css('top', 0)
				.css('left', 0)
				.css('right', 0)
				.css('bottom', 0)
				.css('display', 'inline-flex')
				.css('justify-content', 'center')
				.css('align-items', 'center')
				.css('color', settings.textColor)
				.css('z-index', 2)

			// Hide elements
			label.length > 0 ? label.css('display', 'none') : checkbox.css('display', 'none')
			if(!checkbox.prop('checked')){
				overlay.hide()
				figcaption.hide()
			}

			// Actions
			figure.bind('click.imgCheckbox', methods.toggleCheckbox)

		})

	};
})(jQuery);
