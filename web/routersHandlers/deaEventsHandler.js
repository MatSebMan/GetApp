// Import controller
var controller = require("../controllers/deaEventsController");

exports.getRoutesHandler = function(express){
	var routesHandler = express.Router();

	routesHandler.route('/deas/events')
		.get(controller.findAll);

	return routesHandler;  
}