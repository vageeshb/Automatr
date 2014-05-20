$(document).ready( function () {
  $('.show-modal').click( function (e) {
    e.preventDefault();
    $('#modal-image').attr('src', $(this).attr('href'));
    $('#error-modal').modal('toggle');
  });
});