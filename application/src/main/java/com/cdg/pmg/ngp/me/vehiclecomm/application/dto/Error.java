package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** This class used to define all Error Fields */
@Getter
@Setter
@Builder
public class Error implements Serializable {
  @Serial private static final long serialVersionUID = 7816610691460267759L;

  private String message;
  private String code;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ErrorDetail data;

  /** The type Error detail. */
  @Getter
  @Setter
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ErrorDetail implements Serializable {
    @Serial private static final long serialVersionUID = -9102851913623515134L;

    private List<String> validation;
    private transient List<FieldError> fields;

    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
      private String name;
      private List<String> messages;
    }
  }
}
