package co.com.bancolombia.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// @NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String email;
    private String name;
    private int age;
    private String status;

    public User() {
    }
}
