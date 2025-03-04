
package acme.entities.review;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review extends AbstractEntity {

	// Serialization -------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes
	@Mandatory
	@ValidShortText
	@Automapped
	private String				name;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				postedAt;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				subject;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10, integer = 2, fraction = 2)
	@Automapped
	private double				score;

	@Optional
	@Automapped
	private Boolean				isRecommended;

	// Relations ---------------------------------------------

}
