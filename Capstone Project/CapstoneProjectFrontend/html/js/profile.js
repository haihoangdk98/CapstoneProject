$(document).ready(function(){
    
    $('.button-group > button').on('click', function() {
        $('.button-group > button').removeClass('active');
        $(this).addClass('active');
    });
    $('input[name=search]').focus(function () {
            $('.search .fillter').show();
        
    });
    $(document).mouseup(function (e) {
        if (!$('.search').is(e.target) && !$('.fillter').is(e.target)
        && $('.search').has(e.target).length == 0
        && $('.fillter').has(e.target).length == 0) {
            $('.fillter').hide();
        }
    });
    
    //read more and read less
    $('.moreless-button').click(function () {
        $('.moretext').slideToggle();
        if ($('.moreless-button').text() == "Read more") {
            $(this).text("Read less")
        } else {
            $(this).text("Read more")
        }
    });

    //show and hide comment
    size_li = $(".listReview li").length;
    $('#showLess').hide();
    x = 3;
    $('.listReview li:lt(' + x + ')').show();
    $('#loadMore').click(function () {
        x = (x + 3 <= size_li) ? x + 3 : size_li;
        $('.listReview li:lt(' + x + ')').show(500);
        if(x > 3){
            $('#showLess').show();
        }
        if(x == size_li){
            $('#loadMore').hide();
        }
    });
    $('#showLess').click(function () {
        x = (x - 3 < 3) ? 3 : x - 3;
        $('.listReview li').not(':lt(' + x + ')').hide(500);
        if (x <= 3) {
            $('#showLess').hide();
        }if(x < size_li){
            $('#loadMore').show();
        }
    });

    // click sign up and close sign up form

    $('.signup').click(function () {
        $('.signUpForm').show();
    });

    $('.closeLogin').click(function () {
        $('.signUpForm,.loginForm').hide();
    });
    // click login and close login form

    $('.login').click(function () {
        $('.loginForm').show();
    });

    //mouse click outside .content-login form
    $(document).mouseup(function(e) 
    {
        if(e.button == 0){
            var container = $(".content-login");
            // if the target of the click isn't the container nor a descendant of the container
            if (!container.is(e.target) && container.has(e.target).length === 0 ) 
            {
                $('.signUpForm,.loginForm').hide();
            }
        }
    });
    //.sheet redirect
    // toi ve lam not
    $(document).on('click', '.sheet', function(event) { 
        // event.preventDefault(); 
        $("#sheetTrigger").trigger('click');
        console.log('why run?');
    });

    // hide and show password in login
    $(document).on('click', '.fa-eye', function() {
        
        var input = $("#pass_log_id");
        input.attr('type') === 'password' ? input.attr('type','text') : input.attr('type','password')
    });

   
});
