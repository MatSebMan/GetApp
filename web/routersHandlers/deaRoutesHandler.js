// Import controller
var controller = require("../controllers/deaLocationController");

exports.getRoutesHandler = function(express){
	var routesHandler = express.Router();

	routesHandler.route('/deaList')
		.get(controller.findAll);

	return routesHandler;  
}