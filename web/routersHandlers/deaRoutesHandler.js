// Import controller
var controller = require("../controllers/deaLocationController");

exports.getRoutesHandler = function(express){
	var routesHandler = express.Router();

	routesHandler.route('/deaList')
		.get(controller.findAll);

	routesHandler.route('/deaLocation')
		.get(controller.findLocations);

	return routesHandler;  
}