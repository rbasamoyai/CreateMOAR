package rbasamoyai.createmoar.foundation.reactors.elements.modification;

import java.util.function.BiConsumer;

import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;

public class MutableElementView implements NuclearElementView {

    private final NuclearElement element;
    private final double volume;
    private final BiConsumer<NuclearElement, Double> productCons;

    private double amount;

    public MutableElementView(NuclearElement element, double amount, double volume, BiConsumer<NuclearElement, Double> productCons) {
        this.element = element;
        this.amount = amount;
        this.volume = volume;
        this.productCons = productCons;
    }

    @Override public NuclearElement getElement() { return this.element; }

    @Override public double getAmount() { return this.amount; }
    @Override public void setAmount(double amount) { this.amount = amount; }

    @Override public double getVolume() { return this.volume; }

    @Override public boolean isMutable() { return true; }

    @Override public void saveChanges() { this.productCons.accept(this.element, this.amount); }

    @Override
    public NuclearElementView createProductView(NuclearElement element) {
        return new MutableElementView(element, 0, this.volume, this.productCons);
    }

    public static MutableElementView fromVolume(NuclearElement element, double volume, BiConsumer<NuclearElement, Double> productCons) {
        return new MutableElementView(element, element.getAmountFromVolume(volume), volume, productCons);
    }

}
