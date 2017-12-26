package edu.ufl.biotext2kb.utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * This is a help class to store EntityAndPredicate csv file data
 * Each distinct row in the csv is stored as an new independent BioText2KBEntityAndPredicateDict object
 * Each column in the csv is represented as a field in BioText2KBEntityAndPredicateDict
 */
public class BioText2KBEntityAndPredicateDict implements Serializable{
    private final String instance;
    private final boolean isInUMLS;
    private final Double weight;
    private final boolean isEntity;

    public BioText2KBEntityAndPredicateDict(String instance, boolean isInUMLS, Double weight, boolean isEntity){
        this.instance = instance;
        this.isInUMLS = isInUMLS;
        this.weight = weight;
        this.isEntity = isEntity;
    }

    public String getInstance() {
        return instance;
    }

    public boolean isInUMLS() {
        return isInUMLS;
    }

    public Double getWeight() {
        return weight;
    }

    public boolean isEntity() {
        return isEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BioText2KBEntityAndPredicateDict that = (BioText2KBEntityAndPredicateDict) o;
        return isInUMLS == that.isInUMLS &&
                isEntity == that.isEntity &&
                Objects.equals(instance, that.instance) &&
                Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, isInUMLS, weight, isEntity);
    }

    @Override
    public String toString() {
        return "BioText2KBEntityAndPredicateDict{" +
                "instance='" + instance + '\'' +
                ", isInUMLS=" + isInUMLS +
                ", weight=" + weight +
                ", isEntity=" + isEntity +
                '}';
    }
}
