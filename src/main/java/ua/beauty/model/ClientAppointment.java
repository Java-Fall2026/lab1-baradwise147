package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Mutable entity representing an appointment booking for a client.
 * Remains a class because {@code state} is mutable (✎).
 * Key (🔑❓): composite key of {@code master}, {@code appointmentDate}, and {@code startTime}.
 */
public final class ClientAppointment {

    private final BeautyClient client;
    private final Master master;
    private final BeautyService service;
    private final LocalDate appointmentDate;
    private final LocalTime startTime;
    private final LocalDateTime createdAt;
    private AppointmentState state;

    public ClientAppointment(BeautyClient client, Master master, BeautyService service,
                             LocalDate appointmentDate, LocalTime startTime, AppointmentState state) {
        BeautyUtils.validateAppointment(client, master, service, appointmentDate, startTime, state);

        this.client = client;
        this.master = master;
        this.service = service;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.createdAt = LocalDateTime.now();

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

    public AppointmentState getState() {
        return state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Updates the status of the appointment.
     *
     * @param state new state, must not be null
     */
    public final void setState(AppointmentState state) {
        BeautyUtils.requireNotNull(state, "Appointment state");
        this.state = state;
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
                ", state=" + state +
                ", createdAt=" + createdAt +
                '}';
    }
}
