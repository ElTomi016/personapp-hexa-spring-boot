package co.edu.javeriana.as.personapp.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Modelo de dominio Estudio: une Person + Profession con fecha y universidad. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Study {
	@NonNull
	private Person person;
	@NonNull
	private Profession profession;
	private LocalDate graduationDate;
	private String universityName;
}
