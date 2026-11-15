package ua.beauty.model;

import ua.beauty.util.BeautyUtils;
import ua.common.BaseEntity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a client in the Beauty Salon.
 * Key field: {@code phone} (🔑).
 * Creation is performed via static factory {@link #of(String, String, LocalDate)}.
 */
public final class BeautyClient extends BaseEntity {

    private final String phone;
    private final String name;
    private final LocalDate birthDate;

    private BeautyClient(String phone, String name, LocalDate birthDate) {
        super();
        String normalizedPhone = BeautyUtils.normalize(phone);
        String normalizedName = BeautyUtils.normalize(name);

        BeautyUtils.validatePhone(normalizedPhone);
        BeautyUtils.validateName(normalizedName, "Client");
        BeautyUtils.validateBirthDate(birthDate);

        this.phone = normalizedPhone;
        this.name = normalizedName;
        this.birthDate = birthDate;
    }

    public static BeautyClient of(String phone, String name, LocalDate birthDate) {
        return new BeautyClient(phone, name, birthDate);
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeautyClient that = (BeautyClient) o;
        return Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

    @Override
    public String toString() {
        return "BeautyClient{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
