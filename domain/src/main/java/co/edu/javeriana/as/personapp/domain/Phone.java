package co.edu.javeriana.as.personapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Modelo de dominio Telefono; owner = Person duena. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
	@NonNull
	private String number;
	@NonNull
	private String company;
	@NonNull
	private Person owner;
}
