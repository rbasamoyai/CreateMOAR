package rbasamoyai.createmoar.foundation.reactors.elements.modification;

import rbasamoyai.createmoar.foundation.reactors.elements.NuclearElement;

public interface NuclearElementView {

    NuclearElement getElement();

    double getAmount();
    void setAmount(double amount);

    double getVolume();

    boolean isMutable();
    void saveChanges();

    /**
     * Creates a view for a child product, such as decay or fission products.
     * The child view should be mutable, should have the same volume as the
     * parent view, and should be initialized to 0.
     *
     * @param element the child element
     * @return a new child element view
     */
    NuclearElementView createProductView(NuclearElement element);

    default double getTemperature() { return 293; } // TODO: flesh out and make non-default; placeholder

}
