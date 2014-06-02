$(document).ready( function () {
  $('.show-modal').click( function (e) {
    e.preventDefault();
    $('#modal-image').attr('src', $(this).attr('href'));
    $('#fail-error-msg').text($(this).attr('data-msg'));
    $('#error-modal').modal('toggle');
  });
});