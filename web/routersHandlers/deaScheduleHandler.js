// Import controller
var controller = require("../controllers/deaScheduleController");

exports.getRoutesHandler = function(express){
	var routesHandler = express.Router();

    routesHandler.route('/dea_schedule/:id')
		.get(controller.getScheduleByIdDea)
		.put(controller.editScheduleByIdDea)		

	return routesHandler;  
}