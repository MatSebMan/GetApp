var jwt = require('jwt-simple');
var validateUser = require('../routes/auth').validateUser;
var removeUser = require('../routes/auth').removeUser;
var getLoggedUsers = require('../routes/auth').getLoggedUsers;
var debugMode = false;

module.exports = function(req, res, next) {
  function getCookie(cname, cookie) {
    var name = cname + "=";
    var ca = cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return;
  };

  function debugOrRedirect(status, message, res, redirectUrl) {
    if(debugMode) {
      res.status(status);
        res.json({
          "status": status,
          "message": message
        });
    } else {
      res.redirect('/');
    }
  }

  // When performing a cross domain request, you will recieve
  // a preflighted request first. This is to check if our the app
  // is safe. 
 
  // We skip the token outh for [OPTIONS] requests.
  //if(req.method == 'OPTIONS') next();
 
  var token = (req.body && req.body.access_token) || (req.query && req.query.access_token) || req.headers['x-access-token'];

  if(!token && req.headers['cookie']) {
    token = getCookie("access_token",req.headers['cookie']);
  }

  if (token) {
    try {
      var decoded = jwt.decode(token, require('../config/secret.js')());
      var key = decoded.user.username;
 
      if (decoded.exp <= Date.now()) {
        removeUser(key);
        debugOrRedirect(400, "Sesion expirada", res, '/');
        return;
      }

      let successCallback = function() {
        if ((req.url.indexOf('/app') >= 0) || (req.url.indexOf('/api') >= 0)) {
          next(); // To move to next middleware
        } else {
          debugOrRedirect(403, "Usuario no autorizado", res, '/');
          return;
        }
      }

      let failCallback = function() {
        // No user with this name exists, respond back with a 401
        debugOrRedirect(401, "Usuario no valido", res, '/');
        return;
      }
 
      // Authorize the user to see if s/he can access our resources
      validateUser(decoded.user.username, decoded.user.pass, successCallback, failCallback); // The key would be the logged in user's username
    } catch (err) {
      debugOrRedirect(500, "Oops algo salio mal", res, '/');
    }
  } else {
    debugOrRedirect(401, "Token o clave invalida", res, '/');
    return;
  }
};