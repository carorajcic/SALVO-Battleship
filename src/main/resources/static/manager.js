$(function() {
  // muestra texto en la salida
  function showOutput(text) {
    $("#output").text(text);
  }
  // carga y muestra el JSON de los Players
  function loadData() {
    $.get("/rest/players")
    .done(function(data) {
      showOutput(JSON.stringify(data, null, 2));
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }
  // controla al usuario cuando clickea agregar persona
  function addPlayer() {
    var name = $("#email").val();
    if (name) {
      postPlayer(name);
    }
  }
  // codea para publicar un nuevo jugador usando AJAX
  // si funciona, recarga y muestra la data subida desde el servidor
  function postPlayer(userName) {
    $.post({
      headers: {
          'Content-Type': 'application/json'
      },
      dataType: "text",
      url: "/rest/players",
      data: JSON.stringify({ "userName": userName })
    })
    .done(function( ) {
      showOutput( "Saved -- reloading");
      loadData();
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }
  $("#add_player").on("click", addPlayer);
  loadData();
});