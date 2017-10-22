// Import controller
var controller = require("../controllers/deaLocationController");

exports.getRoutesHandler = function(express){
	var routesHandler = express.Router();

	routesHandler.route('/dea')
		.get(controller.findAll)
		.post(controller.create);

	routesHandler.route('/dea/:id')
		.get(controller.findById)
		.put(controller.edit)
		.delete(controller.delete);

	routesHandler.route('/deaLocation')
		.get(controller.findNearestDeas);

	return routesHandler;  
}