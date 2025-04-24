package Model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "reviews")
public class Review extends DistributedEntity {
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    private int rate;

    @Column(columnDefinition = "NVARCHAR(MAX)") // Ensures comment is stored as NVARCHAR
    private String comment;
}
