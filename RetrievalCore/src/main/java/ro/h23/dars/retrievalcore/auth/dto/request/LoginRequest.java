package ro.h23.dars.retrievalcore.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
	@NotBlank
  	private String username;

	@NotBlank
	private String password;

}
