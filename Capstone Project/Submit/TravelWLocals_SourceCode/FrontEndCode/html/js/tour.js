var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var slides = $(".mySlides");
  var dots = $(".dot");
  slides.hide();
  $('.navigation .dot').removeClass('active');
  $('.navigation .dot').eq(n - 1).addClass('active');

  $('.slideshow-container > div').eq(n - 1).addClass('active');
  $('.slideshow-container > div').eq(n - 1).show();
}

$(document).ready(function() {
  $('.slideshow-container > div').eq(0).css('display', 'block');
  $('.navigation .dot').eq(0).addClass('active');
  console.log($('#text-calendar').val());
});

//show datepicker
$(function() {
  $('input.calendar').pignoseCalendar({
      format: 'DD-MM-YYYY' // date format string. (2017-02-02)
  });
  
  $('#btn-calendar').click(function() {
      $('.calendar').trigger('click');
  });
});

//get day in calendar
$(document).mouseup(function(e) 
{
    if(e.button == 0){
        var container = $(".pignose-calendar-body");
        if (container.is(e.target) || container.has(e.target).length === 1 ) 
        {
          setTimeout(function() {
            $( ".ButtonText-xoSvS" ).text($('#text-calendar').val());
          }, 100);
          
        }
    }
});

// hiện thị bảng thêm bớt người đi du lịch
$(document).mouseup(function(e) 
{
    if(e.button == 0){
      var container = $(".viewNumberTravel");
      var button = $("#Button-1bHL6");
      if (!container.is(e.target) && container.has(e.target).length === 0 && !button.is(e.target) && button.has(e.target).length === 0 ) {
        $(".viewNumberTravel").hide();
      }
    }
});

$(document).on('click', '.DropdownButton-15Fja', function() {
    if($(".viewNumberTravel").css('display') === 'none') {
      $(".viewNumberTravel").show();
    } else {
      $(".viewNumberTravel").hide();
    }
});




