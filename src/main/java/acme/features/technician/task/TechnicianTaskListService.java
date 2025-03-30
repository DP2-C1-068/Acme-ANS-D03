
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status;
		Integer maintenanceRecordId;
		MaintenanceRecord maintenanceRecord = null;

		maintenanceRecordId = super.getRequest().hasData("maintenanceRecordId") ? super.getRequest().getData("maintenanceRecordId", int.class) : null;

		if (maintenanceRecordId != null) {
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
			status = maintenanceRecord != null && //
				(!maintenanceRecord.isDraftMode() || //
					super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician()));

		} else
			status = true;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {

		Collection<Task> tasks;
		Integer maintenanceRecordId;
		boolean mine;
		int technicianId;

		maintenanceRecordId = super.getRequest().hasData("maintenanceRecordId") ? super.getRequest().getData("maintenanceRecordId", int.class) : null;
		mine = super.getRequest().hasData("mine");
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (maintenanceRecordId != null)
			tasks = this.repository.findTasksByMasterId(maintenanceRecordId);
		else if (mine)
			tasks = this.repository.findTasksByTechnicianId(technicianId);
		else
			tasks = this.repository.findPublishedTasks();

		super.getBuffer().addData(tasks);

	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "type", "priority", "description");
		super.addPayload(dataset, task, "estimatedDurationHours", "draftMode");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Task> tasks) {
		Integer maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		boolean showCreate = false;
		boolean mine;

		maintenanceRecordId = super.getRequest().hasData("maintenanceRecordId") ?//
			super.getRequest().getData("maintenanceRecordId", int.class) : null;
		mine = super.getRequest().hasData("mine");

		if (maintenanceRecordId != null) {
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
			showCreate = maintenanceRecord != null && maintenanceRecord.isDraftMode() //
				&& super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());
		} else if (mine)
			showCreate = true;

		super.getResponse().addGlobal("maintenanceRecordId", maintenanceRecordId);
		super.getResponse().addGlobal("mine", mine);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
