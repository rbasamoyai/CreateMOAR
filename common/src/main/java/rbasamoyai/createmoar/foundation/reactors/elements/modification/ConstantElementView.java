package rbasamoyai.createmoar.foundation.reactors.elements.modification;

import java.util.function.BiConsumer;

import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;

public record ConstantElementView(NuclearElement element, double amount, double volume,
                                  BiConsumer<NuclearElement, Double> productCons) implements NuclearElementView {

    @Override public NuclearElement getElement() { return this.element; }
    @Override public double getAmount() { return this.amount; }
    @Override public void setAmount(double amount) {}
    @Override public double getVolume() { return this.volume; }

    @Override public boolean isMutable() { return false; }
    @Override public void saveChanges() {}

    @Override
    public NuclearElementView createProductView(NuclearElement element) {
        return new MutableElementView(element, 0, this.volume, this.productCons);
    }

    public static ConstantElementView fromVolume(NuclearElement element, double volume, BiConsumer<NuclearElement, Double> productCons) {
        return new ConstantElementView(element, element.getAmountFromVolume(volume), volume, productCons);
    }

}
