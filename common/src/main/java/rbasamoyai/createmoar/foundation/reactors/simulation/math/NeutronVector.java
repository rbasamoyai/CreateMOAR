package rbasamoyai.createmoar.foundation.reactors.simulation.math;

public class NeutronVector {

    public double thermal;
    public double fast;

    public NeutronVector() {
        this.thermal = this.fast = 0;
    }

    public NeutronVector(double thermal, double fast) {
        this.thermal = thermal;
        this.fast = fast;
    }

    public NeutronVector set(NeutronVector other) {
        this.thermal = other.thermal;
        this.fast = other.fast;
        return this;
    }

    public NeutronVector set(double thermal, double fast) {
        this.thermal = thermal;
        this.fast = fast;
        return this;
    }

    public NeutronVector zero() {
        this.thermal = this.fast = 0;
        return this;
    }

    public NeutronVector add(NeutronVector v) {
        this.thermal += v.thermal;
        this.fast += v.fast;
        return this;
    }

    public NeutronVector add(double thermal, double fast) {
        this.thermal += thermal;
        this.fast += fast;
        return this;
    }

    public NeutronVector fma(double a, NeutronVector v) {
        this.thermal += a * v.thermal;
        this.fast += a * v.fast;
        return this;
    }

    public NeutronVector sub(NeutronVector v) {
        this.thermal -= v.thermal;
        this.fast -= v.fast;
        return this;
    }

    public NeutronVector sub(double thermal, double fast) {
        this.thermal -= thermal;
        this.fast -= fast;
        return this;
    }

    public NeutronVector scale(double m) {
        this.thermal *= m;
        this.fast *= m;
        return this;
    }

    public NeutronVector mult(NeutronVector v) {
        this.thermal *= v.thermal;
        this.fast *= v.fast;
        return this;
    }

    public NeutronVector mult(double thermal, double fast) {
        this.thermal *= thermal;
        this.fast *= fast;
        return this;
    }

    public double dot(NeutronVector v) { return this.thermal * v.thermal + this.fast * v.fast; }

    public NeutronVector copy() { return new NeutronVector(this.thermal, this.fast); }

}
