package pl.switalski.wiki.java.hibernate.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name = "METEOROLOGICAL_OBSERVATORIES")
public class Observatory extends AbstractEntity {
	
	@Column(name = "name")
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "observatory", cascade = CascadeType.ALL)
	private List<Measurement> measurements;
	
	public Observatory() {
	}
	
	public Observatory(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Measurement> getMeasurements() {
		return measurements;
	}
	
	public void setMeasurements(List<Measurement> measurements) {
		this.measurements = measurements;
	}

	public void addMeasurement(Measurement measurement) {
		if (this.measurements == null) {
			this.measurements = new ArrayList<Measurement>();
		}
		this.measurements.add(measurement);
		measurement.setObservatory(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toStringExclude(this, "measurements");
	}

}
