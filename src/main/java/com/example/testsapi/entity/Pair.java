package com.example.testsapi.entity;

import java.util.Objects;

public class Pair <P, Q>{
    private P member1;
    private Q member2;

    public Pair(P member1, Q member2) {
        this.member1 = member1;
        this.member2 = member2;
    }

    public P getMember1() {
        return member1;
    }

    public void setMember1(P member1) {
        this.member1 = member1;
    }

    public Q getMember2() {
        return member2;
    }

    public void setMember2(Q member2) {
        this.member2 = member2;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "member1=" + member1 +
                ", member2=" + member2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(member1, pair.member1) && Objects.equals(member2, pair.member2);
    }
}
