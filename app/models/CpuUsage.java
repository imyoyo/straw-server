package models;

import java.util.ArrayList;
import java.util.List;

public class CpuUsage {
	private final double total;
	private final List<Double> individual;

	public CpuUsage(double total, List<Double> individual) {
		this.total = total;
		this.individual = new ArrayList<>(individual);
	}

	public double getTotal() {
		return total;
	}

	public List<Double> getIndividual() {
		return new ArrayList<>(individual);
	}
}
