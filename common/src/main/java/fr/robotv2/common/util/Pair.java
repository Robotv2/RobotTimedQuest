package fr.robotv2.common.util;

import java.util.Objects;

public class Pair<A, B> {

    public final A fst;
    public final B snd;

    public Pair(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Pair)) {
            return false;
        }

        if(this == object) {
            return true;
        }

        final Pair<?, ?> otherPair = (Pair<?, ?>) object;
        return Objects.equals(fst, otherPair.fst) &&
                Objects.equals(snd, otherPair.snd);
    }
}
