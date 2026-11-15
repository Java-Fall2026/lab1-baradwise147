package ua.beauty.model;

import ua.beauty.util.BeautyUtils;
import ua.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entity representing an appointment booking for a client.
 * Key (🔑❓): composite key of {@code master}, {@code appointmentDate}, and {@code startTime}.
 * Justification: A master cannot handle more than one appointment at the exact same start time on a given date.
 * Has a mutable field: {@code state} (✎).
 */
public final class ClientAppointment extends BaseEntity {

    private final BeautyClient client;
    private final Master master;
    private final BeautyService service;
    private final LocalDate appointmentDate;
    private final LocalTime startTime;
    private String state;

    public ClientAppointment(BeautyClient client, Master master, BeautyService service,
                             LocalDate appointmentDate, LocalTime startTime, String state) {
        super();
        BeautyUtils.validateAppointment(client, master, service, appointmentDate, startTime);

        this.client = client;
        this.master = master;
        this.service = service;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;

        // Setter is called to avoid duplicate validation logic
        setState(state);
    }

    public BeautyClient getClient() {
        return client;
    }

    public Master getMaster() {
        return master;
    }

    public BeautyService getService() {
        return service;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public String getState() {
        return state;
    }

    /**
     * Updates the status of the appointment.
     * Marked final because it is invoked from the constructor.
     *
     * @param state new state, must be one of CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
     */
    public final void setState(String state) {
        String normalizedState = BeautyUtils.normalizeUpper(state);
        BeautyUtils.validateAppointmentState(normalizedState);
        this.state = normalizedState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientAppointment that = (ClientAppointment) o;
        return Objects.equals(master, that.master) &&
                Objects.equals(appointmentDate, that.appointmentDate) &&
                Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(master, appointmentDate, startTime);
    }

    @Override
    public String toString() {
        return "ClientAppointment{" +
                "client=" + client +
                ", master=" + master +
                ", service=" + service +
                ", appointmentDate=" + appointmentDate +
                ", startTime=" + startTime +
                ", state='" + state + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
