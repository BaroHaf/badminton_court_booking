package Model;

import Model.Constant.Rank;
import Model.Constant.Role;
import Model.Constant.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "owner_request")
public class OwnerReq extends DistributedEntity{
    @Column(unique = true, nullable = false)
    private String user_name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String phone;
    private String token;
    private boolean isVerified;
    @Enumerated(EnumType.STRING)
    private Status status;
    public OwnerReq(String user_name, String email, String password,Boolean isVerified, String phone, String token, Status status) {
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.token = token;
        this.status = status;
        this.isVerified = isVerified;
    }
}
