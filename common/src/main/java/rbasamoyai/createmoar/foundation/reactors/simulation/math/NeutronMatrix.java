package rbasamoyai.createmoar.foundation.reactors.simulation.math;

public class NeutronMatrix {

    public double thermalToThermal;
	public double thermalToFast;
	public double fastToThermal;
	public double fastToFast;

	public NeutronMatrix() {
		this.zero();
	}

	public NeutronMatrix(double thermalToThermal, double thermalToFast, double fastToThermal, double fastToFast) {
		this.thermalToThermal = thermalToThermal;
		this.thermalToFast = thermalToFast;
		this.fastToThermal = fastToThermal;
		this.fastToFast = fastToFast;
	}

	public NeutronMatrix zero() {
		this.thermalToThermal = this.thermalToFast = this.fastToThermal = this.fastToFast = 0;
		return this;
	}

	public NeutronMatrix add(NeutronMatrix m) {
        this.thermalToThermal += m.thermalToThermal;
        this.thermalToFast += m.thermalToFast;
        this.fastToThermal += m.fastToThermal;
        this.fastToFast += m.fastToFast;
		return this;
	}

	public NeutronMatrix scale(double d) {
        this.thermalToThermal *= d;
        this.thermalToFast *= d;
        this.fastToThermal *= d;
        this.fastToFast *= d;
		return this;
	}

	public NeutronVector transform(NeutronVector v) {
		double t = v.thermal * this.thermalToThermal + v.fast * this.fastToThermal;
		double f = v.thermal * this.thermalToFast + v.fast * this.fastToFast;
		v.thermal = t;
		v.fast = f;
		return v;
	}

	public NeutronVector transformInverse(NeutronVector v) {
		double t = v.thermal * this.fastToFast - v.fast * this.fastToThermal;
		double f = -v.thermal * this.thermalToFast + v.fast * this.thermalToThermal;
		double det = this.determinant();
		v.thermal = t / det;
		v.fast = f / det;
		return v;
	}

	public NeutronMatrix transpose(NeutronMatrix target) {
		target.thermalToThermal = this.thermalToThermal;
		double a = this.fastToThermal;
		target.fastToThermal = this.thermalToFast;
		target.thermalToFast = a;
		target.fastToFast = this.fastToFast;
		return target;
	}

	public NeutronMatrix transform(NeutronMatrix m, NeutronMatrix target) {
		double t1 = this.thermalToThermal * m.thermalToThermal + this.fastToThermal * m.thermalToFast;
		double f1 = this.thermalToFast * m.thermalToThermal + this.fastToFast * m.thermalToFast;
		double t2 = this.thermalToThermal * m.fastToThermal + this.fastToThermal * m.fastToFast;
		double f2 = this.thermalToFast * m.fastToThermal + this.fastToFast * m.fastToFast;
		target.thermalToThermal = t1;
		target.thermalToFast = f1;
		target.fastToThermal = t2;
		target.fastToFast = f2;
		return target;
	}

	public double determinant() {
		return this.thermalToThermal * this.fastToFast - this.thermalToFast * this.fastToThermal;
	}

}
