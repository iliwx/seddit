package ir.ac.kntu.backend.DTO;

import ir.ac.kntu.backend.model.VotableType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {

    @NotNull
    private VotableType type;
    @NotNull
    private int value;
}
