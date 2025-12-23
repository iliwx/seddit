package ir.ac.kntu.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.ac.kntu.backend.error.IErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public abstract class ErrorDTO {

    @Getter
    @RequiredArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GeneralRs {
        private final String code;
        private final String description;
        private List<FieldDTO> fields;

        // ------------------------------

        public GeneralRs(String code) {
            this(code, null);
        }

        public GeneralRs(IErrorCode errorCode) {
            this(errorCode.getCode());
        }

        public GeneralRs(IErrorCode errorCode, String description) {
            this(errorCode.getCode(), description);
        }

        // ------------------------------

        public GeneralRs addField(FieldDTO field) {
            if (fields == null) {
                fields = new ArrayList<>();
            }
            fields.add(field);
            return this;
        }

        public GeneralRs addFields(List<FieldDTO> list) {
            if (fields == null) {
                fields = new ArrayList<>();
            }
            fields.addAll(list);
            return this;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldDTO {
        private final String field;
        private final String code;

        public FieldDTO(String field) {
            this(field, null);
        }
    }
}
