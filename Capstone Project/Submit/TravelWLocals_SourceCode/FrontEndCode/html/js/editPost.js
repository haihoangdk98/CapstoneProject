$(document).on('click', '#activitiesAdd', function(event) { 
    var $newdiv1 = $('<div class="activitiesInput"><div class="coverContent"><div class="brief">Brief<input type="index"></div><div class="detail">Detail</div><textarea rows="4" cols="50" type="textarea"></textarea><button type="button" class="btn btn-danger">Delete</button></div></div>');
    $('.activities').append($newdiv1);
    console.log($newdiv1);
});


$(document).on('click', '#includeService', function(event) { 
    var $newdiv1 = $('<div class="dropdownCoverSelect"><input type="text" class="dropdown-select"><button type="button" class="btn btn-danger btn-add-service">Delete</button></div>');
    $('.include-service').append($newdiv1);
    console.log();
});
